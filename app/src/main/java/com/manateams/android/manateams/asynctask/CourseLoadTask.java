package com.manateams.android.manateams.asynctask;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.android.manateams.util.Utils;
import com.quickhac.common.TEAMSGradeParser;
import com.quickhac.common.TEAMSGradeRetriever;
import com.quickhac.common.data.Course;
import com.quickhac.common.districts.TEAMSUserType;
import com.quickhac.common.districts.impl.AustinISDParent;
import com.quickhac.common.districts.impl.AustinISDStudent;

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
        final TEAMSUserType userType;
        if (username.matches("^[sS]\\d{6,8}\\d?$")) {
            userType = new AustinISDStudent();
        } else {
            userType = new AustinISDParent();
        }
        try {
            final TEAMSGradeParser p = new TEAMSGradeParser();

            //Generate final cookie
            final String finalcookie = Utils.getTEAMSCookies(new DataManager(context), username, password, userType);

            //POST to login to TEAMS
            String userIdentification;
            if (TEAMSuser.length()>0){
                //See if user has a seperate login for TEAMS/AISD
                userIdentification = TEAMSGradeRetriever.postTEAMSLogin(TEAMSuser, TEAMSpass, studentId, finalcookie, userType);
            }
            else{
                userIdentification = TEAMSGradeRetriever.postTEAMSLogin(username, password,studentId, finalcookie, userType);
            }
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
