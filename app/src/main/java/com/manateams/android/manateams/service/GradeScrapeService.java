package com.manateams.android.manateams.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.manateams.android.manateams.CoursesActivity;
import com.manateams.android.manateams.R;
import com.manateams.android.manateams.asynctask.AssignmentLoadTask;
import com.manateams.android.manateams.asynctask.AsyncTaskCompleteListener;
import com.manateams.android.manateams.asynctask.CourseLoadTask;
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.android.manateams.util.Utils;
import com.manateams.scraper.data.ClassGrades;
import com.manateams.scraper.data.Course;
import com.manateams.scraper.data.Cycle;
import com.manateams.scraper.data.GradeValue;
import com.manateams.scraper.data.Semester;

public class GradeScrapeService extends IntentService implements AsyncTaskCompleteListener {

    private DataManager dataManager;
    private boolean isFullUpdate;

    public GradeScrapeService() {
        super("GradeScrapeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("BitBitBit", "scraping grades");
        dataManager = new DataManager(this);
        if (dataManager.getLastFullUpdate() == -1 || (System.currentTimeMillis() - dataManager.getLastFullUpdate()) > Constants.INTERVAL_UPDATE_FULL) {
            Log.d("BitBitBit", "Full update initiated");
            isFullUpdate = true;
            dataManager.setLastFullUpdate();
        } else {
            isFullUpdate = false;
        }
        if (dataManager.getUsername() != null && dataManager.getPassword() != null && dataManager.getStudentId() != null) {
            if (Utils.isInternetAvailable(this)) {
                if (isFullUpdate || Utils.isOnWifi(this) || (Math.abs(System.currentTimeMillis() - dataManager.getOverallGradesLastUpdated()) > Constants.INTERVAL_MOBILE_UPDATE_AVERAGE)) {
                    new CourseLoadTask(this, this).execute(dataManager.getUsername(), dataManager.getPassword(), dataManager.getStudentId(), dataManager.getTEAMSuser(), dataManager.getTEAMSpass());
                } else {
                    Log.d("DibDib", "Not loading averages to conserve data");
                }
                // Start recursively loading assignment grades
                if (isFullUpdate || Utils.isOnWifi(this) || (Math.abs(System.currentTimeMillis() - dataManager.getClassGradesLastUpdated(dataManager.getCourseGrades()[0].courseId)) > Constants.INTERVAL_MOBILE_UPDATE_ASSIGNMENT)) {
                    Log.d("DibDib", "Loading class grades at index 0");
                    new AssignmentLoadTask(this, this, false, isFullUpdate).execute(new String[]{dataManager.getUsername(), dataManager.getPassword(), dataManager.getStudentId(), String.valueOf(0), dataManager.getTEAMSuser(), dataManager.getTEAMSpass()});
                } else {
                    Log.d("DibDib", "Not loading class grades to conserve data");
                }
            }

        }

    }

    @Override
    public void onCoursesLoaded(Course[] courses) {
        if(courses != null) {
            Course[] oldCourses = dataManager.getCourseGrades();
            dataManager.setCourseGrades(courses);
            dataManager.setOverallGradesLastUpdated();

            checkForGradeChanges(oldCourses, courses);
            for (Course c : courses) {
                if (c.semesters[1].average.value != -1) {
                    for (int i = c.semesters[1].cycles.length - 1; i >= 0; i--) {
                        if (c.semesters[1].cycles[i].average != null) {
                            dataManager.addCourseDatapoint(c.semesters[1].cycles[i].average, c.courseId);
                            break;
                        }
                    }
                } else {
                    for (int i = c.semesters[0].cycles.length - 1; i >= 0; i--) {
                        if (c.semesters[0].cycles[i].average != null) {
                            dataManager.addCourseDatapoint(c.semesters[0].cycles[i].average, c.courseId);
                            break;
                        }
                    }

                }
            }
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
                            new AssignmentLoadTask(this, this, false,true).execute(new String[]{dataManager.getUsername(), dataManager.getPassword(), dataManager.getStudentId(), String.valueOf(i),dataManager.getTEAMSuser(),dataManager.getTEAMSpass()});
                            sendGradeChangeNotification(true, i, d, k, oldCycle.average, newCycle.average);
                        } else if ((oldCycle != null && oldCycle.average != null) && (newCycle == null || newCycle.average == null)) {
                            // This shouldn't happen, unless a teacher takes out a grade...
                        } else if (!oldCycle.average.toString().equals(newCycle.average.toString())) {
                            // Grade has changed
                            new AssignmentLoadTask(this, this, false,false).execute(new String[]{dataManager.getUsername(), dataManager.getPassword(), dataManager.getStudentId(), String.valueOf(i),dataManager.getTEAMSuser(),dataManager.getTEAMSpass()});
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
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_notification);
        builder.setColor(getResources().getColor(R.color.app_primary));
        builder.setAutoCancel(true);
        if(Build.VERSION.SDK_INT >= 20) {
            builder.setCategory(Notification.CATEGORY_SOCIAL);
            builder.setVisibility(Notification.VISIBILITY_PRIVATE);
        }
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

        //Respect user option
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(prefs.getBoolean("pref_showNotifications",true)){
            notificationManager.notify(1, builder.build());
        }
    }

    @Override
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex) {
        Course[] courses = dataManager.getCourseGrades();
        if(courses != null) {
            if(grades != null) {
                dataManager.setClassGrades(grades, courses[courseIndex].courseId);
                dataManager.setClassGradesLastUpdated(courses[courseIndex].courseId);
            }
            if (courseIndex < courses.length - 1) {
                // Recursively load the next course class grades
                Log.d("DibDib", "Loading class grades at index " + String.valueOf(courseIndex + 1));
                new AssignmentLoadTask(this, this, false,isFullUpdate).execute(new String[]{dataManager.getUsername(), dataManager.getPassword(), dataManager.getStudentId(), String.valueOf(courseIndex + 1),dataManager.getTEAMSuser(),dataManager.getTEAMSpass()});
            }
        }
    }
}
