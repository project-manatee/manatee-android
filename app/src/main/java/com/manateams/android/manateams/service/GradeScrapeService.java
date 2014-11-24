package com.manateams.android.manateams.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.manateams.android.manateams.asynctask.AsyncTaskCompleteListener;
import com.manateams.android.manateams.asynctask.CourseLoadTask;
import com.manateams.android.manateams.util.DataManager;
import com.quickhac.common.data.ClassGrades;
import com.quickhac.common.data.Course;

public class GradeScrapeService extends IntentService implements AsyncTaskCompleteListener {

    private DataManager dataManager;

    public GradeScrapeService() {
        super("GradeScrapeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("BitBitBit", "scraping grades");
        dataManager = new DataManager(this);
        if (dataManager.getUsername() != null && dataManager.getPassword() != null && dataManager.getStudentId() != null) {
            new CourseLoadTask(this, this).execute(dataManager.getUsername(), dataManager.getPassword(), dataManager.getStudentId());
        }
    }

    @Override
    public void onCoursesLoaded(Course[] courses) {
        dataManager.setCourseGrades(courses);
    }

    @Override
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex) {

    }
}
