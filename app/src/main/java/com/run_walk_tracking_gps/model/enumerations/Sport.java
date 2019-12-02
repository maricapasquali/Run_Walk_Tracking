package com.run_walk_tracking_gps.model.enumerations;

import com.run_walk_tracking_gps.R;

public enum Sport {

    RUN(R.string.run, R.drawable.ic_run),
    WALK(R.string.walk, R.drawable.ic_walk);

    private final int strId;
    private final int iconId;

    Sport(int strId, int iconId) {
        this.strId = strId;
        this.iconId = iconId;
    }

    public int getStrId() {
        return this.strId;
    }

    public int getIconId() {
        return this.iconId;
    }
}
