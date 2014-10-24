package com.manateams.android.manateams.asynctask;

import com.quickhac.common.data.ClassGrades;
import com.quickhac.common.data.Course;

public interface AsyncTaskCompleteListener {
    public void onCoursesLoaded(Course[] courses);
    public void onClassGradesLoaded(ClassGrades[] grades, int courseIndex);
}
