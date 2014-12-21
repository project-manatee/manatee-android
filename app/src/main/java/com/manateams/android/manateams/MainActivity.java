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
    private String TEAMSuser = "";
    private String TEAMSpass = "";

    private RelativeLayout loginTextLayout;
    private RelativeLayout loginLoadingLayout;
    private TextView usernameText;
    private TextView passwordText;
    private TextView studentIdText;

    private DataManager dataManager;
    private int tries;
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
        login();
    }

    private void login() {
        tries = 0;
        if (!loggingIn) {
            if (usernameText.getText().toString().length() > 0 && passwordText.getText().toString().length() > 0) {
                loginTextLayout.setVisibility(View.GONE);
                loggingIn = true;
                loginLoadingLayout.setVisibility(View.VISIBLE);
                username = usernameText.getText().toString();
                password = passwordText.getText().toString();
                studentId = studentIdText.getText().toString();
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
            if (!username.matches("^[sS]\\d{6,8}\\d?$") && tries < 2) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Secondary Login");
                alert.setMessage("It seems that this account is accessed with two sets of credentials. If this applies to you, enter the credentials for your TEAMS account below.");
                LayoutInflater inflater = getLayoutInflater();
                final View v = inflater.inflate(R.layout.secondary_login_dialog, null);
                alert.setView(v);
                alert.setCancelable(false);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
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
                Toast.makeText(this, getString(R.string.toast_login_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex) {

    }
}
