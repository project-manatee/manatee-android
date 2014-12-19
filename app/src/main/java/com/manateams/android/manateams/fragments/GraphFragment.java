package com.manateams.android.manateams.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.DataManager;
import com.quickhac.common.data.GradeValue;

import java.util.ArrayList;

/**
 * Created by Ehsan on 12/18/2014.
 */
public class GraphFragment extends Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        String classID = getArguments().getString("request");
        setupGraph(classID,v);
        return v;
    }

    public void setupGraph(String courseID,View v) {
        LineChart l = (LineChart) v.findViewById(R.id.linechart);
        ArrayList<GradeValue> grades = new DataManager(getActivity()).getCourseDatapoints(courseID);
        if (grades != null && grades.size() > 0 ) {
            LineData dataSet = constructDataSet(grades);
            l.setData(dataSet);
        }
    }

    private LineData constructDataSet(ArrayList<GradeValue> grades) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for(int i = 0; i < grades.size();i++){
            xVals.add(String.valueOf(i));
            yVals.add(new Entry(grades.get(i).value,i));
        }
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        LineDataSet set1 = new LineDataSet(yVals, "Average Trend");
        set1.enableDashedLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleSize(4f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
        dataSets.add(set1);
        LineData dataout = new LineData(xVals,dataSets);
        return dataout;
    }
}
