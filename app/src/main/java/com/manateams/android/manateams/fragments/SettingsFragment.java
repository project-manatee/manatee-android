package com.manateams.android.manateams.fragments;

import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.scraper.data.Course;




public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);

        MultiSelectListPreference weightedPreference = (MultiSelectListPreference) findPreference("pref_weightedClasses");

        MultiSelectListPreference excludedPreference = (MultiSelectListPreference) findPreference("pref_excludedClasses");

        Course[] courses = new DataManager(getActivity()).getCourseGrades();

        CharSequence[] classes = new CharSequence[courses.length];
        for (int i = 0; i < courses.length; i++) {
            classes[i] = courses[i].title;
        }
        weightedPreference.setEntryValues(classes);
        weightedPreference.setEntries(classes);
        excludedPreference.setEntryValues(classes);
        excludedPreference.setEntries(classes);
    }
}
