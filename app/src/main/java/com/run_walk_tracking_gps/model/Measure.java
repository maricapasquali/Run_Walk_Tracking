package com.run_walk_tracking_gps.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.db.tables.SettingsDescriptor;
import com.run_walk_tracking_gps.utilities.ConversionUnitUtilities;
import com.run_walk_tracking_gps.utilities.NumberUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

        public static HashMap<Type, Unit> getAllDefault(JSONObject unitDefaultJson) throws JSONException {
            final HashMap<Type, Unit> unit = new HashMap<>();
            unit.put(Type.ENERGY, Unit.valueOf(unitDefaultJson.getString(SettingsDescriptor.UnitMeasureDefault.ENERGY)));
            unit.put(Type.DISTANCE, Unit.valueOf(unitDefaultJson.getString(SettingsDescriptor.UnitMeasureDefault.DISTANCE)));
            unit.put(Type.WEIGHT, Unit.valueOf(unitDefaultJson.getString(SettingsDescriptor.UnitMeasureDefault.WEIGHT)));
            unit.put(Type.HEIGHT, Unit.valueOf(unitDefaultJson.getString(SettingsDescriptor.UnitMeasureDefault.HEIGHT)));
            unit.put(Type.MIDDLE_SPEED, unit.get(Type.DISTANCE)!=null && unit.get(Type.DISTANCE).equals(MILE)? MILE_PER_HOUR : KILOMETER_PER_HOUR);
            unit.put(Type.DURATION, SECOND);
            return unit;
        }

        public String getString() {
            return this.str;
        }

        public static Unit of(String str){
            return Stream.of(values()).filter(u -> u.str.equals(str)).findFirst().orElse(null);
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
                case DURATION:
                    measureUnits = new Unit[]{Unit.SECOND, null};
                    break;
            }
            return measureUnits;
        }

        public static HashMap<Type, Unit> getMapMeasureUnitDefault(){
            //HashMap<Type, Unit> map = new HashMap<>();Stream.of(values()).forEach(t -> map.put(t, t.getMeasureUnitDefault())); return map;
            return Stream.of(values()).collect(Collectors.toMap(t -> t, Type::getMeasureUnitDefault, (prev, next) -> next, HashMap::new));
        }

    }

    private static final String UNSET_CONTEXT = "Context not set";
    private Context context;

    private Type type;
    private Double value;
    private Unit unit;

    private HashMap<Type, Unit> unitsDefault;

    private Measure(Measure measure){
        this.context = measure.context;
        this.type = measure.type;
        this.value = measure.value;
        this.unit = measure.unit;
        this.unitsDefault = measure.unitsDefault;
    }

    private Measure(Context context, Measure.Type type){
        this.context = context;
        this.type = type;
        this.value = 0d;
        try {
            final JSONObject unitDefaultJson = DaoFactory.getInstance(context).getSettingDao().getUnitMeasureDefault();
            unitsDefault = unitDefaultJson==null ? Type.getMapMeasureUnitDefault() : Measure.Unit.getAllDefault(unitDefaultJson);
            switch (type){
                case HEIGHT:
                    setUnit(unitsDefault.get(Type.HEIGHT));//DefaultPreferencesUser.getUnitHeightDefault(context));
                    break;
                case DISTANCE:
                    setUnit(unitsDefault.get(Type.DISTANCE)); //DefaultPreferencesUser.getUnitDistanceDefault(context));
                    break;
                case WEIGHT:
                    setUnit(unitsDefault.get(Type.WEIGHT)); //DefaultPreferencesUser.getUnitWeightDefault(context));
                    break;
                case MIDDLE_SPEED:
                    setUnit(unitsDefault.get(Type.MIDDLE_SPEED));//DefaultPreferencesUser.getUnitMiddleSpeedDefault(context));
                    break;
                case ENERGY:
                    setUnit(unitsDefault.get(Type.ENERGY)); //DefaultPreferencesUser.getUnitEnergyDefault(context));
                    break;
                case DURATION:
                    setUnit(unitsDefault.get(Type.DURATION));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

        int sizeMap = 6;
        this.unitsDefault = new HashMap<Type, Unit>(sizeMap);
        IntStream.range(0, sizeMap).forEach(i -> this.unitsDefault.put(Type.valueOf(in.readString()), Unit.valueOf(in.readString())));
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

        for (Map.Entry<Type, Unit> entry : this.unitsDefault.entrySet()) {
            dest.writeString(entry.getKey().toString());
            dest.writeString(entry.getValue().toString());
        }
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

    private Double get(Type typeToSet){
        if(unitsDefault.get(typeToSet).equals(typeToSet.getMeasureUnitDefault()))
            return getValueToDb();
        else
            return conversionTo(this.type.getMeasureUnit()[1], this.value);
    }

    private Double getValueToGui(){
        if(this.value==null) return 0d;
        resetUnit();
        switch (this.type){
            case DURATION: {
                return getValueToDb();
            }
            case ENERGY:{
                return get(Type.ENERGY);
            }
            case MIDDLE_SPEED: {
               return get(Type.MIDDLE_SPEED);
            }
            case WEIGHT: {
                return get(Type.WEIGHT);
            }
            case HEIGHT: {
                return get(Type.HEIGHT);
            }
            case DISTANCE: {
                return get(Type.DISTANCE);
            }
        }
        return this.value;
    }

    private void resetUnit(){
        if(!type.equals(Type.DURATION)){
            try {
                Measure.Unit u = null;
                final JSONObject unitDefaultJson = DaoFactory.getInstance(context).getSettingDao().getUnitMeasureDefault();
                unitsDefault = unitDefaultJson==null ? Type.getMapMeasureUnitDefault() : Unit.getAllDefault(unitDefaultJson);
                switch (this.type){
                    case MIDDLE_SPEED:
                        u = unitsDefault.get(Type.MIDDLE_SPEED); //DefaultPreferencesUser.getUnitMiddleSpeedDefault(context);
                        break;
                    case ENERGY:
                        u = unitsDefault.get(Type.ENERGY); //DefaultPreferencesUser.getUnitEnergyDefault(context);
                        break;
                    case DISTANCE:
                        u = unitsDefault.get(Type.DISTANCE);//DefaultPreferencesUser.getUnitDistanceDefault(context);
                        break;
                    case HEIGHT:
                        u = unitsDefault.get(Type.HEIGHT);//DefaultPreferencesUser.getUnitHeightDefault(context);
                        break;
                    case WEIGHT:
                        u = unitsDefault.get(Type.WEIGHT);//DefaultPreferencesUser.getUnitWeightDefault(context);
                        break;
                    default:
                        break;
                }
                if(!u.equals(unit)) setUnit(u);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                Unit unitS = unitsDefault.get(Type.DISTANCE); //DefaultPreferencesUser.getUnitDistanceDefault(context);
                this.value = unitS.equals(Unit.MILE) ? conversionTo(Unit.KILOMETER, value): value;
                    break;
            }
            case WEIGHT:{
                Unit unitS = unitsDefault.get(Type.WEIGHT); //DefaultPreferencesUser.getUnitWeightDefault(context);
                this.value = unitS.equals(Unit.POUND) ? conversionTo(Unit.KILOGRAM, value): value;
                break;
            }
            case HEIGHT:{
                Unit unitS = unitsDefault.get(Type.HEIGHT);//DefaultPreferencesUser.getUnitHeightDefault(context);
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
            setValueFromDb(Utilities.getSecond(integer, decimal));
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

    public void setUnit(String unit) {
        this.unit = Measure.Unit.valueOf(unit);
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
            return Utilities.format(this.value.intValue());

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
                return Utilities.format(this.value.intValue());
            return getValueToGui() + context.getString(R.string.space) + getUnit().getString();
        }
        return toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measure measure = (Measure) o;
        return type == measure.type &&
                Objects.equals(value, measure.value) &&
                unit == measure.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value, unit);
    }

    public static class Utilities {

        public static String formatMeasure(final Double value, final Unit unit){
            return NumberUtilities.round2(value)+ " " + unit.str;
        }

        private static double getSecond(int integer, int decimal){
            return (decimal*60)+(integer*3600);
        }

        public static String format(String time){
            return format(toSeconds(time));
        }

        public static Integer toSeconds(String duration){
            try{
                int hours = 0, minutes = 0, sec = 0;
                String[] split = duration.split(":");
                if(split.length==2){
                    minutes = Integer.valueOf(split[0]);
                    sec = Integer.valueOf(split[1]);

                }else{
                    hours = Integer.valueOf(split[0]);
                    minutes = Integer.valueOf(split[1]);
                    sec = Integer.valueOf(split[2]);
                }
                return sec + (minutes*60)+(hours*3600);

            }catch (PatternSyntaxException e){
                return 0;
            }
        }

        public static List<Long> time(long seconds){
            long hours = seconds / 3600;
            long minutes = (seconds /60) % 60;
            long sec = seconds%60;
            return Arrays.asList(hours, minutes, sec);
        }

        @SuppressLint("DefaultLocale")
        public static String format(long seconds){
            final List<Long> time = time(seconds);
            return String.format(Measure.Format.FORMAT_DURATION, time.get(0), time.get(1), time.get(2));
        }


        private static List<Long> timeMill(long milSeconds){
            return time(milSeconds/1000);
        }

        @SuppressLint("DefaultLocale")
        public static String formatMill(long millSeconds){
            final List<Long> time = timeMill(millSeconds);
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
