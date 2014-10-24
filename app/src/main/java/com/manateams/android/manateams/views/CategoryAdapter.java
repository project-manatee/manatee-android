package com.manateams.android.manateams.views;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.Constants;
import com.quickhac.common.data.Assignment;
import com.quickhac.common.data.Category;
import com.quickhac.common.data.ClassGrades;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ClassGrades grades;
    private Context context;

    public CategoryAdapter(Context context, ClassGrades grades) {
        this.context = context;
        this.grades = grades;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_assignment_category, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder viewHolder, int position) {
        viewHolder.setIsRecyclable(false);
        if (viewHolder.assignmentTable.getChildCount() == 0) {
            if (grades != null && grades.categories != null) {
                Category category = grades.categories[position];
                if (category != null) {
                    Assignment[] assignments = category.assignments;
                    if (assignments != null) {
                        viewHolder.titleText.setText(category.title);
                        // Set a different color
                        viewHolder.colorBar.setBackgroundColor(Color.parseColor(Constants.COLORS[position % Constants.COLORS.length]));

                        for (int i = 0; i < assignments.length; i++) {
                            TableRow assignmentRow = new TableRow(context);
                            Assignment assignment = assignments[i];
                            if (assignment != null) {
                                LinearLayout row = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.row_assignment, null);
                                Log.d("WATWAT", "Adding assignment " + assignment.title);
                                TextView assignmentText = (TextView) row.findViewById(R.id.text_assignment);
                                TextView gradeText = (TextView) row.findViewById(R.id.text_grade);
                                assignmentText.setText(assignment.title);
                                gradeText.setText(assignment.pointsString());
                                assignmentRow.addView(row);
                                viewHolder.assignmentTable.addView(assignmentRow);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (grades != null && grades.categories != null) {
            return grades.categories.length;
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText;
        public TextView weightText;
        public LinearLayout colorBar;
        public TableLayout assignmentTable;
        public TextView averageText;

        public ViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.text_title);
            weightText = (TextView) itemView.findViewById(R.id.text_weight);
            colorBar = (LinearLayout) itemView.findViewById(R.id.layout_title_color);
            assignmentTable = (TableLayout) itemView.findViewById(R.id.table_assignments);
            averageText = (TextView) itemView.findViewById(R.id.text_average);
        }
    }
}
