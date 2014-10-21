package com.manateams.android.manateams;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends Activity {

    /* Solely used for login. If the user is already logged in, forwards to showing the grades. */

    boolean loggingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().hide();
        setupViews();
        loggingIn = false;
    }

    public void setupViews() {

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
        RelativeLayout loginTextLayout = (RelativeLayout) findViewById(R.id.layout_loginText);
        if (!loggingIn) {
            loginTextLayout.setVisibility(View.GONE);
            loggingIn = true;
        } else {
            loginTextLayout.setVisibility(View.VISIBLE);
            loggingIn = false;
        }
    }
}
