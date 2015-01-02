package com.manateams.android.manateams.fragments;

/**
 * Created by Ehsan on 1/2/2015.
 */

import android.os.Bundle;
import android.preference.MultiSelectListPreference;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.DataManager;
import com.quickhac.common.data.Course;
import android.support.v4.preference.PreferenceFragment;




public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_settings);

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
