package com.run_walk_tracking_gps.model;

import com.run_walk_tracking_gps.model.enumerations.Sport;
import java.util.Date;

public class WorkoutBuilder {

    private Workout workout;

    private WorkoutBuilder(){
        workout = new Workout();
    }

    public static WorkoutBuilder create(){
        return new WorkoutBuilder();
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
        workout.setDuration(duration);
        return this;
    }

    public WorkoutBuilder setDistance(double distance){
        workout.setDistance(distance);
        return this;
    }

    public WorkoutBuilder setCalories(double calories){
        workout.setCalories(calories);
        return this;
    }

    public WorkoutBuilder setMiddleSpeed(double middleSpeed) {
        workout.setMiddleSpeed(middleSpeed);
        return this;
    }

    public WorkoutBuilder setSport(Sport sport){
        workout.setSport(sport);
        return this;
    }
}
