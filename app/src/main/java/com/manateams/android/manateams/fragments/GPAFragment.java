package com.manateams.android.manateams.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.DataManager;
import com.quickhac.common.GPACalc;
import com.quickhac.common.data.Course;
import com.quickhac.common.util.Numeric;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GPAFragment extends Fragment {

    private LinearLayout rootLayout;

    private DataManager dataManager;
    private Course[] courses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gpa, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataManager = new DataManager(getActivity());
        courses = dataManager.getCourseGrades();
        setupViews();
        populateGPASummaries();
    }

    private void setupViews() {
        rootLayout = (LinearLayout) getActivity().findViewById(R.id.layout_root);
    }

    private void populateGPASummaries() {
        if(courses != null) {
            TextView unweightedTitle = new TextView(getActivity());
            unweightedTitle.setText(getString(R.string.text_gpa_unweighted));
            TextView unweightedText = new TextView(getActivity());
            unweightedText.setText(String.valueOf(Numeric.round(GPACalc.unweighted(courses), 3)));
            rootLayout.addView(unweightedTitle);
            rootLayout.addView(unweightedText);


            TextView weightedTitle = new TextView(getActivity());
            weightedTitle.setText(getString(R.string.text_gpa_weighted));
            TextView weightedText = new TextView(getActivity());
            weightedText.setText(String.valueOf(Numeric.round(getWeightedGPA(), 3)));
            rootLayout.addView(weightedTitle);
            rootLayout.addView(weightedText);
        }
    }

    private double getWeightedGPA() {
        SharedPreferences defaultPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        List<String> weightedClasses = new ArrayList<String>();
        Set<String> savedWeighted = defaultPrefs.getStringSet(
                "pref_weightedClasses", null);
        if (savedWeighted != null) {
            String[] weighted = savedWeighted
                    .toArray(new String[savedWeighted.size()]);
            if (weighted != null) {
                for (int i = 0; i < weighted.length; i++) {
                    weightedClasses.add(weighted[i]);
                }
            }
        }

        List<String> excludedClasses = new ArrayList<String>();
        Set<String> savedExcluded = defaultPrefs.getStringSet(
                "pref_excludedClasses", null);
        if (savedExcluded != null) {
            String[] excluded = savedExcluded
                    .toArray(new String[savedExcluded.size()]);
            if (excluded != null) {
                for (int i = 0; i < excluded.length; i++) {
                    excludedClasses.add(excluded[i]);
                }
            }
        }

        // remove excluded classes from list of weighed classes to calculate
        List<String> toWeighted = new ArrayList<String>();
        for (int i = 0; i < weightedClasses.size(); i++) {
            boolean excluded = false;
            for (int d = 0; d < excludedClasses.size(); d++) {
                if (d < excludedClasses.size()
                        && excludedClasses.get(d).equals(courses[i].title)) {
                    excluded = true;
                }
            }
            if (!excluded) {
                toWeighted.add(weightedClasses.get(i));
            }
        }
        return GPACalc.weighted(courses, toWeighted, 0);
    }
}