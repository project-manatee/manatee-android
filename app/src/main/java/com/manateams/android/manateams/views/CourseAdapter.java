package com.manateams.android.manateams.views;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.Constants;
import com.quickhac.common.data.Course;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private Course[] courses;

    public CourseAdapter(Course[] courses) {
        this.courses = courses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_course, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.titleText.setText(courses[position].title);
        // Set a different color
        viewHolder.colorBar.setBackgroundColor(Color.parseColor(Constants.COLORS[position % Constants.COLORS.length]));
    }

    @Override
    public int getItemCount() {
        return courses.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText;
        public LinearLayout colorBar;
        public ViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.text_title);
            colorBar = (LinearLayout) itemView.findViewById(R.id.layout_title_color);
        }
    }
}
