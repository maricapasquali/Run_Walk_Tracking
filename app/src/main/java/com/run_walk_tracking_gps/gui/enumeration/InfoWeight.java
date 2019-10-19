package com.run_walk_tracking_gps.gui.enumeration;

public enum  InfoWeight {

    WEIGHT(Measure.WEIGHT.getStrId(),Measure.WEIGHT.getIconId()),
    DATE(InfoWorkout.DATE.getStrId(), InfoWorkout.DATE.getIconId());

    private final int strId;
    private final int iconId;

    InfoWeight(int strId, int iconId) {
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
