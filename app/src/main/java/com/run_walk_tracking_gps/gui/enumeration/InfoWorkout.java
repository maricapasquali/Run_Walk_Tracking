package com.run_walk_tracking_gps.gui.enumeration;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.run_walk_tracking_gps.R;

import java.util.ArrayList;
import java.util.Arrays;


public enum InfoWorkout {

    DATE(R.string.date, R.drawable.ic_calendar),
    SPORT(R.string.sport, 0),
    TIME(Measure.DURATION.getStrId(), Measure.DURATION.getIconId()),
    DISTANCE(Measure.DISTANCE.getStrId(), Measure.DISTANCE.getIconId()),
    CALORIES(Measure.ENERGY.getStrId(), Measure.ENERGY.getIconId()),
    MIDDLE_SPEED(Measure.MIDDLE_SPEED.getStrId(),Measure.MIDDLE_SPEED.getIconId());

    private final int strId;
    private final int iconId;

    InfoWorkout(int strId, int iconId) {
        this.strId = strId;
        this.iconId = iconId;
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
