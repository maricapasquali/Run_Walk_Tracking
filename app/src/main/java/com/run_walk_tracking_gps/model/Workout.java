package com.run_walk_tracking_gps.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

import com.run_walk_tracking_gps.utilities.DateUtilities;
import com.run_walk_tracking_gps.model.enumerations.Sport;

import java.util.Date;

public class Workout implements Parcelable {

    private int id;
    private Sport sport;
    private Date date;
    private String time;
    private String distance = "N.D.";
    private String calories = "N.D.";
    private String middle_speed = "N.D.";

    public Workout(){}

    private Workout(Date date, String time, String distance, String calories) {
        this.date = date;
        this.time = time;
        this.distance = distance;
        this.calories = calories;
    }

    public Workout(Sport sport) {
        this.setSport(sport);
    }

    private Workout(Sport sport, Date date, String time, String distance, String calories, String middle_speed) {
        this.sport = sport;
        this.date = date;
        this.time = time;
        this.distance = distance;
        this.calories = calories;
        this.middle_speed = middle_speed;
    }

    protected Workout(Parcel in) {
        id = in.readInt();
        date = new Date(in.readLong());
        sport = Sport.valueOf(in.readString());
        time = in.readString();
        distance = in.readString();
        calories = in.readString();
        middle_speed = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(date.getTime());
        dest.writeString(sport.name());
        dest.writeString(time);
        dest.writeString(distance);
        dest.writeString(calories);
        dest.writeString(middle_speed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public static Workout create(Date date, String time, String distance, String calories) {
        return new Workout(date, time, distance, calories);
    }

    public static Workout create(Sport sport, Date date, String time, String distance, String calories, String middle_speed) {
        return new Workout(sport, date, time, distance, calories, middle_speed);
    }

    public int getId() {
        return id;
    }

    public Sport getSport() {
        return sport;
    }

    public Date getDate() {
        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDateString() {
        return DateUtilities.parseShortToString(this.date);
    }

    public String getTime() {
        return time;
    }

    public String getDistance() {
        return distance;
    }

    public String getCalories() {
        return calories;
    }

    public String getMiddleSpeed() {
        return middle_speed;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public void setMiddleSpeed(String middle_speed) {
        this.middle_speed = middle_speed;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String[] toArrayString() {
        return new String[]{this.getDateString(), this.sport.toString(), this.time, this.distance, this.calories, this.middle_speed};
    }

    public boolean isMinimalSet(){
        return this.date!=null && this.sport!=null && this.time!=null;
    }

    @Override
    public String toString() {
        return "Workout{ Id=" + id +
                ", sport='" + sport +'\''+
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", distance='" + distance + '\'' +
                ", calories='" + calories + '\'' +
                ", middle_speed='" + middle_speed + '\'' +
                '}';
    }
}
