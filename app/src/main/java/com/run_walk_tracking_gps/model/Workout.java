package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.enumeration.InfoWorkout;
import com.run_walk_tracking_gps.gui.enumeration.Measure;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.utilities.DateUtilities;
import com.run_walk_tracking_gps.utilities.DurationUtilities;
import com.run_walk_tracking_gps.utilities.MeasureUtilities;

import org.json.JSONException;

import java.util.Date;

public class Workout implements Parcelable {

    private static final String ND = "N.D.";

    private int id_workout;

    private String map_route;
    private String date;
    private int duration;
    private double distance;
    private double calories;
    private double middle_speed;
    private Sport sport;

    public Workout(){}

    private Workout (Workout w){
        this.id_workout = w.id_workout;
        this.map_route = w.map_route;
        this.date = w.date;
        this.duration = w.duration;
        this.distance = w.distance;
        this.calories = w.calories;
        this.middle_speed = w.middle_speed;
        this.sport = w.sport;
    }

    public Workout clone(){
        return new Workout(this);
    }

    protected Workout(Parcel in) {
        id_workout = in.readInt();
        map_route = in.readString();
        sport = Sport.valueOf(in.readString());
        date = in.readString();
        duration = in.readInt();
        distance = in.readDouble();
        calories = in.readDouble();
        middle_speed = in.readDouble();
    }

    // TODO: 11/1/2019 CREATE UN COMPARATOR


    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_workout);
        dest.writeString(map_route);
        dest.writeString(sport.toString());
        dest.writeString(date);
        dest.writeInt(duration);
        dest.writeDouble(distance);
        dest.writeDouble(calories);
        dest.writeDouble(middle_speed);
    }

    public int getIdWorkout() {
        return id_workout;
    }

    public String getMapRoute() {
        return map_route;
    }

    public Date getDate() {
        return DateUtilities.parseStringWithTimeToDateString(date);
    }

    public Sport getSport() {
        return sport;
    }

    public int getDuration() {
        return duration;
    }

    public String getDurationStr(){
        return DurationUtilities.format(this.duration);
    }

    public double getDistance() {
        return distance;
    }

    public double getCalories() {
        return calories;
    }

    public double getMiddleSpeed() {
       return middle_speed;
    }

    public void setIdWorkout(int id_workout) {
        this.id_workout = id_workout;
    }

    public void setMapRoute(String map_route) {
        this.map_route = map_route;
    }

    public void setDate(Date date) {
        this.date = DateUtilities.parseShortToString(date);
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        setMiddleSpeed();
    }

    public void setDistance(double distance) {
        this.distance = distance;
        setMiddleSpeed();
    }

    private void setMiddleSpeed(){
        this.middle_speed = (duration>0f ? this.distance/((double) this.duration/3600) : 0f);
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public String[] toArrayString() {
        return new String[]{this.date, sport.toString() ,
                DurationUtilities.format(this.duration),
                this.distance>0 ?String.valueOf(this.distance) : ND,
                this.calories>0 ?String.valueOf(this.calories) : ND,
                this.middle_speed>0 ?String.valueOf(this.middle_speed) : ND,
                };
    }

    public boolean isMinimalSet(){
        return this.date!=null && this.sport!=null && this.duration!=0;
    }

    public static String valueStr(Context context, InfoWorkout infoWorkout, String detailsWorkout){
        String valueStr = null;
        try {
            double valueDouble = Double.valueOf(detailsWorkout);
            if(valueDouble==0) throw new NullPointerException();

            switch (infoWorkout) {
                case CALORIES:
                    if (available(detailsWorkout))
                        valueStr = MeasureUtilities.energySpeedStr(context, valueDouble);
                    break;
                case DISTANCE:
                    if (Workout.available(detailsWorkout))
                        valueStr = MeasureUtilities.distanceStr(context, valueDouble);
                    break;
                case MIDDLE_SPEED:
                    if (Workout.available(detailsWorkout))
                        valueStr = MeasureUtilities.middleSpeedStr(context, valueDouble);
                    break;
            }

        } catch (JSONException je) {
            je.printStackTrace();
        }catch (NullPointerException e){
            return ND;
        }catch (NumberFormatException n){
            return detailsWorkout;
        }

        return valueStr;
    }

    private static boolean available(String available){
        return !available.equals(ND);
    }

    @Override
    public String toString() {

        return "Workout{ id_workout='" + id_workout +'\''+
                    ", map_route='" + map_route +'\''+
                    ", sport='" + sport +'\''+
                    ", date='" + getDate() + '\'' +
                    ", time='" + duration + '\'' +
                    ", distance='" + distance + '\'' +
                    ", calories='" + calories + '\'' +
                    ", middle_speed='" + middle_speed + '\'' +
                    '}';
    }
}
