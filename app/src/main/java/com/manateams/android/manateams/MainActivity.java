package com.manateams.android.manateams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
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
    private String TEAMSuser = "";
    private String TEAMSpass = "";

    private RelativeLayout loginTextLayout;
    private RelativeLayout loginLoadingLayout;
    private TextView usernameText;
    private TextView passwordText;
    private TextView studentIdText;
    private Button loginButton;

    private DataManager dataManager;
    private int tries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Google Analytics stuff
        ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

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

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);

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
        tries = 0;
        dataManager.invalidateCookie();
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
                new CourseLoadTask(this, this).execute(username, password, studentId,"","");
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
        tries ++;
        if(courses != null) {
            if(!Utils.isAlarmsSet(this)) {
                Log.d("BitBitBit", "registering alarms");
                Utils.setAlarms(Constants.INTERVAL_GRADE_SCRAPE, this);
            }
            dataManager.setCourseGrades(courses);
            dataManager.setCredentials(username, password, studentId,TEAMSuser,TEAMSpass);
            dataManager.setOverallGradesLastUpdated();
            Intent intent = new Intent(this, CoursesActivity.class);
            startActivity(intent);
            finish();
        } else {
            loggingIn = false;
            dataManager.invalidateCookie();
            if (!username.matches("^[sS]\\d{6,8}\\d?$") && tries < 2) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.secondary_login_title);
                alert.setMessage( getString(R.string.secondary_login_info));
                LayoutInflater inflater = getLayoutInflater();
                final View v = inflater.inflate(R.layout.secondary_login_dialog, null);
                alert.setView(v);
                alert.setCancelable(false);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        loggingIn = true;
                        TEAMSuser = ((EditText)v.findViewById(R.id.backupusername)).getText().toString();
                         TEAMSpass = ((EditText)v.findViewById(R.id.backuppassword)).getText().toString();
                         new CourseLoadTask(MainActivity.this, getApplicationContext()).execute(username, password, studentId, TEAMSuser, TEAMSpass);
                    }
                });
                alert.show();
            }
            else{
                username = "";
                password = "";
                studentId = "";
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
                loginButton.setFocusable(false);
                loginButton.setFocusableInTouchMode(false);
                loginButton.setClickable(true);
                loginButton.setEnabled(true);
                loginTextLayout.setVisibility(View.VISIBLE);
                loginLoadingLayout.setVisibility(View.GONE);
                Toast.makeText(this, getString(R.string.toast_login_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex) {

    }
}
