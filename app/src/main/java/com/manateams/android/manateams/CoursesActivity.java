package com.manateams.android.manateams;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.manateams.android.manateams.util.DataManager;
import com.manateams.android.manateams.views.CourseAdapter;
import com.quickhac.common.data.Course;


public class CoursesActivity extends Activity {

    private RecyclerView coursesList;
    private Course[] courses;
    private DataManager dataManager;
    private CourseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        dataManager = new DataManager(this);
        courses = dataManager.getCourseGrades();
        setupViews();
    }

    private void setupViews() {
        coursesList = (RecyclerView) findViewById(R.id.list_courses);
        coursesList.setLayoutManager(new LinearLayoutManager(this));
        coursesList.setItemAnimator(new DefaultItemAnimator());

        // Set the grade cards
        adapter = new CourseAdapter(courses);
        coursesList.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.courses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
