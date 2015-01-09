package com.manateams.android.manateams.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.android.manateams.util.Utils;
import com.quickhac.common.TEAMSGradeParser;
import com.quickhac.common.TEAMSGradeRetriever;
import com.quickhac.common.data.ClassGrades;
import com.quickhac.common.data.Course;
import com.quickhac.common.districts.TEAMSUserType;
import com.quickhac.common.districts.impl.AustinISDParent;
import com.quickhac.common.districts.impl.AustinISDStudent;

public class AssignmentLoadTask extends AsyncTask<String, String, ClassGrades[]> {

    private AsyncTaskCompleteListener callback;
    private Context context;
    private int courseIndex;
    ProgressDialog dialog;
    private boolean showDialog;

    public AssignmentLoadTask(AsyncTaskCompleteListener callback, Context context, boolean showDialog) {
        this.callback = callback;
        this.context = context;
        this.showDialog = showDialog;
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
        final String studentId = params[2];
        courseIndex = Integer.valueOf(params[3]);
        final String TEAMSuser = params[4];
        final String TEAMSpass = params[5];
        final TEAMSUserType userType;
        if (username.matches("^[sS]\\d{6,8}\\d?$")) {
            userType = new AustinISDStudent();
        } else {
            userType = new AustinISDParent();
        }
        try {
            final TEAMSGradeParser p = new TEAMSGradeParser();

            //Generate final cookie
            final String finalcookie = Utils.getTEAMSCookies(new DataManager(context),username,password,userType);

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
            ClassGrades[] grades = new ClassGrades[6]; // TODO don't hardcode length
            for(int i = 0; i < grades.length; i++) {
                grades[i] = TEAMSGradeRetriever.getCycleClassGrades(courses[courseIndex], i, averageHtml, finalcookie, userType, userIdentification);
            }
            if(grades == null) {
                Log.d("WATWAT", "grades null at load");
            }
            return grades;
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
