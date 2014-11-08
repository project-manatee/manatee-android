package com.manateams.android.manateams;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.manateams.android.manateams.util.DataManager;
import com.quickhac.common.data.Course;


public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_settings);

        MultiSelectListPreference weightedPreference = (MultiSelectListPreference) findPreference("pref_weightedClasses");

        MultiSelectListPreference excludedPreference = (MultiSelectListPreference) findPreference("pref_excludedClasses");

        Course[] courses = new DataManager(this).getCourseGrades();

        CharSequence[] classes = new CharSequence[courses.length];
        for (int i = 0; i < courses.length; i++) {
            classes[i] = courses[i].title;
        }
        weightedPreference.setEntryValues(classes);
        weightedPreference.setEntries(classes);
        excludedPreference.setEntryValues(classes);
        excludedPreference.setEntries(classes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
