package com.manateams.android.manateams.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.scraper.TEAMSGradeParser;
import com.manateams.scraper.TEAMSGradeRetriever;
import com.manateams.scraper.data.ClassGrades;
import com.manateams.scraper.data.Course;
import com.manateams.scraper.districts.TEAMSUserType;

import java.io.IOException;

public class AssignmentLoadTask extends AsyncTask<String, String, ClassGrades[]> {

    private AsyncTaskCompleteListener callback;
    private Context context;
    private int courseIndex;
    ProgressDialog dialog;
    private boolean showDialog;
    private final boolean fullRefresh;

    public AssignmentLoadTask(AsyncTaskCompleteListener callback, Context context, boolean showDialog,boolean fullRefresh) {
        this.callback = callback;
        this.context = context;
        this.showDialog = showDialog;
        this.fullRefresh = fullRefresh;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(showDialog) {
            if (dialog == null) {
                this.dialog = new ProgressDialog(context);
                this.dialog.setMessage(context.getString(R.string.dialog_assignment_loading));
                this.dialog.setCancelable(false);
                this.dialog.show();
            }
        }
    }

    @Override
    protected ClassGrades[] doInBackground(String... params) {
        final String username = params[0];
        final String password = params[1];
        final String studentID = params[2];
        courseIndex = Integer.valueOf(params[3]);
        final String TEAMSuser = params[4];
        final String TEAMSpass = params[5];

        final DataManager dataManager = new DataManager(context);
        final TEAMSGradeRetriever retriever = new TEAMSGradeRetriever();

        try {
            // Get the user type
            final TEAMSUserType userType = retriever.getUserType(username);

            // Get the appropriate cookie
            final String cookie;
            if (Math.abs(dataManager.getCookieLastUpdated() - System.currentTimeMillis()) > Constants.INTERVAL_EXPIRE_COOKIE) {
                final String newCookie = retriever.getNewCookie(username, password, userType);
                dataManager.setCookie(newCookie);
                cookie = newCookie;
            } else {
                cookie = dataManager.getCookie();
            }

            // Get the appropriate user identification info
            final String userIdentification;
            final String newUserIdentification = retriever.getNewUserIdentification(username, password, studentID, TEAMSuser, TEAMSpass, cookie, userType);
            userIdentification = newUserIdentification;
            dataManager.setUserIdentification(newUserIdentification);

            // Get the HTML of the main page
            final String averageHTML;
            if(dataManager.getAverageHtml() == null) {
                final String newAverageHTML = retriever.getTEAMSPage("/selfserve/PSSViewReportCardsAction.do", "", cookie, userType, userIdentification);
                if(newAverageHTML != null) {
                    averageHTML = newAverageHTML;
                    dataManager.setAverageHtml(newAverageHTML);
                } else {
                    return null;
                }
            } else {
                averageHTML = dataManager.getAverageHtml();
            }

            // Get individual course grades
            final Course[] courses = dataManager.getCourseGrades();
            final long lastUpdated = dataManager.getClassGradesLastUpdated(courses[courseIndex].courseId);
            // If the first time loading this class or manual refresh, load all grades, otherwise load only current cycle to conserve data
            if (lastUpdated == -1 || fullRefresh) {
                final ClassGrades[] grades = new ClassGrades[6];
                for (int i = 0; i < grades.length; i++) {
                    grades[i] = retriever.getCycleClassGrades(courses[courseIndex], i, averageHTML, cookie, userType, userIdentification);
                }
                return grades;
            } else {
                final ClassGrades[] grades = dataManager.getClassGrades(courses[courseIndex].courseId);
                if (grades != null && grades.length > 0) {
                    int latestCycle = grades.length - 1;
                    while (grades[latestCycle] == null && latestCycle > 0) {
                        latestCycle--;
                    }
                    if (latestCycle >= 0) {
                        grades[latestCycle] = retriever.getCycleClassGrades(courses[courseIndex], latestCycle, averageHTML, cookie, userType, userIdentification);
                        return grades;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            new DataManager(context).invalidateCookie();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ClassGrades[] grades) {
        if(showDialog) {
            dialog.dismiss();
        }
        callback.onClassGradesLoaded(grades, courseIndex);
    }
}
