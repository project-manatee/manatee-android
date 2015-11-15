package com.manateams.android.manateams.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.manateams.android.manateams.R;
import com.manateams.android.manateams.util.DataManager;
import com.manateams.scraper.GPACalc;
import com.manateams.scraper.data.Course;
import com.manateams.scraper.util.Numeric;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GPAFragment extends Fragment implements View.OnClickListener {

    private LinearLayout rootLayout;

    private DataManager dataManager;
    private Button gpaText;
    private Course[] courses;
    ViewPager pager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gpa, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataManager = new DataManager(getActivity());
        courses = dataManager.getCourseGrades();
        setupViews();
        setupPager(dataManager);
        populateGPASummaries();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateGPASummaries();
    }

    private void setupViews() {
        rootLayout = (LinearLayout) getActivity().findViewById(R.id.layout_root);
        gpaText = (Button) getActivity().findViewById(R.id.text_gpa);
        gpaText.setOnClickListener(this);
    }
    private void setupPager(DataManager dataManager){
        pager = (ViewPager)getActivity().findViewById(R.id.pager);
        pager.setAdapter(new GraphAdapter (getChildFragmentManager(), dataManager));
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
        tabs.setTextColor(getResources().getColor(R.color.white));
        tabs.setBackgroundColor(getResources().getColor(R.color.app_primary));
        tabs.setViewPager(pager);
    }
    private void populateGPASummaries() {
        if(courses != null) {
            try {
                gpaText.setText("GPA: " + String.valueOf(Numeric.round(GPACalc.unweighted(courses), 3)) + " / " + String.valueOf(Numeric.round(getWeightedGPA(), 3)));
            }
            catch(Exception e){
                gpaText.setText("GPA: N/A");
            }
            if(dataManager.isFirstTimeViewingGPA()) {
                ShowcaseView showcaseView = new ShowcaseView.Builder(getActivity())
                        .setTarget(new ViewTarget(getView().findViewById(R.id.text_gpa)))
                        .setContentTitle(getResources().getString(R.string.gpa_intro_title))
                        .setContentText(getResources().getString(R.string.gpa_intro_content))
                        .setStyle(R.style.CustomShowcaseTheme)
                        .hideOnTouchOutside()
                        .setShowcaseEventListener(new OnShowcaseEventListener() {
                            @Override
                            public void onShowcaseViewHide(ShowcaseView showcaseView) {

                            }

                            @Override
                            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                    new ShowcaseView.Builder(getActivity())
                                            .setTarget(new ViewTarget(getView().findViewById(R.id.linechart)))
                                            .setContentTitle(getResources().getString(R.string.graph_intro_title))
                                            .setContentText(getResources().getString(R.string.graph_intro_content))
                                            .setStyle(R.style.CustomShowcaseTheme)
                                            .hideOnTouchOutside()
                                            .build();
                            }

                            @Override
                            public void onShowcaseViewShow(ShowcaseView showcaseView) {

                            }
                        })
                        .build();
                dataManager.setFirstTimeViewingGPA(false);
                //ShowcaseView.insertShowcaseView(R.id.text_gpa, getActivity(), "GPA", "Tap to adjust weighted classes.", null);
            }
        }
    }

    private double getWeightedGPA() {
        SharedPreferences defaultPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        List<String> weightedClasses = new ArrayList<String>();
        Set<String> savedWeighted = defaultPrefs.getStringSet(
                "pref_weightedClasses", null);
        if (savedWeighted != null) {
            String[] weighted = savedWeighted
                    .toArray(new String[savedWeighted.size()]);
            if (weighted != null) {
                for (int i = 0; i < weighted.length; i++) {
                    weightedClasses.add(weighted[i]);
                }
            }
        }

        List<String> excludedClasses = new ArrayList<String>();
        Set<String> savedExcluded = defaultPrefs.getStringSet(
                "pref_excludedClasses", null);
        if (savedExcluded != null) {
            String[] excluded = savedExcluded
                    .toArray(new String[savedExcluded.size()]);
            if (excluded != null) {
                for (int i = 0; i < excluded.length; i++) {
                    excludedClasses.add(excluded[i]);
                }
            }
        }

        // remove excluded classes from list of weighed classes to calculate
        List<String> toWeighted = new ArrayList<String>();
        for (int i = 0; i < weightedClasses.size(); i++) {
            boolean excluded = false;
            for (int d = 0; d < excludedClasses.size(); d++) {
                if (d < excludedClasses.size()
                        && excludedClasses.get(d).equals(courses[i].title)) {
                    excluded = true;
                }
            }
            if (!excluded) {
                toWeighted.add(weightedClasses.get(i));
            }
        }
        return GPACalc.weighted(courses, toWeighted, 0);
    }

    @Override
    public void onClick(View v) {
        Course[] courses = new DataManager(getActivity()).getCourseGrades();
        final CharSequence[] classes = new CharSequence[courses.length];
        boolean[] isSelectedArray = new boolean[courses.length];
        for (int i = 0; i < courses.length; i++) {
            classes[i] = courses[i].title;
        }
        final SharedPreferences defaultPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        final Set<String> savedWeighted = defaultPrefs.getStringSet(
                "pref_weightedClasses", new HashSet<String>());
        if (savedWeighted != null) {
            for (String s : savedWeighted) {
                for (int i = 0; i < classes.length; i++) {
                    if (classes[i].equals(s)) {
                        isSelectedArray[i] = true;
                    }
                }
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.pref_chooseWeighted));
        builder.setMultiChoiceItems(classes,isSelectedArray,new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked){
                    savedWeighted.add(String.valueOf(classes[which]));
                }
                else{
                    savedWeighted.remove(String.valueOf(classes[which]));
                }
            }
        });
        builder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor e = defaultPrefs.edit();
                e.putStringSet("pref_weightedClasses",savedWeighted);
                e.commit();
                populateGPASummaries();

            }
        });
        builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    public class GraphAdapter extends FragmentPagerAdapter {
        Course[] courses;
        public GraphAdapter(FragmentManager fm,DataManager d) {
            super(fm);
            courses = d.getCourseGrades();
        }

        @Override
        public Fragment getItem(int i) {
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putString("request", courses[i].courseId);
            args.putInt("index",i);
            GraphFragment fragment = new GraphFragment();
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return courses[position].title;
        }
        @Override
        public int getCount() {
            return courses.length;
        }
    }
}
