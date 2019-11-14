package com.run_walk_tracking_gps.model;

import android.content.Context;

import java.text.ParseException;
import java.util.Date;

public class StatisticsBuilder {

    private StatisticsData statisticsData;

    private StatisticsBuilder(Context context, Measure.Type measure){
        statisticsData = StatisticsData.create(Measure.create(context, measure));
    }

    private static StatisticsBuilder create(Context context, Measure.Type measure){
        return new StatisticsBuilder(context, measure) ;
    }

    public static StatisticsBuilder createStatisticWeight(Context context){
        return create(context, Measure.Type.WEIGHT);
    }

    public static StatisticsBuilder createStatisticMiddleSpeed(Context context){
        return create(context, Measure.Type.MIDDLE_SPEED);
    }

    public static StatisticsBuilder createStatisticEnergy(Context context){
        return create(context, Measure.Type.ENERGY);
    }

    public static StatisticsBuilder createStatisticDistance(Context context){
        return create(context, Measure.Type.DISTANCE);
    }

    public StatisticsBuilder setValue(Double value){
        statisticsData.setValue(value);
        return this;
    }

    public StatisticsBuilder setDate(String date) throws ParseException {
        statisticsData.setDate(date);
        return this;
    }

    public StatisticsBuilder setDate(Date date) {
        statisticsData.setDate(date);
        return this;
    }

    public StatisticsData build(){
        return statisticsData;
    }

}
