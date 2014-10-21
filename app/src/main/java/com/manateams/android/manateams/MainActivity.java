package com.manateams.android.manateams;

<<<<<<< HEAD
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
=======
>>>>>>> c25f4df630c5e910fa6c5255000c5701a54f6365
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    /* Solely used for login. If the user is already logged in, forwards to showing the grades. */

    boolean loggingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD
        getActionBar().hide();
        setupViews();
        loggingIn = false;
=======
        //Define Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

>>>>>>> c25f4df630c5e910fa6c5255000c5701a54f6365
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
<<<<<<< HEAD
        switch (item.getItemId()) {

        }
=======
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
>>>>>>> c25f4df630c5e910fa6c5255000c5701a54f6365
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
