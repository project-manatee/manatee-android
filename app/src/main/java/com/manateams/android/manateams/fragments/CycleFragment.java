package com.manateams.android.manateams.fragments;

/**
 * Created by patil215 on 10/24/14.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manateams.android.manateams.R;

public class CycleFragment extends Fragment  {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cycle, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) /*called after layout created */{
        super.onActivityCreated(savedInstanceState);
    }

}

