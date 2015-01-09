package com.manateams.android.manateams;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.manateams.android.manateams.asynctask.AssignmentLoadTask;
import com.manateams.android.manateams.asynctask.AsyncTaskCompleteListener;
import com.manateams.android.manateams.fragments.CycleFragment;
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.DataManager;
import com.quickhac.common.data.ClassGrades;
import com.quickhac.common.data.Course;


public class AssignmentActivity extends ActionBarActivity implements AsyncTaskCompleteListener {

    private DataManager dataManager;
    private ClassGrades[] grades;
    private String courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        //Define Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Configure Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle(getIntent().getStringExtra(Constants.EXTRA_COURSETITLE));

        int courseIndex = getIntent().getIntExtra(Constants.EXTRA_COURSEINDEX, 0);
        dataManager = new DataManager(this);
        grades = dataManager.getClassGrades(courseIndex);

        courseID = getIntent().getStringExtra(Constants.EXTRA_COURSEID);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new Adapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextColor(getResources().getColor(R.color.white));
        tabs.setBackgroundColor(getResources().getColor(R.color.app_primary));
        tabs.setViewPager(pager);

        // Show the latest cycle
        if(grades != null && grades.length > 0) {
            int latestCycle = grades.length - 1;
            while (grades[latestCycle] == null && latestCycle > 0) {
                latestCycle--;
            }
            if (latestCycle >= 0) {
                pager.setCurrentItem(latestCycle);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.assignment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_refresh:
                int courseIndex = getIntent().getIntExtra(Constants.EXTRA_COURSEINDEX, 0);
                new AssignmentLoadTask(this, this, true,true).execute(new String[]{dataManager.getUsername(), dataManager.getPassword(), dataManager.getStudentId(), String.valueOf(courseIndex), dataManager.getTEAMSuser(), dataManager.getTEAMSpass()});
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCoursesLoaded(Course[] courses) {

    }

    @Override
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex) {
        Intent intent = new Intent(this, AssignmentActivity.class);
        intent.putExtra(Constants.EXTRA_COURSEINDEX, courseIndex);
        intent.putExtra(Constants.EXTRA_COURSETITLE, getIntent().getStringExtra(Constants.EXTRA_COURSETITLE));
        intent.putExtra(Constants.EXTRA_COURSEID,  courseID);
        startActivity(intent);
        finish();
    }

    public class Adapter extends FragmentPagerAdapter {
        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Bundle args = new Bundle();
            args.putString(Constants.EXTRA_COURSEID, courseID);
            if (grades != null && grades[i] != null) {
                args.putString(Constants.EXTRA_GRADES, new Gson().toJson(grades[i]));
            } else {
                Log.d("WATWAT", "grades null");
                args.putString(Constants.EXTRA_GRADES, null);
            }
            CycleFragment fragment = new CycleFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Cycle " + ++position;
        }

        @Override
        public int getCount() {
            //TODO Hardcoded cycle count
            return 6;
        }
    }
}


