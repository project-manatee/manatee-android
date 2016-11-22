package com.manateams.android.manateams.views;

import android.content.Context;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.manateams.scraper.data.GradeValue;

public class EditorActionListener implements TextView.OnEditorActionListener { //callback for EditText event
    private int categoryIndex;
    private int assignmentIndex;
    private double ptsPossible;
    private Context context;
    private CategoryAdapter adapter;

    public EditorActionListener(Context context, CategoryAdapter adapter, int categoryIndex, int assignmentIndex, double ptsPossible) {
        super();
        this.categoryIndex = categoryIndex;
        this.assignmentIndex = assignmentIndex;
        this.ptsPossible = ptsPossible;
        this.adapter = adapter;
        this.context = context;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE){ //user clicks done
            //lose focus and hide the keyboard
            v.clearFocus();
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            //add or remove projected grade
            String newValue = v.getText().toString();
            if (newValue.isEmpty()) {
                adapter.updateProjectedGrade(categoryIndex, assignmentIndex, null);
            } else {
                double scaledNewValue = (Integer.parseInt(newValue)/ptsPossible)*100.0;
                adapter.updateProjectedGrade(categoryIndex, assignmentIndex, new GradeValue(scaledNewValue));
            }
            adapter.updateProjectedAverages(categoryIndex);
            adapter.notifyDataSetChanged(); //refresh the views
        }
        return false;
    }

}
