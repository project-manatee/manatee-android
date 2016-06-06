package com.manateams.android.manateams.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.scraper.TEAMSGradeParser;
import com.manateams.scraper.TEAMSGradeRetriever;
import com.manateams.scraper.data.Course;
import com.manateams.scraper.districts.TEAMSUserType;

import java.io.IOException;

public class CourseLoadTask extends AsyncTask<String, String, Course[]> {

    private AsyncTaskCompleteListener callback;
    private Context context;
    private boolean showDialog;

    public CourseLoadTask(AsyncTaskCompleteListener callback, Context context) {
        this.callback = callback;
        this.context = context;
        this.showDialog = showDialog;
    }

    @Override
    protected Course[] doInBackground(String... params) {
        final String username = params[0];
        final String password = params[1];
        final String studentId = params[2];
        final String TEAMSuser = params[3];
        final String TEAMSpass = params[4];


        final DataManager dataManager = new DataManager(context);
        final TEAMSGradeRetriever retriever = new TEAMSGradeRetriever();
        final TEAMSGradeParser parser = new TEAMSGradeParser();

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
            final String newUserIdentification = retriever.getNewUserIdentification(username, password, studentId, TEAMSuser, TEAMSpass, cookie, userType);
            userIdentification = newUserIdentification;
            dataManager.setUserIdentification(newUserIdentification);

            // Get the HTML of the main page
            final String averageHTML = retriever.getTEAMSPage("/selfserve/PSSViewReportCardsAction.do", "", cookie, userType, userIdentification);
            dataManager.setAverageHtml(averageHTML);
            return parser.parseAverages(averageHTML);
        } catch(Exception e) {
            e.printStackTrace();
            dataManager.invalidateCookie();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Course[] courses) {
        callback.onCoursesLoaded(courses);
    }
}
