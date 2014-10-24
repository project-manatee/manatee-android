package com.manateams.android.manateams.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.manateams.android.manateams.R;
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

    public AssignmentLoadTask(AsyncTaskCompleteListener callback, Context context) {
        this.callback = callback;
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (dialog == null) {
            this.dialog = new ProgressDialog(context);
            this.dialog.setMessage(context.getString(R.string.dialog_assignment_loading));
            this.dialog.setCancelable(false);
            this.dialog.show();
        }
    }

    @Override
    protected ClassGrades[] doInBackground(String... params) {
        final String username = params[0];
        final String password = params[1];
        final String studentId = params[2];
        courseIndex = Integer.valueOf(params[3]);
        final TEAMSUserType userType;
        if (username.matches("^s\\d{7}$")) {
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
            ClassGrades[] grades = new ClassGrades[6]; // TODO don't hardcode length
            for(int i = 0; i < grades.length; i++) {
                grades[i] = TEAMSGradeRetriever.getCycleClassGrades(courses[courseIndex], i, averageHtml, finalcookie, userType, userIdentification);

            }
            return grades;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ClassGrades[] grades) {
        dialog.dismiss();
        callback.onClassGradesLoaded(grades, courseIndex);
    }
}
