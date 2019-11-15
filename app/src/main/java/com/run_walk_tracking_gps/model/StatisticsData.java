package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.gui.ApplicationActivity;
import com.run_walk_tracking_gps.utilities.DateUtilities;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StatisticsData implements Parcelable{

    private int id;
    private Date date;
    private Measure measure;

    private StatisticsData(StatisticsData statisticsData) {
        this.id = statisticsData.id;
        this.date = statisticsData.date;
        this.measure = statisticsData.measure.clone();
    }

    private StatisticsData(Measure measure) {
        this.measure = measure;
    }

    private StatisticsData(Context context, Measure.Type measure){
        this.measure = Measure.create(context, measure);
    }

    public static StatisticsData create(Measure measure){
        return new StatisticsData(measure);
    }

    public static StatisticsData create(Context context, Measure.Type measure){
        return new StatisticsData(context, measure);
    }

    public StatisticsData clone(){
        return new StatisticsData(this);
    }

// START - Parcelable IMPLEMENTATION
    protected StatisticsData(Parcel in) {
        id = in.readInt();
        date = new Date(in.readLong());
        measure = in.readParcelable(Measure.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(date.getTime());
        dest.writeParcelable(measure, 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StatisticsData> CREATOR = new Creator<StatisticsData>() {
        @Override
        public StatisticsData createFromParcel(Parcel in) {
            return new StatisticsData(in);
        }

        @Override
        public StatisticsData[] newArray(int size) {
            return new StatisticsData[size];
        }
    };

// END - Parcelable IMPLEMENTATION

    public Measure getMeasure() {
        return measure;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getDateStr(){
        return DateUtilities.parseFullToString(date);
    }

    public Double getValue() {
        return measure.getValue();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) throws ParseException {
        this.date = DateUtilities.parseShortToDate(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setValue(Double value) {
        this.measure.setValue(value);
    }

    public List<String> toArrayListString(Context context){
        return Arrays.asList(getMeasure().toString(context), DateUtilities.parseShortToStringNoTime(date));
    }

    public boolean isSet() {
        return !Measure.isNullOrEmpty(measure) && date!=null;
    }

    @Override
    public String toString() {
        return "StatisticsData [ Id = "+this.id+", Date = " +getDateStr() +", Value = " +this.measure.getValue()+ " ]";
    }

    public void setContext(Context context) {
        this.measure.setContext(context);
    }
}