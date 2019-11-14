package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.utilities.ConversionUnitUtilities;

import org.json.JSONException;
import java.util.regex.PatternSyntaxException;

public class Measure implements Parcelable {

    public enum Unit {
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
        MILE_PER_HOUR(R.string.mile_per_hour),

        SECOND(R.string.second);

        private final int strId;

        Unit(int strId) {
            this.strId = strId;
        }

        public int getStrId() {
            return this.strId;
        }
    }

    public enum Type {
        DISTANCE(R.string.distance, R.drawable.ic_distance),
        WEIGHT(R.string.weight, R.drawable.ic_weight),
        HEIGHT(R.string.height, R.drawable.ic_height),
        ENERGY(R.string.calories, R.drawable.ic_calories),
        DURATION(R.string.time, R.drawable.ic_time),
        MIDDLE_SPEED(R.string.middle_speed, R.drawable.ic_speedometer);

        private final int strId;
        private final int iconId;

        Type(int strId, int iconId) {
            this.strId = strId;
            this.iconId = iconId;
        }

        public int getStrId() {
            return this.strId;
        }

        public int getIconId() {
            return this.iconId;
        }

        public static Type[] getMeasureChangeable(){
            return new Type[]{DISTANCE, WEIGHT, HEIGHT};
        }

        public static Type[] getMeasureStatistics(){
            return new Type[]{MIDDLE_SPEED,ENERGY, DISTANCE, WEIGHT};
        }

        public Unit[] getMeasureUnit() {
            Unit[] measureUnits = new Unit[0];

            switch (this)
            {
                case HEIGHT:
                    measureUnits = new Unit[]{Unit.METER, Unit.FEET};
                    break;
                case DISTANCE:
                    measureUnits = new Unit[]{Unit.KILOMETER, Unit.MILE};
                    break;
                case WEIGHT:
                    measureUnits = new Unit[]{Unit.KILOGRAM, Unit.POUND};
                    break;
                case MIDDLE_SPEED:
                    measureUnits = new Unit[]{Unit.KILOMETER_PER_HOUR, Unit.MILE_PER_HOUR};
                    break;
                case ENERGY:
                    measureUnits = new Unit[]{Unit.KILO_CALORIES, null};
                    break;
            }
            return measureUnits;
        }

    }

    private Type type;
    private Double value;
    private Unit unit;

    private Measure(Measure measure){
        this.type = measure.type;
        this.value = measure.value;
        this.unit = measure.unit;
    }

