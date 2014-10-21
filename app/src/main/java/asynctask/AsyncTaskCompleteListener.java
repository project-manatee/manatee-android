package asynctask;

import com.quickhac.common.data.Course;

public interface AsyncTaskCompleteListener {
    public void onCoursesLoaded(Course[] courses);
}
