package com.run_walk_tracking_gps.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.utilities.ConversionUnitUtilities;
import com.run_walk_tracking_gps.utilities.NumberUtilities;

import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

public class Measure implements Parcelable {

    public enum Unit {
        // DISTANCE
        KILOMETER("Km"),
        MILE("mi"),

        // WEIGHT
        KILOGRAM("Kg"),
        POUND("lb"),

        // HEIGHT
        METER("m"),
        FEET("ft"),

        // ENERGY
        KILO_CALORIES("Kcal"),

        // DURATION
        HOURS("h"),
        MINUTES("min"),
        SECOND("s"),

        // MIDDLE SPEED
        KILOMETER_PER_HOUR("Km/h"),
        MILE_PER_HOUR("mi/h");

        private final String str;

        Unit(String str) {
            this.str = str;
        }

        public String getString() {
            return this.str;
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

        public Unit getValueOfMeasureUnitDefault(final String value){
            try{
                return Stream.of(getMeasureUnit()).filter(u -> u.equals(Unit.valueOf(value))).findFirst().orElse(getMeasureUnitDefault());
            }catch (NullPointerException e){
                return getMeasureUnitDefault();
            }
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

    private static final String UNSET_CONTEXT = "Context not set";
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
        switch (type){
            case HEIGHT:
                setUnit(DefaultPreferencesUser.getUnitHeightDefault(context));
                break;
            case DISTANCE:
                setUnit(DefaultPreferencesUser.getUnitDistanceDefault(context));
                break;
            case WEIGHT:
                setUnit(DefaultPreferencesUser.getUnitWeightDefault(context));
                break;
            case MIDDLE_SPEED:
                setUnit(DefaultPreferencesUser.getUnitMiddleSpeedDefault(context));
                break;
            case ENERGY:
                setUnit(DefaultPreferencesUser.getUnitEnergyDefault(context));
                break;
            case DURATION:
                setUnit(Measure.Unit.SECOND);
                break;
            }

    }

    public static Measure create(Context context, Measure.Type type){
        return new Measure(context, type);
    }

    public static Measure create(Context context, Measure.Type type, Double value){
        final  Measure measure = new Measure(context, type);
        measure.setValueFromDb(value);
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
        dest.writeDouble(getValueToDb());
        dest.writeString(this.unit.toString());
    }

// END - Parcelable IMPLEMENTATION


    public Context getContext() {
        return context;
    }

    public Type getType() {
        return type;
    }

    public Unit getUnit() {
        return unit;
    }




    public Double getValue(final boolean toDb){
        if(toDb){
            return getValueToDb();
        }else {
            return getValueToGui();
        }
    }

    private Double getValueToDb(){
        if(this.value==null) return 0d;
        return NumberUtilities.round2(this.value);
    }

    private Double getValueToGui(){
        if(this.value==null) return 0d;
        resetUnit();
        switch (this.type){
            case DURATION: return getValueToDb();
            case ENERGY:{
                if(DefaultPreferencesUser.getUnitEnergyDefault(context).equals(Type.ENERGY.getMeasureUnitDefault()))
                    return getValueToDb();
                else return conversionTo(this.type.getMeasureUnit()[1], value);
            }
           case MIDDLE_SPEED: {
               if(DefaultPreferencesUser.getUnitDistanceDefault(context).equals(Type.DISTANCE.getMeasureUnitDefault()))
                   return getValueToDb();
               else
                   return conversionTo(this.type.getMeasureUnit()[1], value);
           }
           default:{
               String unitStr = DefaultPreferencesUser.getUnitDefault(context, this.type.toString().toLowerCase());
               if(unitStr==null || Measure.Unit.valueOf(unitStr).equals(this.type.getMeasureUnitDefault()))
                   return getValueToDb();
               else
                   return conversionTo(this.type.getMeasureUnit()[1], value);

           }
        }
    }


    private void resetUnit(){
        if(!type.equals(Type.DURATION)){
            Measure.Unit u = null;
            switch (this.type){
                case MIDDLE_SPEED:
                    u = DefaultPreferencesUser.getUnitMiddleSpeedDefault(context);
                    break;
                case ENERGY:
                    u = DefaultPreferencesUser.getUnitEnergyDefault(context);
                    break;
                case DISTANCE:
                    u = DefaultPreferencesUser.getUnitDistanceDefault(context);
                    break;
                case HEIGHT:
                    u = DefaultPreferencesUser.getUnitHeightDefault(context);
                    break;
                case WEIGHT:
                    u = DefaultPreferencesUser.getUnitWeightDefault(context);
                    break;
                default:
                    break;
            }
            if(!u.equals(unit)) setUnit(u);
        }
    }



    public void setValue(final boolean fromDb, Double value){
        if(fromDb){
            setValueFromDb(value);
        }else {
            setValueFromGui(value);
        }
    }

    private void setValueFromGui(Double value){
        switch (this.type){
            case DISTANCE:{
                Unit unitS = DefaultPreferencesUser.getUnitDistanceDefault(context);
                this.value = unitS.equals(Unit.MILE) ? conversionTo(Unit.KILOMETER, value): value;
                    break;
            }
            case WEIGHT:{
                Unit unitS = DefaultPreferencesUser.getUnitWeightDefault(context);
                this.value = unitS.equals(Unit.POUND) ? conversionTo(Unit.KILOGRAM, value): value;
                break;
            }
            case HEIGHT:{
                Unit unitS = DefaultPreferencesUser.getUnitHeightDefault(context);
                this.value = unitS.equals(Unit.FEET) ? conversionTo(Unit.METER, value): value;
                break;
            }
            default:
                setValueFromDb(value);
                break;
        }
    }

    private void setValueFromDb(Double value) {
        this.value = value;
    }



    public Measure getMeasure(int integer, int decimal){
        if(this.getType().equals(Measure.Type.DURATION))
            setValueFromDb(DurationUtilities.getSecond(integer, decimal));
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
        return distance==null || distance.getValueToDb() == 0d || distance.getValueToGui() == 0d;
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
                getValueToDb() == 0d)
            return context.getString(R.string.no_available_abbr);

        return getValueToGui() + context.getString(R.string.space) + getUnit().getString();
    }

    @Override
    public String toString() {
        if(context!=null)
            return toString(context);
        return UNSET_CONTEXT;
    }

    public String toString(boolean isHome) {
        if(isHome)
        {
            if(this.type.equals(Type.DURATION))
                return DurationUtilities.format(this.value.intValue());
            return getValueToGui() + context.getString(R.string.space) + getUnit().getString();
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
