package com.manateams.android.manateams.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.manateams.android.manateams.AssignmentActivity;
import com.manateams.android.manateams.R;
import com.manateams.android.manateams.asynctask.AssignmentLoadTask;
import com.manateams.android.manateams.asynctask.AsyncTaskCompleteListener;
import com.manateams.android.manateams.asynctask.CourseLoadTask;
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.android.manateams.views.CourseAdapter;
import com.manateams.android.manateams.views.RecyclerItemClickListener;
import com.manateams.scraper.data.ClassGrades;
import com.manateams.scraper.data.Course;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

public class CourseFragment extends Fragment implements AsyncTaskCompleteListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView coursesList;
    private TextView lastUpdatedText;

    private Course[] courses;
    private DataManager dataManager;
    private CourseAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    public boolean allowAssignmentLoad;
    private ShowcaseView showcaseView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_courses, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) /*called after layout created */{
        super.onActivityCreated(savedInstanceState);
        dataManager = new DataManager(getActivity());
        courses = dataManager.getCourseGrades();
        setupViews();
    }

    private void setupViews() {
        lastUpdatedText = (TextView) getActivity().findViewById(R.id.text_lastupdated);
        // Set relative time for last updated
        PrettyTime p = new PrettyTime();
        lastUpdatedText.setText("Last updated " + p.format(new Date(dataManager.getOverallGradesLastUpdated())) + " - pull down to refresh");

        coursesList = (RecyclerView) getActivity().findViewById(R.id.list_courses);
        coursesList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        coursesList.setItemAnimator(new DefaultItemAnimator());
        // Set the grade cards
        adapter = new CourseAdapter(getActivity(), courses);
        coursesList.setAdapter(adapter);
        coursesList.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(dataManager.getClassGrades(dataManager.getCourseGrades()[position].courseId) != null) {
                            startAssignmentActivity(position);
                        } else {
                            loadAssignmentsForCourse(position);
                        }
                    }
                })
        );
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.app_primary, R.color.app_accent, R.color.pink,R.color.red);
        swipeRefreshLayout.setEnabled(true);
        if(dataManager.isFirstTimeViewingGrades()){
            allowAssignmentLoad = false;
             showcaseView = new ShowcaseView.Builder(getActivity())
                    .setTarget(new ViewTarget(getView().findViewById(R.id.list_courses)))
                    .setContentTitle(getResources().getString(R.string.grades_intro_title))
                    .setContentText(getResources().getString(R.string.grades_intro_content))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .hideOnTouchOutside()
                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                        @Override
                        public void onShowcaseViewHide(ShowcaseView showcaseView) {

                        }

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                            allowAssignmentLoad = true;
                        }

                        @Override
                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                        }
                    })
                    .build();
            dataManager.setFirstTimeViewingGrades(false);
        }
        else{
            allowAssignmentLoad = true;
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        lastUpdatedText = (TextView) getActivity().findViewById(R.id.text_lastupdated);
        // Set relative time for last updated
        PrettyTime p = new PrettyTime();
        lastUpdatedText.setText("Last updated " + p.format(new Date(dataManager.getOverallGradesLastUpdated())) + " - pull down to refresh");
    }
    public void restartActivity() {
        try {
            Intent intent = getActivity().getIntent();
            getActivity().overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getActivity().finish();

            getActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadAssignmentsForCourse(int position) {
        if (showcaseView != null){
            showcaseView.hide();
        }
        if (allowAssignmentLoad)
            new AssignmentLoadTask(this, getActivity(), true,true).execute(new String[] {dataManager.getUsername(), dataManager.getPassword(), dataManager.getStudentId(), String.valueOf(position),dataManager.getTEAMSuser(),dataManager.getTEAMSpass()});
    }

    @Override
    public void onCoursesLoaded(Course[] courses) {
        try{
            swipeRefreshLayout.setRefreshing(false);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(courses != null) {
            dataManager.setCourseGrades(courses);
            dataManager.setOverallGradesLastUpdated();
            for (Course c: courses){
                if (c.semesters[1].average.value != -1){
                    for (int i = c.semesters[1].cycles.length-1; i >= 0; i--){
                        if(c.semesters[1].cycles[i].average != null){
                            dataManager.addCourseDatapoint(c.semesters[1].cycles[i].average,c.courseId);
                            break;
                        }
                    }
                }
                else{
                    for (int i = c.semesters[0].cycles.length-1; i >= 0; i--){
                        if(c.semesters[0].cycles[i].average != null){
                            dataManager.addCourseDatapoint(c.semesters[0].cycles[i].average,c.courseId);
                            break;
                        }
                    }
                }
            }
            restartActivity();
        }
    }

    @Override
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex) {
        dataManager.setClassGrades(grades, courses[courseIndex].courseId);
        dataManager.setClassGradesLastUpdated(courses[courseIndex].courseId);
        startAssignmentActivity(courseIndex);
    }

    public void startAssignmentActivity(int courseIndex) {
        Intent intent = new Intent(getActivity(), AssignmentActivity.class);
        intent.putExtra(Constants.EXTRA_COURSEINDEX, courseIndex);
        intent.putExtra(Constants.EXTRA_COURSETITLE, courses[courseIndex].title);
        intent.putExtra(Constants.EXTRA_COURSEID, courses[courseIndex].courseId);
        startActivity(intent);

    }

    @Override
    public void onRefresh() {
        new CourseLoadTask(this, getActivity()).execute(dataManager.getUsername(), dataManager.getPassword(), dataManager.getStudentId(),dataManager.getTEAMSuser(),dataManager.getTEAMSpass());
    }
}
