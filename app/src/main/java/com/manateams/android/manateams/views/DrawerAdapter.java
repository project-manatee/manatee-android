package com.manateams.android.manateams.views;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.manateams.android.manateams.R;

public class DrawerAdapter extends ArrayAdapter<String> {

    private String[] items;
    private LayoutInflater vi;
    private int viewResourceID;
    private Context context;
    public DrawerAdapter(Context context, int textViewResourceId, String[] items) {
        super(context, textViewResourceId, items);
        this.context = context;
        vi = LayoutInflater.from(context);
        viewResourceID = textViewResourceId;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        String g = items[position];
        if (v == null){
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            v = inflater.inflate(viewResourceID, parent, false);
            TextView title = (TextView) v.findViewById(R.id.title);
            title.setText(g);
            ImageView icon = (ImageView) v.findViewById(R.id.icon);
            switch(position) {
                case 0:
                    icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_assignment_grey600_48dp));
                    break;
                case 1:
                    icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_assessment_grey600_48dp));
                    break;
                case 2:
                    icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_settings_grey600_48dp));
                    break;
                case 3:
                    icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_info_grey600_48dp));
                    break;
            }
        }
        // v = g.addView(getContext(), position);
        return v;
    }

}