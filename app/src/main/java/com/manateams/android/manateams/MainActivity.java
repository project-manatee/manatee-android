package com.manateams.android.manateams;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.manateams.android.manateams.asynctask.AsyncTaskCompleteListener;
import com.manateams.android.manateams.asynctask.CourseLoadTask;
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.android.manateams.util.Utils;
import com.quickhac.common.data.ClassGrades;
import com.quickhac.common.data.Course;


public class MainActivity extends ActionBarActivity implements AsyncTaskCompleteListener {


    /* Solely used for login. If the user is already logged in, forwards to showing the grades. */

    private boolean loggingIn;

    private String username;
    private String password;
    private String studentId;

    private RelativeLayout loginTextLayout;
    private RelativeLayout loginLoadingLayout;
    private TextView usernameText;
    private TextView passwordText;
    private TextView studentIdText;
    private Button loginButton;

    private DataManager dataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loggingIn = false;
        setContentView(R.layout.activity_main);
        setupViews();
        //Define Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dataManager = new DataManager(this);
        if(dataManager.getCourseGrades() != null) {
            Intent intent = new Intent(this, CoursesActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (dataManager.getUsername() != null && dataManager.getPassword() != null && dataManager.getStudentId() != null) {
                usernameText.setText(dataManager.getUsername());
                passwordText.setText(dataManager.getPassword());
                studentIdText.setText(dataManager.getStudentId());
                login();
            }
        }
    }

    public void setupViews() {
        loginTextLayout = (RelativeLayout) findViewById(R.id.layout_loginText);
        loginLoadingLayout = (RelativeLayout) findViewById(R.id.layout_loading);
        usernameText = (TextView) findViewById(R.id.edittext_login_username);
        passwordText = (TextView) findViewById(R.id.edittext_login_password);
        studentIdText = (TextView) findViewById(R.id.edittext_login_id);
        loginButton = (Button) findViewById(R.id.button_login);
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
        return super.onOptionsItemSelected(item);
    }

    public void onLoginClick(View v) {
        login();
    }

    private void login() {
        if (!loggingIn) {
            if (usernameText.getText().toString().length() > 0 && passwordText.getText().toString().length() > 0 && studentIdText.getText().toString().length() > 0) {
                loginTextLayout.setVisibility(View.GONE);
                loggingIn = true;
                loginLoadingLayout.setVisibility(View.VISIBLE);
                username = usernameText.getText().toString();
                password = passwordText.getText().toString();
                studentId = studentIdText.getText().toString();
                // Annoying programmatic UI code
                usernameText.setFocusable(false);
                usernameText.setFocusableInTouchMode(false);
                usernameText.setClickable(false);
                usernameText.setEnabled(false);
                passwordText.setFocusable(false);
                passwordText.setFocusableInTouchMode(false);
                passwordText.setClickable(false);
                passwordText.setEnabled(false);
                studentIdText.setFocusable(false);
                studentIdText.setFocusableInTouchMode(false);
                studentIdText.setClickable(false);
                studentIdText.setEnabled(false);
                loginButton.setFocusable(false);
                loginButton.setFocusableInTouchMode(false);
                loginButton.setClickable(false);
                loginButton.setEnabled(false);
                new CourseLoadTask(this, this).execute(username, password, studentId);
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
        if(courses != null) {
            if(!Utils.isAlarmsSet(this)) {
                Log.d("BitBitBit", "registering alarms");
                Utils.setAlarms(Constants.INTERVAL_GRADE_SCRAPE, this);
            }
            dataManager.setCourseGrades(courses);
            dataManager.setCredentials(username, password, studentId);
            dataManager.setOverallGradesLastUpdated();
            Intent intent = new Intent(this, CoursesActivity.class);
            startActivity(intent);
            finish();
        } else {
            usernameText.setFocusable(true);
            usernameText.setFocusableInTouchMode(true);
            usernameText.setClickable(true);
            usernameText.setEnabled(true);
            passwordText.setFocusable(true);
            passwordText.setFocusableInTouchMode(true);
            passwordText.setClickable(true);
            passwordText.setEnabled(true);
            studentIdText.setFocusable(true);
            studentIdText.setFocusableInTouchMode(true);
            studentIdText.setClickable(true);
            studentIdText.setEnabled(true);
            loginButton.setFocusable(true);
            loginButton.setFocusableInTouchMode(true);
            loginButton.setClickable(true);
            loginButton.setEnabled(true);
            loginTextLayout.setVisibility(View.VISIBLE);
            loginLoadingLayout.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.toast_login_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex) {

    }
}
