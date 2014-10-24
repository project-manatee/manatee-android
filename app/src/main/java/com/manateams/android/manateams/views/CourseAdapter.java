package com.manateams.android.manateams.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.Utils;
import com.quickhac.common.data.Course;
import com.quickhac.common.data.GradeValue;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private Course[] courses;
    private Context context;

    public CourseAdapter(Context context, Course[] courses) {
        this.courses = courses;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_course, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }



    private int getGradeColor(GradeValue value) {
        // default of white
        int[] values = new int[] { 255, 255, 255 };
        if (value.type == GradeValue.TYPE_DOUBLE) {
            values = Utils.getGradeColorNumber((int) value.value_d, "#787878");
        } else if (value.type == GradeValue.TYPE_INTEGER) {
            values = Utils.getGradeColorNumber(value.value, "#787878");
        }
        return Color.rgb(values[0], values[1], values[2]);
    }

    private TextView makeFooterText(String footer) {
        TextView text = new TextView(context);
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        text.setTextSize(14);
        text.setTypeface(text.getTypeface(), Typeface.BOLD);
        text.setText(footer);
        text.setPadding(8, 16, 8, 16);
        return text;
    }

    private TextView makeGradeText(GradeValue grade) {
        TextView text = new TextView(context);
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        Typeface sansSerifLight = Typeface.create("sans-serif-light",
                Typeface.NORMAL);
        text.setTextSize(30);
        if(grade != null) {
            text.setBackgroundColor(getGradeColor(grade));
            text.setText(grade.toString());
        } else {
            text.setText("");
        }
        text.setTypeface(sansSerifLight);
        text.setPadding(8, 12, 8, 12);
        return text;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Course course = courses[position];
        viewHolder.setIsRecyclable(false);
        viewHolder.titleText.setText(course.title);
        // Set a different color
        viewHolder.colorBar.setBackgroundColor(Color.parseColor(Constants.COLORS[position % Constants.COLORS.length]));

        if(viewHolder.gradeTable.getChildCount() == 0) {
            // Add grades
            for (int i = 0; i < course.semesters.length; i++) {
                if(course.semesters[i].average != null && !(course.semesters[i].average.toString().length() == 0)) {
                    TableRow gradeRow = new TableRow(context);
                    TableRow footerRow = new TableRow(context);
                    for (int d = 0; d < course.semesters[i].cycles.length; d++) {
                        if (course.semesters[i].cycles[d].average != null) {
                            gradeRow.addView(makeGradeText(course.semesters[i].cycles[d].average));
                        } else {
                            gradeRow.addView(makeGradeText(null));
                        }
                        footerRow.addView(makeFooterText(((context.getString(R.string.misc_cycle) + (i * course.semesters[0].cycles.length + (d + 1)))).toUpperCase()));
                    }
                    if (course.semesters[i].examGrade != null) {
                        gradeRow.addView(makeGradeText(course.semesters[i].examGrade));
                    } else {
                        gradeRow.addView(makeGradeText(null));
                    }
                    footerRow.addView(makeFooterText(context.getString(R.string.misc_final).toUpperCase()));
                    if (course.semesters[i].average != null) {
                        gradeRow.addView(makeGradeText(course.semesters[i].average));
                    } else {
                        gradeRow.addView(makeGradeText(null));
                    }
                    footerRow.addView(makeFooterText(context.getString(R.string.misc_average).toUpperCase()));
                    viewHolder.gradeTable.addView(gradeRow);
                    viewHolder.gradeTable.addView(footerRow);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return courses.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText;
        public LinearLayout colorBar;
        public TableLayout gradeTable;

        public ViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.text_title);
            colorBar = (LinearLayout) itemView.findViewById(R.id.layout_title_color);
            gradeTable = (TableLayout) itemView.findViewById(R.id.table_grades);
        }
    }
}
