package com.run_walk_tracking_gps;

import android.util.Log;

import com.run_walk_tracking_gps.gui.enumeration.FilterTime;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.WorkoutBuilder;
import com.run_walk_tracking_gps.utilities.DateUtilities;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FilterTest {

    private static final String TAG = FilterTest.class.getName();
    private List<Workout> workouts = new ArrayList<>();

    @Before
    public void init(){

        Workout workout = WorkoutBuilder.create()
                .setDate(DateUtilities.parseStringWithTimeToDateString("05/11/2019 11:42"))
                .setCalories(200)
                .setDistance(30)
                .setDuration(3600)
                .build();
        workouts.add(workout);


        workout = WorkoutBuilder.create()
                                .setDate(DateUtilities.parseStringWithTimeToDateString("03/11/2019 21:07"))
                                .setCalories(200)
                                .setDistance(30)
                                .setDuration(3600)
                                .build();
        workouts.add(workout);

        workout = WorkoutBuilder.create()
                .setDate(DateUtilities.parseStringWithTimeToDateString("02/11/2015 21:07"))
                .setCalories(200)
                .setDistance(30)
                .setDuration(3600)
                .build();
        workouts.add(workout);

        workout = WorkoutBuilder.create()
                .setDate(DateUtilities.parseStringWithTimeToDateString("01/10/2015 21:07"))
                .setCalories(200)
                .setDistance(30)
                .setDuration(3600)
                .build();
        workouts.add(workout);

    }

    @Test
    public void filter(){
        // Log.e(TAG, ""+workouts);
        // YEAR
        final Map<Integer, List<Workout>> mapYear = createMap(workouts, FilterTime.YEAR);
        //Log.e(TAG, ""+mapYear);
        // MONTH
        final Map<Integer, List<Workout>> mapMonth = createMap(workouts, FilterTime.MONTH);
        //Log.e(TAG, ""+mapMonth);
        // ALL
        final Map<Integer, List<Workout>> mapAll = createMap(workouts, FilterTime.ALL);
        Log.e(TAG, ""+mapAll);
    }

    private Map<Integer, List<Workout>> map(int filter){
        return workouts.stream().collect((Collectors.groupingBy(w -> {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(w.getDate());
            return calendar.get(filter) +(Calendar.MONTH==filter? 1 :0);
        }))).entrySet().stream().sorted((o1, o2) -> Integer.compare(o2.getKey(), o1.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

    }

    private Map<Integer, List<Workout>> createMap(List<Workout> workouts, FilterTime filter){
        switch (filter){
            case ALL:
                Map<Integer, List<Workout>> map = new HashMap<>();
                IntStream.range(0, workouts.size()).forEach(i -> map.put(i, Collections.singletonList(workouts.get(i))));
                return map;
            case YEAR:
                return map(Calendar.YEAR);
            case MONTH:
                return map(Calendar.MONTH);
        }
        return null;
    }
}
