package com.run_walk_tracking_gps.gui.enumeration;

import com.run_walk_tracking_gps.R;

public enum MeasureUnit
{
    KILOMETER(R.string.kilometer),
    MILE(R.string.mile),

    KILOGRAM(R.string.kilogram),
    POUND(R.string.pound),

    METER(R.string.meter),
    FEET(R.string.feet),

    KILO_CALORIES(R.string.kilo_calories),

    HOURS(R.string.unit_hours),
    MINUTES(R.string.unit_min),

    KILOMETER_PER_HOUR(R.string.kilometer_per_hour),
    MILE_PER_HOUR(R.string.mile_per_hour);

    private final int strId;

    MeasureUnit(int strId) {
        this.strId = strId;
    }

    public int getStrId() {
        return this.strId;
    }

    /**
     * 1 Mile = 1.61 Km
     * 1 Pound = 0.45 Kg
     * 1 Feet = 0,3048 m
     * 1 Inch = 2.54 cm
     */
}
