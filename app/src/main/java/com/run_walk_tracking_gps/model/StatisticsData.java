package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.enumeration.Measure;
import com.run_walk_tracking_gps.gui.enumeration.MeasureUnit;
import com.run_walk_tracking_gps.utilities.DateUtilities;

import org.json.JSONException;

import java.util.Date;
import java.util.Map;

public class StatisticsData implements Parcelable{

    private int id;
    private Date date;
    private Double data;

    public StatisticsData() {
    }

    public StatisticsData(final Date date, final Double statisticData) {
        this.date = date;
        this.data = statisticData;
    }

    private StatisticsData(StatisticsData statisticData) {
        this(statisticData.date, statisticData.data);
        this.id=statisticData.getId();
    }

    public StatisticsData clone(){
        return new StatisticsData(this);
    }

    protected StatisticsData(Parcel in) {
        id = in.readInt();
        date = new Date(in.readLong());
        data = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date key) {
        this.date = key;
    }

    public void setStatisticData(Double value) {
        this.data = value;
    }

    public Date getDate() {
        return date;
    }

    public String getDateStr(){
        return DateUtilities.parseFullToString(date);
    }

    public Double getStatisticData() {
        return data;
    }

    public String getStatisticDataStr(Context context, Measure measure)  {
        StringBuilder stringBuilder = new StringBuilder(data + context.getString(R.string.space));
        try {
            switch (measure){
                case DISTANCE:
                    stringBuilder.append(Preferences.getUnitDistanceDefault(context));
                    break;
                case MIDDLE_SPEED: stringBuilder.append(Preferences.getUnitMiddleSpeedDefault(context));
                    break;
                case ENERGY: stringBuilder.append(Preferences.getUnitEnergyDefault(context));
                    break;
                case WEIGHT:
                    stringBuilder.append(Preferences.getUnitWeightDefault(context));
                    break;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public boolean isSet() {
        return data!=null && date!=null;
    }


    @Override
    public String toString() {
        return "StatisticsData [ Id = "+this.id+", Date = " +this.date +", Value = " +this.data+ " ]";
    }
}