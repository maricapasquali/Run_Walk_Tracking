package com.run_walk_tracking_gps.gui.enumeration;

import com.run_walk_tracking_gps.R;


public enum Measure {

    DISTANCE(R.string.distance, R.drawable.ic_distance),
    WEIGHT(R.string.weight, R.drawable.ic_weight),
    HEIGHT(R.string.height, R.drawable.ic_height),
    ENERGY(R.string.calories, R.drawable.ic_calories),
    DURATION(R.string.time, R.drawable.ic_time),
    MIDDLE_SPEED(R.string.middle_speed, R.drawable.ic_speedometer);

    private final int strId;
    private final int iconId;

    Measure(int strId, int iconId) {
        this.strId = strId;
        this.iconId = iconId;
    }

    public int getStrId() {
        return this.strId;
    }

    public int getIconId() {
        return this.iconId;
    }

    public static Measure[] getMeasureChangeable(){
        return new Measure[]{DISTANCE, WEIGHT, HEIGHT};
    }

    public static Measure[] getMeasureStatistics(){
        return new Measure[]{MIDDLE_SPEED,ENERGY, DISTANCE, WEIGHT};
    }

    public MeasureUnit[] getMeasureUnit() {
        MeasureUnit[] measureUnits = new MeasureUnit[0];

        switch (this)
        {
            case HEIGHT:
                measureUnits = new MeasureUnit[]{MeasureUnit.METER, MeasureUnit.FEET};
                break;
            case DISTANCE:
                measureUnits = new MeasureUnit[]{MeasureUnit.KILOMETER, MeasureUnit.MILE};
                break;
            case WEIGHT:
                measureUnits = new MeasureUnit[]{MeasureUnit.KILOGRAM, MeasureUnit.POUND};
                break;
            case MIDDLE_SPEED:
                measureUnits = new MeasureUnit[]{MeasureUnit.KILOMETER_PER_HOUR, MeasureUnit.MILE_PER_HOUR};
                break;
            case ENERGY:
                measureUnits = new MeasureUnit[]{MeasureUnit.KILO_CALORIES, null};
                break;
        }
        return measureUnits;
    }

}
