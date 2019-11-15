package com.run_walk_tracking_gps.gui.enumeration;

import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.Workout;

public enum  InfoWeight {

    WEIGHT(Measure.Type.WEIGHT),
    DATE(Workout.Info.DATE);

    private final int strId;
    private final int iconId;

    InfoWeight(int strId, int iconId) {
        this.strId = strId;
        this.iconId = iconId;
    }

    InfoWeight(Measure.Type measure) {
        this.strId = measure.getStrId();
        this.iconId = measure.getIconId();
    }

    InfoWeight(Workout.Info infoWorkout) {
        this.strId = infoWorkout.getStrId();
        this.iconId = infoWorkout.getIconId();
    }

    public int getStrId() {
        return this.strId;
    }

    public int getIconId() {
        return this.iconId;
    }
}
