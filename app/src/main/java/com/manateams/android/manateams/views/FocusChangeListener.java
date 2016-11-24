package com.manateams.android.manateams.views;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.manateams.scraper.data.GradeValue;

public class FocusChangeListener implements View.OnFocusChangeListener{
    private int categoryIndex;
    private int assignmentIndex;
    private double ptsPossible;
    private Context context;
    private CategoryAdapter adapter;

    public FocusChangeListener(Context context, CategoryAdapter adapter, int categoryIndex, int assignmentIndex, double ptsPossible) {
        super();
        this.categoryIndex = categoryIndex;
        this.assignmentIndex = assignmentIndex;
        this.ptsPossible = ptsPossible;
        this.adapter = adapter;
        this.context = context;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText gradeText = (EditText) v;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (hasFocus) {
            //move the cursor to the end and bring up the keyboard
            String s = gradeText.getText().toString();
            gradeText.setText("");
            gradeText.append(s);
            imm.showSoftInput(gradeText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            //hide the keyboard
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            //update projected grades
            String newValue = gradeText.getText().toString();
            if (newValue.isEmpty()) {
                adapter.updateProjectedGrade(categoryIndex, assignmentIndex, null);
            } else {
                double scaledNewValue = (Integer.parseInt(newValue)/ptsPossible)*100.0;
                adapter.updateProjectedGrade(categoryIndex, assignmentIndex, new GradeValue(scaledNewValue));
            }
            adapter.updateProjectedAverages(categoryIndex);
            try {
                adapter.notifyDataSetChanged();//refresh the views
            } catch (IllegalStateException e) {
                //this exception will be thrown if the focus is lost
                //by the user scrolling away
            }
        }
    }
}
