package com.manateams.android.manateams.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.manateams.android.manateams.asynctask.AsyncTaskCompleteListener;
import com.manateams.android.manateams.asynctask.CourseLoadTask;
import com.manateams.android.manateams.util.DataManager;
import com.quickhac.common.data.ClassGrades;
import com.quickhac.common.data.Course;
import com.quickhac.common.data.Cycle;
import com.quickhac.common.data.Semester;

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
        Course[] oldCourses = dataManager.getCourseGrades();
        dataManager.setCourseGrades(courses);
        dataManager.setOverallGradesLastUpdated();

        checkForGradeChanges(oldCourses, courses);

        //Todo Detection of current cycle
        for (Course c : courses) {
            dataManager.addCourseDatapoint(c.semesters[0].average, c.courseId);
        }
    }

    public void checkForGradeChanges(Course[] oldCourses, Course[] newCourses) {
        for (int i = 0; i < newCourses.length; i++) {
            if (i < newCourses.length) {
                Course oldCourse = oldCourses[i];
                Course newCourse = newCourses[i];
                for (int d = 0; d < oldCourse.semesters.length; d++) {
                    Semester oldSemester = oldCourse.semesters[d];
                    Semester newSemester = newCourse.semesters[d];
                    for (int k = 0; k < oldSemester.cycles.length; k++) {
                        Cycle oldCycle = oldSemester.cycles[k];
                        Cycle newCycle = newSemester.cycles[k];

                        if ((oldCycle == null || oldCycle.average == null) && (newCycle == null || newCycle.average == null)) {
                            // No change
                        } else if ((oldCycle == null || oldCycle.average == null) && (newCycle != null && newCycle.average != null)) {
                            // New grade for class
                        } else if ((oldCycle != null && oldCycle.average != null) && (newCycle == null || newCycle.average == null)) {
                            // This shouldn't happen, unless a teacher takes out a grade...
                        } else if (!oldCycle.average.toString().equals(newCycle.average.toString())) {
                            // Grade has changed
                        } else {
                            // Grade is the same
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex) {

    }
}
