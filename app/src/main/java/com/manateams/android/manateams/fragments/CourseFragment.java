package com.manateams.android.manateams.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manateams.android.manateams.AssignmentActivity;
import com.manateams.android.manateams.R;
import com.manateams.android.manateams.asynctask.AssignmentLoadTask;
import com.manateams.android.manateams.asynctask.AsyncTaskCompleteListener;
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.android.manateams.views.CourseAdapter;
import com.manateams.android.manateams.views.RecyclerItemClickListener;
import com.quickhac.common.data.ClassGrades;
import com.quickhac.common.data.Course;

public class CourseFragment extends Fragment implements AsyncTaskCompleteListener {

    private RecyclerView coursesList;
    private Course[] courses;
    private DataManager dataManager;
    private CourseAdapter adapter;

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
        coursesList = (RecyclerView) getActivity().findViewById(R.id.list_courses);
        coursesList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        coursesList.setItemAnimator(new DefaultItemAnimator());

        // Set the grade cards
        adapter = new CourseAdapter(getActivity(), courses);
        coursesList.setAdapter(adapter);
        coursesList.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        loadAssignmentsForCourse(position);
                    }
                })
        );
    }

    public void loadAssignmentsForCourse(int position) {
        new AssignmentLoadTask(this, getActivity()).execute(new String[] {dataManager.getUsername(), dataManager.getPassword(), dataManager.getStudentId(), String.valueOf(position)});
    }

    @Override
    public void onCoursesLoaded(Course[] courses) {

    }

    @Override
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex) {
        dataManager.setClassGrades(grades, courseIndex);
        Intent intent = new Intent(getActivity(), AssignmentActivity.class);
        intent.putExtra(Constants.EXTRA_COURSEINDEX, courseIndex);
        intent.putExtra(Constants.EXTRA_COURSETITLE, courses[courseIndex].title);
        startActivity(intent);
    }
}
