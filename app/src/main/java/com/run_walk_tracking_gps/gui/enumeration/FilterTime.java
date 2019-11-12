package com.run_walk_tracking_gps.gui.enumeration;

import com.run_walk_tracking_gps.R;

import java.util.Arrays;
import java.util.stream.Stream;

public enum FilterTime {
    ALL(R.string.all),
    YEAR(R.string.year),
    MONTH(R.string.month),
    WEEK(R.string.week);

    private final int strId;

    FilterTime(int strId) {
        this.strId = strId;
    }

    public int getStrId() {
        return this.strId;
    }

    public static FilterTime[] valuesWorkouts(){
        return Stream.of(values()).filter(f -> !f.equals(WEEK)).toArray(FilterTime[]::new);
    }
}
