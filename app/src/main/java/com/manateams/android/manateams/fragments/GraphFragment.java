package com.manateams.android.manateams.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.android.manateams.util.DataPoint;
import com.quickhac.common.data.GradeValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        int index = getArguments().getInt("index");
        setupGraph(classID,index,v);
        return v;
    }

    public void setupGraph(String courseID,int index,View v) {
        LineChart l = (LineChart) v.findViewById(R.id.linechart);
        l.setDragEnabled(false);
        l.setPinchZoom(false);
        l.setDoubleTapToZoomEnabled(false);
        l.setStartAtZero(false);
        ArrayList<DataPoint> grades = new DataManager(getActivity()).getCourseDatapoints(courseID);
        if (grades != null && grades.size() > 0 ) {
            LineData dataSet = constructDataSet(grades, Constants.COLORS[index % Constants.COLORS.length]);
            l.setData(dataSet);
        }
    }

    private LineData constructDataSet(ArrayList<DataPoint> grades,String color) {
        grades = colapseValues(grades);
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for(int i = 0; i < grades.size();i++){
            Date date = new Date(grades.get(i).time);
            SimpleDateFormat format1 = new SimpleDateFormat("MM/dd");
            String formattedDate = format1.format(date);
            xVals.add(formattedDate);
            yVals.add(new Entry(grades.get(i).g.value,i));
        }
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        LineDataSet set1 = new LineDataSet(yVals, "Average Trend");
        set1.enableDashedLine(10f, 5f, 0f);
        set1.setColor(Color.parseColor(color));
        set1.setCircleColor(Color.parseColor(color));
        set1.setLineWidth(1f);
        set1.setCircleSize(4f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.parseColor(color));
        dataSets.add(set1);
        LineData dataout = new LineData(xVals,dataSets);
        return dataout;
    }

    private ArrayList<DataPoint> colapseValues(ArrayList<DataPoint> grades) {
        ArrayList<DataPoint> out = new ArrayList<DataPoint>();
        Map<String,List<DataPoint>> sortedGrades = new HashMap<String,List<DataPoint>>();
        //Add each value to a map based on date
        for(DataPoint d: grades){
            Date date = new Date(d.time);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = format1.format(date);
            if(!sortedGrades.containsKey(formattedDate)){
                sortedGrades.put(formattedDate, new ArrayList<DataPoint>());
            }
            sortedGrades.get(formattedDate).add(d);
        }
        //Choose the last value from each day to display
        for (Map.Entry<String, List<DataPoint>> entry : sortedGrades.entrySet()) {
            String key = entry.getKey();
            List<DataPoint> value = entry.getValue();
            out.add(value.get(value.size()-1));
        }
        return out;
    }
}
