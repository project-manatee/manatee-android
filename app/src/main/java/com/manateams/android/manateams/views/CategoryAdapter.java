package com.manateams.android.manateams.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.Constants;
import com.manateams.scraper.data.Assignment;
import com.manateams.scraper.data.Category;
import com.manateams.scraper.data.ClassGrades;

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
                if (position >= grades.categories.length) {
                    viewHolder.titleText.setText(context.getString(R.string.misc_average));
                    if (grades.average != -1) {
                        viewHolder.weightText.setText(Integer.toString(grades.average));
                    } else {
                        viewHolder.weightText.setText("");
                    }
                    viewHolder.colorBar.setBackgroundColor(context.getResources().getColor(R.color.app_primary));

                    viewHolder.assignmentTable.setPadding(0, 0, 0, 0);
                } else {
                    Category category = grades.categories[position];
                    if (category != null) {
                        Assignment[] assignments = category.assignments;
                        if (assignments != null) {
                            viewHolder.titleText.setText(category.title);
                            // Set a different color
                            viewHolder.colorBar.setBackgroundColor(Color.parseColor(Constants.COLORS[position % Constants.COLORS.length]));

                            if (category.weight >= 0) {
                                viewHolder.weightText.setText(((int) category.weight) + "%");
                            } else {
                                viewHolder.weightText.setText(context.getString(R.string.text_card_category_assignment_weight_null));
                            }

                            for (int i = 0; i < assignments.length; i++) {
                                TableRow assignmentRow = new TableRow(context);
                                Assignment assignment = assignments[i];
                                if (assignment != null) {
                                    LinearLayout row = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.row_assignment, null);
                                    TextView assignmentText = (TextView) row.findViewById(R.id.text_assignment);
                                    TextView gradeText = (TextView) row.findViewById(R.id.text_grade);
                                    assignmentText.setText(assignment.title);
                                    gradeText.setText(assignment.pointsString());
                                    assignmentRow.addView(row);
                                    viewHolder.assignmentTable.addView(assignmentRow);
                                }
                            }

                            if (category.average != null) {
                                // Add category average
                                TableRow averageRow = new TableRow(context);
                                LinearLayout row = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.row_assignment, null);
                                TextView assignmentText = (TextView) row.findViewById(R.id.text_assignment);
                                TextView gradeText = (TextView) row.findViewById(R.id.text_grade);
                                assignmentText.setText(context.getString(R.string.misc_average));
                                assignmentText.setTypeface(assignmentText.getTypeface(), Typeface.BOLD);
                                gradeText.setText(String.valueOf(category.average.intValue()));
                                gradeText.setTypeface(assignmentText.getTypeface(), Typeface.BOLD);
                                averageRow.addView(row);
                                viewHolder.assignmentTable.addView(averageRow);
                            }
                        }
                    }
                }
            }
        } else {
            viewHolder.titleText.setText("No grades :(");
        }
    }

    @Override
    public int getItemCount() {
        if (grades != null && grades.categories != null) {
            return grades.categories.length + 1; //one more card for course average
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText;
        public TextView weightText;
        public LinearLayout colorBar;
        public TableLayout assignmentTable;

        public ViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.text_title);
            weightText = (TextView) itemView.findViewById(R.id.text_weight);
            colorBar = (LinearLayout) itemView.findViewById(R.id.layout_title_color);
            assignmentTable = (TableLayout) itemView.findViewById(R.id.table_assignments);
        }
    }
}
