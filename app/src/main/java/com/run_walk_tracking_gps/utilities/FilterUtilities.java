package com.run_walk_tracking_gps.utilities;

import com.run_walk_tracking_gps.gui.enumeration.FilterTime;
import com.run_walk_tracking_gps.model.Workout;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FilterUtilities {

    public static Map<Integer, List<Workout>> createMapWorkouts(List<Workout> workouts, FilterTime filter){
        switch (filter){
            case ALL:
                Map<Integer, List<Workout>> map = new HashMap<>();
                IntStream.range(0, workouts.size()).forEach(i -> map.put(i, Collections.singletonList(workouts.get(i))));
                return map;
            case YEAR:
                return map(workouts, Calendar.YEAR);
            case MONTH:
                return map(workouts, Calendar.MONTH);
        }
        return null;
    }

    private static Map<Integer, List<Workout>> map(List<Workout> workouts, int filter){
        return workouts.stream().collect((Collectors.groupingBy(w -> {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(w.getDate());
            return calendar.get(filter) +(Calendar.MONTH==filter? 1 :0);
        }))).entrySet().stream().sorted((o1, o2) -> Integer.compare(o2.getKey(), o1.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

    }
}
