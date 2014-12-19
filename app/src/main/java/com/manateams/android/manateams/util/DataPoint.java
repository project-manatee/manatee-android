package com.manateams.android.manateams.util;

import com.quickhac.common.data.GradeValue;

/**
 * Created by Ehsan on 12/19/2014.
 */
public class DataPoint {
    public GradeValue g;
    public long time;

    public DataPoint(GradeValue grade, long l) {
        g  = grade;
        time = l;
    }
}
