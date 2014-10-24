package com.manateams.android.manateams.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.views.CategoryAdapter;
import com.quickhac.common.data.ClassGrades;

public class CycleFragment extends Fragment {

    private RecyclerView categoryList;
    private ClassGrades grades;
    private CategoryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments().getString(Constants.EXTRA_GRADES) != null) {
            grades = new Gson().fromJson(getArguments().getString(Constants.EXTRA_GRADES), new TypeToken<ClassGrades>() {
            }.getType());
        } else {
            grades = null;
        }
        return inflater.inflate(R.layout.fragment_cycle, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) /*called after layout created */ {
        super.onActivityCreated(savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        categoryList = (RecyclerView) getView().findViewById(R.id.list_grades);
        categoryList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        categoryList.setItemAnimator(new DefaultItemAnimator());
        adapter = new CategoryAdapter(getActivity(), grades);
        categoryList.setAdapter(adapter);
    }

}

