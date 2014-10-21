package com.manateams.android.manateams;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;


public class MainActivity extends ActionBarActivity {

    /* Solely used for login. If the user is already logged in, forwards to showing the grades. */

    private boolean loggingIn;
    private RelativeLayout loginTextLayout;
    private RelativeLayout loginLoadingLayout;


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
            loginTextLayout.setVisibility(View.GONE);
            loggingIn = true;
            loginLoadingLayout.setVisibility(View.VISIBLE);
        } else {
            loginTextLayout.setVisibility(View.VISIBLE);
            loggingIn = false;
            loginLoadingLayout.setVisibility(View.GONE);
        }
    }
}
