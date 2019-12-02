package com.run_walk_tracking_gps;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.enumerations.FilterTime;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.WorkoutBuilder;
import com.run_walk_tracking_gps.utilities.DateHelper;
import com.run_walk_tracking_gps.utilities.FilterUtilities;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterTest {

    private Context context= InstrumentationRegistry.getTargetContext();
    private List<Workout> workouts = new ArrayList<>();
    private Map<Integer, List<Workout>> mapAllWorkouts = new HashMap<>();
    private Map<Integer, List<Workout>> mapMonthWorkouts = new HashMap<>();
    private Map<Integer, List<Workout>> mapYearWorkouts = new HashMap<>();

    //@Before
    public void initWorkout(){

       Workout workout = WorkoutBuilder.create(context)
                .setDate(DateHelper.create(context).parseStringWithTimeToDateString("05/11/2019 11:42"))
                .setCalories(200)
                .setDistance(30)
                .setDuration(3600)
                .build();
        workouts.add(workout);


        workout = WorkoutBuilder.create(context)
                                .setDate(DateHelper.create(context).parseStringWithTimeToDateString("03/11/2019 21:07"))
                                .setCalories(200)
                                .setDistance(30)
                                .setDuration(3600)
                                .build();
        workouts.add(workout);

        workout = WorkoutBuilder.create(context)
                .setDate(DateHelper.create(context).parseStringWithTimeToDateString("02/11/2015 21:07"))
                .setCalories(200)
                .setDistance(30)
                .setDuration(3600)
                .build();
        workouts.add(workout);

        workout = WorkoutBuilder.create(context)
                .setDate(DateHelper.create(context).parseStringWithTimeToDateString("01/10/2015 21:07"))
                .setCalories(200)
                .setDistance(30)
                .setDuration(3600)
                .build();
        workouts.add(workout);

        //YEAR
        mapYearWorkouts.put(2019, Arrays.asList(workouts.get(0), workouts.get(1)));
        mapYearWorkouts.put(2015, Arrays.asList(workouts.get(2), workouts.get(3)));

        // MONTH
        mapMonthWorkouts.put(11, Arrays.asList(workouts.get(0), workouts.get(1), workouts.get(2)));
        mapMonthWorkouts.put(10, Collections.singletonList(workouts.get(3)));

        // ALL
        mapAllWorkouts.put(0, Collections.singletonList(workouts.get(0)));
        mapAllWorkouts.put(1, Collections.singletonList(workouts.get(1)));
        mapAllWorkouts.put(2, Collections.singletonList(workouts.get(2)));
        mapAllWorkouts.put(3, Collections.singletonList(workouts.get(3)));
    }

    //@Test
    public void filterWorkouts(){
        // YEAR
        final Map<Integer, List<Workout>> mapYear = FilterUtilities.createMapWorkouts(workouts, FilterTime.YEAR);
        Assert.assertEquals(mapYearWorkouts, mapYear);
        // MONTH
        final Map<Integer, List<Workout>> mapMonth = FilterUtilities.createMapWorkouts(workouts, FilterTime.MONTH);
        Assert.assertEquals(mapMonthWorkouts, mapMonth);
        // ALL
        final Map<Integer, List<Workout>> mapAll = FilterUtilities.createMapWorkouts(workouts, FilterTime.ALL);
        Assert.assertEquals(mapAllWorkouts, mapAll);
    }

    private List<StatisticsData> statistics = new ArrayList<>();

    private List<StatisticsData> statisticsLastWeek = new ArrayList<>();
    private List<StatisticsData> statisticsLastMonth = new ArrayList<>();
    private List<StatisticsData> statisticsLastYear = new ArrayList<>();

    //@Before
    public void initStatisticsData() throws ParseException {

        final  StatisticsData statisticsData = StatisticsData.create(context, Measure.Type.WEIGHT);
        statisticsData.setDate(DateHelper.create(context).parseShortToDate("02/11/2019"));
        statisticsData.setValue(73.0);
        statistics.add(statisticsData);

        statisticsData.setDate(DateHelper.create(context).parseShortToDate("27/10/2019"));
        statisticsData.setValue(74.0);
        statistics.add(statisticsData);


        statisticsData.setDate(DateHelper.create(context).parseShortToDate("26/10/2019"));
        statisticsData.setValue(74.0);
        statistics.add(statisticsData);

        statisticsData.setDate(DateHelper.create(context).parseShortToDate("25/10/2019"));
        statisticsData.setValue(74.5);
        statistics.add(statisticsData);

        statisticsData.setDate(DateHelper.create(context).parseShortToDate("24/10/2019"));
        statisticsData.setValue(74.5);
        statistics.add(statisticsData);

        statisticsData.setDate(DateHelper.create(context).parseShortToDate("10/10/2019"));
        statisticsData.setValue(74.5);
        statistics.add(statisticsData);

        statisticsData.setDate(DateHelper.create(context).parseShortToDate("10/10/2016"));
        statisticsData.setValue(74.5);
        statistics.add(statisticsData);

        // LAST WEEK (from [lastInsert - 1 week] to lastInsert )
        statisticsLastWeek.addAll(Arrays.asList(statistics.get(0), statistics.get(1),statistics.get(2)));
        System.out.println(statisticsLastWeek);

        // LAST MONTH (from [lastInsert - 1 month] to lastInsert )
        statisticsLastMonth.addAll(Arrays.asList(statistics.get(0), statistics.get(1), statistics.get(2),
                statistics.get(3),statistics.get(4), statistics.get(5)));
        System.out.println(statisticsLastMonth);

        // LAST YEAR (from [lastInsert - 1 year] to lastInsert )
        statisticsLastYear.addAll(Arrays.asList(statistics.get(0), statistics.get(1), statistics.get(2),
                statistics.get(3),statistics.get(4), statistics.get(5)));
        System.out.println(statisticsLastYear);
    }

    @Test
    public void filterStatisticsData(){
        final  List<StatisticsData> statisticsDataAll = FilterUtilities.createListFilteredStatisticsData(context, statistics, FilterTime.ALL);
        Assert.assertEquals(statistics, statisticsDataAll);

        final  List<StatisticsData> sLastWeek = FilterUtilities.createListFilteredStatisticsData(context,statistics, FilterTime.WEEK);
        Assert.assertEquals(sLastWeek, statisticsLastWeek);

        final  List<StatisticsData> sLastMonth = FilterUtilities.createListFilteredStatisticsData(context,statistics, FilterTime.MONTH);
        Assert.assertEquals(sLastMonth, statisticsLastMonth);

        final  List<StatisticsData> sLastYear = FilterUtilities.createListFilteredStatisticsData(context,statistics, FilterTime.YEAR);
        Assert.assertEquals(sLastYear, statisticsLastYear);
    }

}
