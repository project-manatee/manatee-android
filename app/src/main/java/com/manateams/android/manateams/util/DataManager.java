package com.manateams.android.manateams.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quickhac.common.data.ClassGrades;
import com.quickhac.common.data.Course;
import com.quickhac.common.data.GradeValue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class DataManager {
    private Context context;
    public DataManager(Context context) {
        this.context = context;
    }

    public void setCredentials(String username, String password, String studentId) {
        setUsername(username);
        setPassword(password);
        setStudentId(studentId);
    }

    public String[] getCredentials() {
        return new String[] { getUsername(), getPassword(), getStudentId() };
    }

    public void setUsername(String username) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("username", username);
        edit.commit();
    }

    public String getUsername() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("username", null);
    }

    public void setPassword(String password) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("password", password);
        edit.commit();

    }

    public String getPassword() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("password", null);
    }

    public void setStudentId(String studentId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("studentId", studentId);
        edit.commit();
    }

    public String getStudentId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("studentId", null);
    }

    public long getOverallGradesLastUpdated() {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERENCE_LASTUPDATED, Context.MODE_PRIVATE);
        return preferences.getLong("overall", -1);
    }

    public void setOverallGradesLastUpdated() {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERENCE_LASTUPDATED, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong("overall", System.currentTimeMillis());
        edit.apply();
    }

    public long getCourseGradesLastUpdated(String courseId) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERENCE_LASTUPDATED, Context.MODE_PRIVATE);
        return preferences.getLong(courseId, -1);
    }

    public void setCourseGradesLastUpdated(String courseId) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERENCE_LASTUPDATED, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(courseId, System.currentTimeMillis());
        edit.apply();
    }

    public void setCourseGrades(Course[] courses) {
        writeToFile(Constants.FILE_COURSES, new Gson().toJson(courses));
    }

    public Course[] getCourseGrades() {
        String data = readFromFile(Constants.FILE_COURSES);
        return new Gson().fromJson(data, new TypeToken<Course[]>() {
        }.getType());
    }

    public void addCourseDatapoint(GradeValue g,String courseID){
        ArrayList<DataPoint> currentValues = getCourseDatapoints(courseID);
        if(currentValues == null){
            currentValues = new ArrayList<DataPoint>();
        }
        currentValues.add(new DataPoint(g,System.currentTimeMillis()));
        writeToFile(Constants.FILE_BASE_DATAPOINTS + courseID ,new Gson().toJson(currentValues));
    }
    public ArrayList<DataPoint> getCourseDatapoints(String courseID){
        String data = readFromFile(Constants.FILE_BASE_DATAPOINTS + courseID);
        return new Gson().fromJson(data, new TypeToken<ArrayList<DataPoint>>(){}.getType());
    }

    public void setClassGrades(ClassGrades[] grades, int courseIndex) {
        writeToFile(Constants.FILE_BASE_CLASSGRADES + String.valueOf(courseIndex), new Gson().toJson(grades));
    }

    public ClassGrades[] getClassGrades(int courseIndex) {
        String data = readFromFile(Constants.FILE_BASE_CLASSGRADES + String.valueOf(courseIndex));
        return new Gson().fromJson(data, new TypeToken<ClassGrades[]>(){}.getType());
    }

    private String readFromFile(String name) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(name);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            //Log.e("DataManager", "File not found: " + e.toString());
        } catch (IOException e) {
            //Log.e("DataManager", "Can not read file: " + e.toString());
        }
        return ret;
    }

    private void writeToFile(String name, String dataString) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(name, Context.MODE_PRIVATE));
            outputStreamWriter.write(dataString);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("DataManager", "File write failed: " + e.toString());
        }
    }
}
