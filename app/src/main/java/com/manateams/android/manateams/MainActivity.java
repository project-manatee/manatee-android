package com.manateams.android.manateams;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quickhac.common.data.Course;

import asynctask.AsyncTaskCompleteListener;
import asynctask.CourseLoadTask;


public class MainActivity extends ActionBarActivity implements AsyncTaskCompleteListener {

    Course[] courses;

    /* Solely used for login. If the user is already logged in, forwards to showing the grades. */

    private boolean loggingIn;
    private RelativeLayout loginTextLayout;
    private RelativeLayout loginLoadingLayout;
    private TextView usernameText;
    private TextView passwordText;
    private TextView studentIdText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        loggingIn = false;
        //Define Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void setupViews() {
        loginTextLayout = (RelativeLayout) findViewById(R.id.layout_loginText);
        loginLoadingLayout = (RelativeLayout) findViewById(R.id.layout_loading);
        usernameText = (TextView) findViewById(R.id.edittext_login_username);
        passwordText = (TextView) findViewById(R.id.edittext_login_password);
        studentIdText = (TextView) findViewById(R.id.edittext_login_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void onLoginClick(View v) {
        if (!loggingIn) {
            if (usernameText.getText().toString().length() > 0 && passwordText.getText().toString().length() > 0) {
                loginTextLayout.setVisibility(View.GONE);
                loggingIn = true;
                loginLoadingLayout.setVisibility(View.VISIBLE);
                new CourseLoadTask(this, this).execute(usernameText.getText().toString(), passwordText.getText().toString(), studentIdText.getText().toString());
            } else {
                Toast.makeText(this, getString(R.string.toast_fill_info), Toast.LENGTH_SHORT).show();
            }
        } else {
            loginTextLayout.setVisibility(View.VISIBLE);
            loggingIn = false;
            loginLoadingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCoursesLoaded(Course[] courses) {
        Log.d("ResultResult", String.valueOf(courses.length));
        for(int i = 0; i < courses.length; i++) {
            Log.d("ResultResult", courses[i].title);
        }
    }
}
