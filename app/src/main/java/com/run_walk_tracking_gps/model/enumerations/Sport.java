package com.run_walk_tracking_gps.model.enumerations;

import com.run_walk_tracking_gps.R;

public enum Sport {

    RUN(R.string.run, R.drawable.ic_run, 1.0),
    WALK(R.string.walk, R.drawable.ic_walk, 0.5);


    private final double constant;
    private final int strId;
    private final int iconId;

    Sport(int strId, int iconId, double constant) {
        this.strId = strId;
        this.iconId = iconId;
        this.constant = constant;
    }

    public int getStrId() {
        return this.strId;
    }

    public int getIconId() {
        return this.iconId;
    }

    public double getConsumedEnergy(final double weight, final double distance){
        return this.constant*weight*distance;
    }
}
