package com.manateams.android.manateams.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.quickhac.common.TEAMSGradeParser;
import com.quickhac.common.TEAMSGradeRetriever;
import com.quickhac.common.data.Course;
import com.quickhac.common.districts.TEAMSUserType;
import com.quickhac.common.districts.impl.AustinISDParent;
import com.quickhac.common.districts.impl.AustinISDStudent;

public class CourseLoadTask extends AsyncTask<String, String, Course[]> {

    private AsyncTaskCompleteListener callback;
    private Context context;

    public CourseLoadTask(AsyncTaskCompleteListener callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected Course[] doInBackground(String... params) {
        final String username = params[0];
        final String password = params[1];
        final String studentId = params[2];
        final TEAMSUserType userType;
        if (username.matches("^[sS]\\d{7}\\d?$")) {
            userType = new AustinISDStudent();
        } else {
            userType = new AustinISDParent();
        }
        try {
            final TEAMSGradeParser p = new TEAMSGradeParser();

            //Get cookies
            final String cstonecookie = TEAMSGradeRetriever.getAustinisdCookie(username, password);
            final String teamscookie = TEAMSGradeRetriever.getTEAMSCookie(cstonecookie, userType);

            //Generate final cookie
            final String finalcookie = teamscookie + ';' + cstonecookie;

            //POST to login to TEAMS
            String userIdentification = TEAMSGradeRetriever.postTEAMSLogin(username, password, finalcookie, userType);
            final String averageHtml = TEAMSGradeRetriever.getTEAMSPage("/selfserve/PSSViewReportCardsAction.do", "", finalcookie, userType, userIdentification);
            Course[] courses = p.parseAverages(averageHtml);
            return courses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Course[] courses) {
        callback.onCoursesLoaded(courses);
    }
}
