package com.run_walk_tracking_gps.model;

import android.content.Context;

import com.run_walk_tracking_gps.model.enumerations.Sport;
import java.util.Date;

public class WorkoutBuilder {

    private Workout workout;

    private WorkoutBuilder(Context context){
        workout = new Workout(context);
    }

    public static WorkoutBuilder create(Context context){
        return new WorkoutBuilder(context);
    }

    public Workout build(){
        return workout;
    }

    public WorkoutBuilder setMapRoute(String mapRoute){
        workout.setMapRoute(mapRoute);
        return this;
    }

    public WorkoutBuilder setDate(Date date){
        workout.setDate(date);
        return this;
    }

    public WorkoutBuilder setDuration(int duration){
        workout.getDuration().setValue((double)duration);
        return this;
    }

    public WorkoutBuilder setDistance(double distance){
        workout.getDistance().setValueFromGui(distance);
        return this;
    }

    public WorkoutBuilder setMiddleSpeed(){
        workout.setMiddleSpeed();
        return this;
    }

    public WorkoutBuilder setCalories(double calories){
        workout.getCalories().setValue(calories);
        return this;
    }

    public WorkoutBuilder setSport(Sport sport){
        workout.setSport(sport);
        return this;
    }
}
