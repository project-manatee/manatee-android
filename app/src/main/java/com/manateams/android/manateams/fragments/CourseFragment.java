package com.manateams.android.manateams.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.android.manateams.views.CourseAdapter;
import com.quickhac.common.data.Course;


/**
 * Created by Ehsan on 10/22/2014.
 */
public class CourseFragment extends Fragment {

    private RecyclerView coursesList;
    private Course[] courses;
    private DataManager dataManager;
    private CourseAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_courses, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) /*called after layout created */{
        super.onActivityCreated(savedInstanceState);
        dataManager = new DataManager(getActivity());
        courses = dataManager.getCourseGrades();
        setupViews();
    }

    private void setupViews() {
        coursesList = (RecyclerView) getActivity().findViewById(R.id.list_courses);
        coursesList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        coursesList.setItemAnimator(new DefaultItemAnimator());

        // Set the grade cards
        adapter = new CourseAdapter(courses);
        coursesList.setAdapter(adapter);
    }
}
