package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.utilities.DateUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Workout implements Parcelable, Cloneable{

    private Context context;

    private int id_workout;
    private String map_route;
    private Date date;
    private ArrayList<Measure> parameters;
    private Sport sport;

    private static String ND;

    public Workout(Context context){
        this.context = context;
        ND = context.getString(R.string.no_available_abbr);
        parameters = new ArrayList<>();
        parameters.add(Measure.create(context, Measure.Type.DURATION));
        parameters.add(Measure.create(context, Measure.Type.DISTANCE));
        parameters.add(Measure.create(context, Measure.Type.ENERGY));
        parameters.add(Measure.create(context, Measure.Type.MIDDLE_SPEED));
    }

    private Workout(Workout w){
        this.context = w.context;
        this.id_workout = w.id_workout;
        this.map_route = w.map_route;
        this.date = w.date;
        this.parameters = w.parameters.stream().map(Measure::clone).collect(Collectors.toCollection(ArrayList::new));
        this.sport = w.sport;
    }

    public Workout clone() {
        return new Workout(this);
    }

    // TODO: 11/14/2019 MIGLIORARE
    public static ArrayList<Workout> createList(Context context,JSONArray workouts) {
        final ArrayList<Workout> workoutsList = (ArrayList<Workout>) Stream.generate(() -> new Workout(context))
                .limit(workouts.length())
                .collect(Collectors.toList());
        for (int i = 0; i < workouts.length(); i++){

            try {
                JSONObject w = (JSONObject)workouts.get(i);
                workoutsList.get(i).setIdWorkout(w.getInt("id_workout"));
                String map = w.getString("map_route");
                if(!map.equals("null")) workoutsList.get(i).setMapRoute(map);
                workoutsList.get(i).setDate(w.getString("date"));
                workoutsList.get(i).setDuration(w.getInt("duration"));
                workoutsList.get(i).setDistance(w.getDouble("distance"));
                workoutsList.get(i).setCalories(w.getDouble("calories"));
                workoutsList.get(i).setSport(Sport.valueOf(w.getString("sport")));
                workoutsList.get(i).setMiddleSpeed(w.getDouble("middle_speed"));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return workoutsList;
    }


// START - Parcelable IMPLEMENTATION
    protected Workout(Parcel in) {
        id_workout = in.readInt();
        map_route = in.readString();
        date = new Date(in.readLong());
        parameters = new ArrayList<>();
        in.readTypedList(parameters, Measure.CREATOR);
        sport = Sport.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_workout);
        dest.writeString(map_route);
        dest.writeLong(date.getTime());
        dest.writeTypedList(parameters);
        dest.writeString(sport.toString());
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

// END - Parcelable IMPLEMENTATION

    public Context getContext() {
        return context;
    }

    public int getIdWorkout() {
        return id_workout;
    }

    public String getMapRoute() {
        return map_route;
    }

    public Date getDate() {
        return date;
    }

    private String getDateStr() {
        return DateUtilities.parseShortToString(date);
    }

    public Sport getSport() {
        return sport;
    }

    public Measure getDuration() {
        return parameters.get(0);
    }

    public Measure getDistance() {
        return parameters.get(1);
    }

    public Measure getCalories() {
        return parameters.get(2);
    }

    public Measure getMiddleSpeed() {
        return parameters.get(3);
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void setIdWorkout(int id_workout) {
        this.id_workout = id_workout;
    }

    public void setMapRoute(String map_route) {
        this.map_route = map_route;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(String date) {
        this.date = DateUtilities.parseStringWithTimeToDateString(date);
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public void setDuration(Integer duration) {
        getDuration().setValue(Double.valueOf(duration));
        setMiddleSpeed();
    }

    public void setDistance(Double distance) {
        getDistance().setValue(distance);
        setMiddleSpeed();
    }

    private void setMiddleSpeed(){
        int duration = getDuration().getValue().intValue();
        double distance = Measure.isNullOrEmpty(getDistance()) ? 0d : getDistance().getValue();

        setMiddleSpeed(distance==0d ? 0d : (duration>0d ? distance/(duration/3600) : 0d));
    }

    private void setMiddleSpeed(Double middleSpeed) {
        getMiddleSpeed().setValue(middleSpeed);
    }

    public void setCalories(Double calories) {
        getCalories().setValue(calories);
    }

    public String[] toArrayString() {
        return new String[]{getDateStr(), sport.toString() ,
                getDuration().toString(context),
                Measure.isNullOrEmpty(getDistance()) ? ND : getDistance().toString(context),
                Measure.isNullOrEmpty(getCalories()) ? ND : getCalories().toString(context),
                Measure.isNullOrEmpty(getMiddleSpeed()) ? ND : getMiddleSpeed().toString(context)
                };
    }

    public boolean isMinimalSet(){
        return this.date!=null && this.sport!=null && !Measure.isNullOrEmpty(getDuration());
    }

    @Override
    public String toString() {

        return "Workout{ id_workout='" + id_workout +'\''+
                    ", map_route='" + map_route +'\''+
                    ", sport='" + sport +'\''+
                    ", date='" + getDateStr() + '\'' +
                    ", duration='" + getDuration().toString(context)  + '\'' +
                    ", distance='" + getDistance().toString(context)  + '\'' +
                    ", calories='" + getCalories().toString(context)  + '\'' +
                    ", middleSpeed='" + getMiddleSpeed().toString(context)  + '\'' +
                    '}';
    }
}
