package com.run_walk_tracking_gps.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Map;

public class StatisticsData implements Parcelable{

    private Date date;
    private Double data;

    public StatisticsData() {
    }

    public StatisticsData(final Date date, final Double statisticData) {
        this.date = date;
        this.data = statisticData;
    }

    protected StatisticsData(Parcel in) {
        date = new Date(in.readLong());
        data = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date.getTime());
        dest.writeDouble(data);
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


    public void setDate(Date key) {
        this.date = key;
    }

    public void setStatisticData(Double value) {
        this.data = value;
    }

    public Date getDate() {
        return date;
    }

    public Double getStatisticData() {
        return data;
    }

    public boolean isSet() {
        return data!=null && date!=null;
    }


    @Override
    public String toString() {
        return "StatisticsData [ Date = " +this.date +", Value = " +this.data+ " ]";
    }
}