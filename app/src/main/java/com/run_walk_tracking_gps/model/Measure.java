package com.run_walk_tracking_gps.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.utilities.ConversionUnitUtilities;
import com.run_walk_tracking_gps.utilities.NumberUtilities;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;
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

        /**
         * @return default measure unit of DATABASE
         */
        public Unit getMeasureUnitDefault(){
            return getMeasureUnit()[0];
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

    private Context context;

    private Type type;
    private Double value;
    private Unit unit;

    private Measure(Measure measure){
        this.context = measure.context;
        this.type = measure.type;
        this.value = measure.value;
        this.unit = measure.unit;
    }

    private Measure(Context context, Measure.Type type){
        this.context = context;
        this.type = type;
        this.value = 0d;
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
                case MIDDLE_SPEED:
                    if(!Preferences.isJustUserLogged(context) ||
                            Preferences.getUnitDistanceDefault(context).equals(context.getString(Measure.Unit.KILOMETER.getStrId())))
                        setUnit(Unit.KILOMETER_PER_HOUR);
                    else
                        setUnit(Unit.MILE_PER_HOUR);
                    break;
                case ENERGY:
                    setUnit(Measure.Unit.KILO_CALORIES);
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

    public static Measure create(Context context, Measure.Type type, Double value){
        final  Measure measure = new Measure(context, type);
        measure.setValue(value);
        return measure;
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
        dest.writeDouble(getValue());
        dest.writeString(this.unit.toString());
    }

// END - Parcelable IMPLEMENTATION


    public Context getContext() {
        return context;
    }

    public Type getType() {
        return type;
    }

// GET TO DB
    public Double getValue(){
        if(this.value==null)return 0d;
        reSetUnit();
        return NumberUtilities.round2(this.value);
    }

// GET TO GUI
    public Double getValueToGui(){

        if(this.type.equals(Type.ENERGY) || isDefaultUnitMeasure())
            return getValue();

        if(this.value==null)return 0d;
        reSetUnit();
        if(this.type.equals(Type.MIDDLE_SPEED) )
            return conversionTo(Type.MIDDLE_SPEED.getMeasureUnit()[1], value);
        else
            return conversionTo(this.type.getMeasureUnit()[1], value);
    }

    private void reSetUnit(){
        if(!type.equals(Type.DURATION)){
            try {
                switch (this.type){
                    case MIDDLE_SPEED:
                    {

                        final String unitS = Preferences.getUnitDistanceDefault(context); //km
                        if(!unitS.equals(context.getString(this.unit.getStrId()).split("/")[0])) // km  != mi
                            setUnit(this.unit.equals(Type.MIDDLE_SPEED.getMeasureUnit()[0])? // mi/h
                                    Type.MIDDLE_SPEED.getMeasureUnit()[1]
                                    : Type.MIDDLE_SPEED.getMeasureUnit()[0]);
                        break;

                    }
                    default:
                    {
                        final String unitS = Preferences.getUnitDefault(context, this.type.toString().toLowerCase()); //km
                        if(!unitS.equals(context.getString(this.unit.getStrId()))) // km  == mi
                            setUnit(this.unit.equals(this.type.getMeasureUnit()[0])? this.type.getMeasureUnit()[1] : this.type.getMeasureUnit()[0]);
                        break;
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private boolean isDefaultUnitMeasure(){
        try {
            if(type.equals(Type.MIDDLE_SPEED))
                return Preferences.getUnitDistanceDefault(context).equals(context.getString(Type.DISTANCE.getMeasureUnit()[0].strId));
            else
                return Preferences.getUnitDefault(context, this.type.toString().toLowerCase())
                        .equals(context.getString(this.type.getMeasureUnit()[0].strId));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Unit getUnit() {
        return unit;
    }

// SET FROM GUI
    public void setValueFromGui(Double value){
        try {
            switch (this.type){
                case DISTANCE:{
                    final String unitS = Preferences.getUnitDistanceDefault(context);
                    this.value = unitS.equals(context.getString(Unit.MILE.getStrId())) ? conversionTo(Unit.KILOMETER, value): value;
                    break;
                }
                case WEIGHT:{
                    final String unitS = Preferences.getUnitWeightDefault(context);
                    this.value = unitS.equals(context.getString(Unit.POUND.getStrId())) ? conversionTo(Unit.KILOGRAM, value): value;
                    break;
                }
                case HEIGHT:{
                    final String unitS = Preferences.getUnitHeightDefault(context);
                    this.value = unitS.equals(context.getString(Unit.FEET.getStrId())) ? conversionTo(Unit.METER, value): value;
                    break;
                }
                default:
                    setValue(value);
                    break;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

// SET FROM DB
    public void setValue(Double value) {
        this.value = value;
    }

    public Measure getMeasure(int integer, int decimal){
        if(this.getType().equals(Measure.Type.DURATION))
            setValue(DurationUtilities.getSecond(integer, decimal));
        else
            setValueFromGui(Double.valueOf(String.format(this.type.equals(Measure.Type.WEIGHT)?
                    Measure.Format.FORMAT_WEIGHT :
                    Measure.Format.FORMAT, integer, decimal)));

        return this;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public static boolean isNullOrEmpty(Measure distance) {
        return distance==null || distance.getValue() == null || distance.getValue() == 0d;
    }

    public static Double conversionTo(Unit unit, Double value){
        switch (unit){
            case MILE:
                return ConversionUnitUtilities.kilometerToMile(value);
            case KILOMETER:
                return ConversionUnitUtilities.mileToKilometer(value);
            case POUND:
                return ConversionUnitUtilities.kilogramToPound(value);
            case KILOGRAM:
                return ConversionUnitUtilities.poundToKilogram(value);
            case FEET:
                return ConversionUnitUtilities.meterToFeet(value);
            case METER:
                return ConversionUnitUtilities.feetToMeter(value);
            case MILE_PER_HOUR:
                return ConversionUnitUtilities.kilometerForHoursToMileForHours(value);
            case KILOMETER_PER_HOUR:
                return ConversionUnitUtilities.mileForHoursToKilometerForHours(value);
        }
        return null;
    }

    private String toString(Context context) {
        if(this.type.equals(Type.DURATION))
            return DurationUtilities.format(this.value.intValue());

        if((this.type.equals(Type.DISTANCE) || this.type.equals(Type.ENERGY) || this.type.equals(Type.MIDDLE_SPEED))&&
                getValue() == 0d)
            return context.getString(R.string.no_available_abbr);

        return getValueToGui() + context.getString(R.string.space) + context.getString(getUnit().getStrId());
    }

    @Override
    public String toString() {
        if(context!=null)
            return toString(context);
        return "context not set";
    }


    public String toString(boolean isHome) {
        if(isHome)
        {
            if(this.type.equals(Type.DURATION))
                return DurationUtilities.format(this.value.intValue());
            return getValueToGui() + context.getString(R.string.space) + context.getString(getUnit().getStrId());

        }
        return toString();
    }

    public static class DurationUtilities {

        private static double getSecond(int integer, int decimal){
            return (decimal*60)+(integer*3600);
        }

        public static Integer toSeconds(String duration){
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

        private static List<Integer> time(int seconds){
            int hours = seconds / 3600;
            int minutes = (seconds /60) % 60;
            int sec = seconds%60;
            return Arrays.asList(hours, minutes, sec);
        }

        @SuppressLint("DefaultLocale")
        public static String format(int seconds){
            final List<Integer> time = time(seconds);
            return String.format(Measure.Format.FORMAT_DURATION, time.get(0), time.get(1), time.get(2));
        }

    }

    public static class Format{
        public static final String FORMAT_NUMBER_DOUBLE ="%02d";
        public static final String FORMAT_NUMBER_SINGLE ="%d";

        private final static String FORMAT_DURATION = FORMAT_NUMBER_DOUBLE+":"+FORMAT_NUMBER_DOUBLE+":"+FORMAT_NUMBER_DOUBLE;
        private final static String FORMAT_WEIGHT = FORMAT_NUMBER_SINGLE+"."+FORMAT_NUMBER_SINGLE;
        private final static String FORMAT = FORMAT_NUMBER_SINGLE+"."+FORMAT_NUMBER_DOUBLE;
    }

}
