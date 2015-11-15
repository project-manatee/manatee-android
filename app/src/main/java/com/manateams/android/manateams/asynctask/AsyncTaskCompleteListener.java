package com.manateams.android.manateams.asynctask;

import com.manateams.scraper.data.ClassGrades;
import com.manateams.scraper.data.Course;

public interface AsyncTaskCompleteListener {
    public void onCoursesLoaded(Course[] courses);
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex);
}
