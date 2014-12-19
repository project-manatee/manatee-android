package com.manateams.android.manateams.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.manateams.android.manateams.CoursesActivity;
import com.manateams.android.manateams.R;
import com.manateams.android.manateams.asynctask.AsyncTaskCompleteListener;
import com.manateams.android.manateams.asynctask.CourseLoadTask;
import com.manateams.android.manateams.util.DataManager;
import com.quickhac.common.data.ClassGrades;
import com.quickhac.common.data.Course;
import com.quickhac.common.data.Cycle;
import com.quickhac.common.data.GradeValue;
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
                            sendGradeChangeNotification(true, i, d, k, oldCycle.average, newCycle.average);
                        } else if ((oldCycle != null && oldCycle.average != null) && (newCycle == null || newCycle.average == null)) {
                            // This shouldn't happen, unless a teacher takes out a grade...
                        } else if (!oldCycle.average.toString().equals(newCycle.average.toString())) {
                            // Grade has changed
                            sendGradeChangeNotification(false, i, d, k, oldCycle.average, newCycle.average);
                        } else {
                            // Grades already exist but nothing changed
                        }
                    }
                }
            }
        }
    }

    public void sendGradeChangeNotification(boolean isNewGrade, int courseIndex, int semesterIndex, int cycleIndex, GradeValue oldGrade, GradeValue newGrade) {
        Course[] courses = dataManager.getCourseGrades();
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setLargeIcon(bm);


        if (!isNewGrade) {
            builder.setContentTitle("Grade changed");
            builder.setContentText("Your grade in " + courses[courseIndex].title + " has changed");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Your grade in " + courses[courseIndex].title + " has changed from a " + oldGrade.toString() + " to a " + newGrade.toString()));
        } else {
            builder.setContentTitle("New grade");
            builder.setContentText("You have a new " + newGrade.toString() + " in " + courses[courseIndex].title);
        }

        Intent resultIntent = new Intent(this, CoursesActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex) {

    }
}
