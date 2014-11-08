package com.manateams.android.manateams.fragments;

import android.os.Bundle;
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
            TextView titleText = new TextView(getActivity());
            titleText.setText(getString(R.string.text_gpa_unweighted));
            TextView unweightedText = new TextView(getActivity());
            unweightedText.setText(String.valueOf(Numeric.round(GPACalc.unweighted(courses), 3)));
            rootLayout.addView(titleText);
            rootLayout.addView(unweightedText);
        }
    }
}
