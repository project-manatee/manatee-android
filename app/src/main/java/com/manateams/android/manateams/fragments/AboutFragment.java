package com.manateams.android.manateams.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.android.manateams.views.CategoryAdapter;
import com.quickhac.common.data.ClassGrades;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

public class AboutFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) /*called after layout created */ {
        super.onActivityCreated(savedInstanceState);
    }
}
