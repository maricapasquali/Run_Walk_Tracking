package com.run_walk_tracking_gps.model.enumerations;

import com.run_walk_tracking_gps.R;


public enum Target {

    MARATHON(R.string.marathon),
    LOSE_WEIGHT(R.string.lose_weight);

    private final int strId;

    Target(int strId) {
        this.strId = strId;
    }

    public int getStrId() {
        return this.strId;
    }

    public int getIconId() {
        return  R.drawable.ic_target;
    }

}
