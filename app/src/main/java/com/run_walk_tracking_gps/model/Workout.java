package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.utilities.DateHelper;
import com.run_walk_tracking_gps.utilities.NumberUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Workout implements Parcelable, Cloneable{

    private Context context;

    private int id_workout;
    private String map_route;
    private Date date;
    private ArrayList<Measure> parameters;
    private Sport sport;

    public Workout(){}

    public Workout(Context context){
        parameters = new ArrayList<>();
        parameters.add(Measure.create(context, Measure.Type.DURATION));
        parameters.add(Measure.create(context, Measure.Type.DISTANCE));
        parameters.add(Measure.create(context, Measure.Type.ENERGY));
        parameters.add(Measure.create(context, Measure.Type.MIDDLE_SPEED));
        sport = DefaultPreferencesUser.getSportDefault(context);

        setContext(context);
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
                workoutsList.get(i).setIdWorkout(w.getInt(HttpRequest.Constant.ID_WORKOUT));
                String map = w.getString(HttpRequest.Constant.MAP_ROUTE);
                if(!map.equals("null")) workoutsList.get(i).setMapRoute(map);
                workoutsList.get(i).setDate(w.getString(HttpRequest.Constant.DATE));
                workoutsList.get(i).getDuration().setValue(true, (double)w.getInt(HttpRequest.Constant.DURATION));
                workoutsList.get(i).getDistance().setValue(true, w.getDouble(HttpRequest.Constant.DISTANCE));
                workoutsList.get(i).getCalories().setValue(true, w.getDouble(HttpRequest.Constant.CALORIES));
                workoutsList.get(i).setSport(Sport.valueOf(w.getString(HttpRequest.Constant.SPORT)));
                double duration = workoutsList.get(i).getDuration().getValue(true);
                double distance = workoutsList.get(i).getDistance().getValue(true);
                workoutsList.get(i).setMiddleSpeed(distance, duration);
            }catch (JSONException e){
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return workoutsList;
    }

    private void setMiddleSpeed(double distance,double duration){
        getMiddleSpeed().setValue(true, NumberUtilities.round2(distance==0d ? 0d : (duration>0d ? distance/(duration/3600) : 0d)));
    }

    public void setMiddleSpeed(){
        double duration = getDuration().getValue(true);
        double distance = getDistance().getValue(true);
        setMiddleSpeed(distance,duration);
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
        return parameters.get(0).getContext();
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
        return DateHelper.create(getContext()).formatShortDateTimeToString(date);
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
        this.parameters.forEach(p -> p.setContext(context));
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

    public void setDate(String date) throws ParseException {
        this.date = DateHelper.create(getContext()).parseShortDateTimeToDate(date);
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    // TODO: 11/15/2019 MIGLIORARE
    public LinkedHashMap<Workout.Info, Object> details(final boolean withMiddleSpeed){
        final LinkedHashMap<Workout.Info, Object> map = new LinkedHashMap<>();
        map.put(Info.DATE, this.getDateStr());
        map.put(Info.SPORT, this.getSport());
        map.put(Info.TIME, this.getDuration().toString());
        map.put(Info.DISTANCE,this.getDistance().toString() );
        map.put(Info.CALORIES, this.getCalories().toString());
        if(withMiddleSpeed) map.put(Info.MIDDLE_SPEED, this.getMiddleSpeed().toString());
        return map;
    }

    public boolean isMinimalSet(){
        return this.date!=null && this.sport!=null && !Measure.isNullOrEmpty(getDuration());
    }

    public JSONObject toJson(Context context) throws JSONException {
        return new JSONObject()
                .put(HttpRequest.Constant.ID_USER, Integer.valueOf(DefaultPreferencesUser.getIdUserLogged(context)))
                .put(HttpRequest.Constant.MAP_ROUTE, this.getMapRoute())
                .put(HttpRequest.Constant.DATE, this.getDate())
                .put(HttpRequest.Constant.DURATION, this.getDuration().getValue(true))
                .put(HttpRequest.Constant.DISTANCE, this.getDistance().getValue(true))
                .put(HttpRequest.Constant.CALORIES, this.getCalories().getValue(true))
                .put(HttpRequest.Constant.SPORT, this.getSport());
    }

    @Override
    public String toString() {

        return "Workout{ id_workout='" + id_workout +'\''+
                        ", map_route='" + map_route +'\''+
                        ", sport='" + sport +'\''+
                        ", date='" + getDateStr() + '\'' +
                        ", duration='" + getDuration().toString()  + '\'' +
                        ", distance='" + getDistance().toString()  + '\'' +
                        ", calories='" + getCalories().toString()  + '\'' +
                        ", middleSpeed='" + getMiddleSpeed().toString()  + '\'' +
                        '}';
    }

    public enum Info {

        DATE(R.string.date, R.drawable.ic_calendar),
        SPORT(R.string.sport),
        TIME(Measure.Type.DURATION),
        DISTANCE(Measure.Type.DISTANCE),
        CALORIES(Measure.Type.ENERGY),
        MIDDLE_SPEED(Measure.Type.MIDDLE_SPEED);

        private final int strId;
        private final int iconId;

        Info(int strId) {
            this.strId = strId;
            this.iconId = 0;
        }

        Info(int strId, int iconId) {
            this.strId = strId;
            this.iconId = iconId;
        }

        Info(Measure.Type measure) {
            this.strId = measure.getStrId();
            this.iconId = measure.getIconId();
        }

        public int getStrId() {
            return this.strId;
        }

        public int getIconId() {
            return this.iconId;
        }

        public static boolean isSport(Info info) {
            return info.equals(SPORT);
        }

        public static Info[] infoWorkoutNoSpeed() {
            return new ArrayList<>(Arrays.asList(values())).stream().filter(i -> !i.equals(MIDDLE_SPEED)).toArray(Info[]::new);
        }

        public static boolean valuesNotRequired(Info item) {
            return item.equals(Info.DISTANCE) || item.equals(Info.CALORIES);
        }

    }

}
