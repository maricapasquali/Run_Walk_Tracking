package com.run_walk_tracking_gps.utilities;

import android.content.Context;
import android.support.v4.util.Pair;
import android.util.Log;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.enumerations.Language;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.zip.DataFormatException;


public final class DateHelper {
    private final static String TAG = DateHelper.class.getName();

    private int HOUR;
    private boolean is24H;
    private static Locale locale;
    private Calendar calendar;

    private DateHelper(Context context){
        locale = Language.getLocale(context);
        calendar = Calendar.getInstance(locale);
        is24H = !locale.equals(Locale.ENGLISH);
        HOUR = is24H ? Calendar.HOUR_OF_DAY : Calendar.HOUR;
        Log.d(TAG, "Is 24 H = "+is24H);
    }

    public static DateHelper create(Context context){
        return new DateHelper(context);
    }

    public boolean isIs24Hour(){
        return is24H;
    }

    public Calendar getCalendar(){
        return calendar;
    }

// FILTER

    public Pair<Date, Date> getRangeLastWeek(Date lastInsert){
        return getRangeOne(Calendar.DAY_OF_MONTH, lastInsert);
    }

    public Pair<Date, Date> getRangeLastMonth(Date lastInsert){
        return getRangeOne(Calendar.MONTH, lastInsert);
    }

    public Pair<Date, Date> getRangeLastYear( Date lastInsert) {
        return getRangeOne(Calendar.YEAR, lastInsert);
    }

    public static boolean isIntoRange(Date d1, Pair<Date, Date> range){
        return d1.compareTo(range.first)>=0 && d1.compareTo(range.second)<=0;
    }

    private Pair<Date, Date> getRangeOne(int filter, Date lastInsert) {
        final Calendar calendar = (Calendar)getCalendar().clone();
        calendar.setTime(lastInsert);
        calendar.add(filter, filter==Calendar.DAY_OF_MONTH ? -7 : -1);
        final Date ago = calendar.getTime();
        return Pair.create(ago, lastInsert);
    }

// FORMATTER

    private String formatToString(int style, Date date) {
        return DateFormat.getDateInstance(style, locale).format(date);
    }

    private String formatDateTimeToString(int styleDate,int styleTime,Date dateWithTime) {
        return DateFormat.getDateTimeInstance(styleDate, styleTime, locale).format(dateWithTime);
    }


    public String formatShortToString(Date date){
        if(date==null) return "";
        return new SimpleDateFormat(is24H? "dd/MM/yyyy" : "MM/dd/yyyy", locale).format(date);
    }

    public String formatFullToString(Date date){
        if(date==null) return "";
        return formatToString(DateFormat.FULL, date);
    }

    public String formatShortDateTimeToString(Date dateWithTime) {
        if(dateWithTime==null) return "";
        //return formatDateTimeToString(DateFormat.SHORT, DateFormat.SHORT, dateWithTime);
        return new SimpleDateFormat(is24H? "dd/MM/yyyy HH:mm" : "MM/dd/yyyy hh:mm aa", locale).format(dateWithTime);
    }

// ------------------ //

    public Date parseShortToDate(String stringDate) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd", locale).parse(stringDate);
    }

    public Date parseShortDateTimeToDate(String dateWithTime) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", locale).parse(dateWithTime);
    }


// GETTER
    public String getMonth(int month){
        return (new DateFormatSymbols(locale).getMonths()[month-1]).toUpperCase();
    }

    public int getHour() {
        return HOUR;
    }
}
