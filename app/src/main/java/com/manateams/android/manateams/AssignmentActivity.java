package com.manateams.android.manateams;

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
import com.manateams.android.manateams.fragments.CycleFragment;
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.DataManager;
import com.quickhac.common.data.ClassGrades;


public class AssignmentActivity extends ActionBarActivity {

    private DataManager dataManager;
    private ClassGrades[] grades;

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

        int courseIndex = getIntent().getIntExtra("courseIndex", 0);
        dataManager = new DataManager(this);
        grades = dataManager.getClassGrades(courseIndex);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new Adapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextColor(getResources().getColor(R.color.white));
        tabs.setBackgroundColor(getResources().getColor(R.color.app_primary));
        tabs.setViewPager(pager);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class Adapter extends FragmentPagerAdapter {
        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt("request", i);
            // TODO change this, it's kinda hacky
            Log.d("WATWAT", "test wat");
            if (grades != null && grades[i] != null) {
                args.putString(Constants.EXTRA_GRADES, new Gson().toJson(grades[i]));
            } else if (grades == null) {
                Log.d("WATWAT", "grades null");
                args.putString(Constants.EXTRA_GRADES, null);
            } else {
                Log.d("WATWAT", "grades at index " + String.valueOf(i) + " null");
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


