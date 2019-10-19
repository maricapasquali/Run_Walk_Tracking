package com.run_walk_tracking_gps.gui.enumeration;

import com.run_walk_tracking_gps.R;

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
}