    private Measure(Context context, Measure.Type type){
        this.type = type;
        try{
            switch (type){
                case HEIGHT:
                    if(!Preferences.isJustUserLogged(context) ||
                            Preferences.getUnitHeightDefault(context).equals(context.getString(Measure.Unit.METER.getStrId())))
                        setUnit(Measure.Unit.METER);
                    else
                        setUnit(Measure.Unit.FEET);

                    break;
                case DISTANCE:
                    if(!Preferences.isJustUserLogged(context) ||
                            Preferences.getUnitDistanceDefault(context).equals(context.getString(Measure.Unit.KILOMETER.getStrId())))
                        setUnit(Measure.Unit.KILOMETER);
                    else
                        setUnit(Measure.Unit.MILE);
                    break;
                case WEIGHT:
                    if(!Preferences.isJustUserLogged(context) ||
                            Preferences.getUnitWeightDefault(context).equals(context.getString(Measure.Unit.KILOGRAM.getStrId())))
                        setUnit(Measure.Unit.KILOGRAM);
                    else
                        setUnit(Measure.Unit.POUND);
                    break;
                case ENERGY:
                    setUnit(Measure.Unit.KILO_CALORIES);
                    break;
                case MIDDLE_SPEED:
                    if(!Preferences.isJustUserLogged(context) ||
                            Preferences.getUnitDistanceDefault(context).equals(context.getString(Measure.Unit.KILOMETER.getStrId())))
                        setUnit(Unit.KILOMETER_PER_HOUR);
                    else
                        setUnit(Unit.MILE_PER_HOUR);
                    break;
                case DURATION:
                    setUnit(Measure.Unit.SECOND);
                    break;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static Measure create(Context context, Measure.Type type){
        return new Measure(context, type);
    }

    private Measure(Type type, Double value, Unit unit){
        this.type = type;
        this.unit = unit;
        this.value = value;
    }

    public static Measure create(Type type, Double value, Unit unit){
        return new Measure(type, value, unit);
    }

    public Measure clone(){
        return new Measure(this);
    }

// START - Parcelable IMPLEMENTATION
    protected Measure(Parcel in) {
        this.type = Type.valueOf(in.readString());
        this.value = in.readDouble();
        this.unit = Unit.valueOf(in.readString());
    }

    public static final Creator<Measure> CREATOR = new Creator<Measure>() {
        @Override
        public Measure createFromParcel(Parcel in) {
            return new Measure(in);
        }

        @Override
        public Measure[] newArray(int size) {
            return new Measure[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type.toString());
        dest.writeDouble(this.value.doubleValue());
        dest.writeString(this.unit.toString());
    }

// END - Parcelable IMPLEMENTATION


    public Type getType() {
        return type;
    }

    public Double getValue() {
        return this.value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Measure getMeasure(int integer, int decimal){
        if(this.getType().equals(Measure.Type.DURATION)){
            setValue(DurationUtilities.getSecond(integer, decimal));
        }else{
            setValue(Double.valueOf(String.format(getType().equals(Measure.Type.WEIGHT)?
                                                        Measure.Format.FORMAT_WEIGHT : Measure.Format.FORMAT, integer, decimal)));
        }
        return this;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public static boolean isNullOrEmpty(Measure distance) {
        return distance==null || distance.getValue() == null || distance.getValue() == 0f;
    }

    public Measure conversionTo(Unit unit){
        Double val = null;

        switch (this.type){
            case DISTANCE:
                if(unit.equals(Unit.MILE))
                    val = ConversionUnitUtilities.kilometerToMile(this.value);
                break;
            case HEIGHT:
                if(unit.equals(Unit.FEET))
                    val = ConversionUnitUtilities.meterToFeet(this.value);
                break;
            case WEIGHT:
                if(unit.equals(Unit.POUND))
                    val = ConversionUnitUtilities.kilogramToPound(this.value);
                break;
            case MIDDLE_SPEED:
                if(unit.equals(Unit.POUND))
                    val = ConversionUnitUtilities.kilometerForHoursToMileForHours(this.value);
                break;
        }

        return val==null ? null : create(this.type, val, unit);
    }

    public String toString(Context context) {
        if(this.type.equals(Type.DURATION) && this.value!=null)
            return DurationUtilities.format(this.value.intValue());

        if((this.type.equals(Type.DISTANCE) || this.type.equals(Type.ENERGY) || this.type.equals(Type.MIDDLE_SPEED))&& getValue() == 0d)
            return context.getString(R.string.no_available_abbr);

        return getValue() + context.getString(R.string.space) + context.getString(this.unit.getStrId());
    }

    private static class DurationUtilities {

        private static double getSecond(int integer, int decimal){
            return (decimal*60)+(integer*3600);
        }

        private static Integer stringToSeconds(String duration){
            try{
                String[] split = duration.split(":");
                int hours = Integer.valueOf(split[0]);
                int minutes = Integer.valueOf(split[1]);
                int sec = Integer.valueOf(split[2]);
                return sec + (minutes*60)+(hours*3600);

            }catch (PatternSyntaxException e){
                return 0;
            }
        }

        private static String format(int seconds){
            int hours = seconds / 3600;
            int minutes = (seconds /60) % 60;
            int sec = seconds%60;
            return String.format(Measure.Format.FORMAT_DURATION, hours, minutes, sec);
        }
    }

    public static class Format{
        public static final String FORMAT_NUMBER_DOUBLE ="%02d";


        private final static String FORMAT_DURATION = "%02d:%02d:%02d";
        private static final String FORMAT_NUMBER_SINGLE ="%d";
        private final static String FORMAT_DURATION_NO_SEC = "%02d:%02d:00";
        private final static String FORMAT_WEIGHT = FORMAT_NUMBER_SINGLE+"."+FORMAT_NUMBER_SINGLE;
        private final static String FORMAT = FORMAT_NUMBER_SINGLE+"."+FORMAT_NUMBER_DOUBLE;
    }

}
