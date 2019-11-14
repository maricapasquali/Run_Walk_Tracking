package com.run_walk_tracking_gps.gui.enumeration;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;

import java.util.ArrayList;
import java.util.Arrays;


public enum InfoWorkout {

    DATE(R.string.date, R.drawable.ic_calendar),
    SPORT(R.string.sport),
    TIME(Measure.Type.DURATION),
    DISTANCE(Measure.Type.DISTANCE),
    CALORIES(Measure.Type.ENERGY),
    MIDDLE_SPEED(Measure.Type.MIDDLE_SPEED);

    private final int strId;
    private final int iconId;

    InfoWorkout(int strId) {
        this.strId = strId;
        this.iconId = 0;
    }

    InfoWorkout(int strId, int iconId) {
        this.strId = strId;
        this.iconId = iconId;
    }

    InfoWorkout(Measure.Type measure) {
        this.strId = measure.getStrId();
        this.iconId = measure.getIconId();
    }

    public int getStrId() {
        return this.strId;
    }

    public int getIconId() {
        return this.iconId;
    }

    public static boolean isSport(InfoWorkout info) {
        return info==SPORT;
    }

    public static InfoWorkout[] infoWorkoutNoSpeed() {
        return new ArrayList<>(Arrays.asList(values())).stream().filter(i -> i != MIDDLE_SPEED).toArray(InfoWorkout[]::new);
    }

}
