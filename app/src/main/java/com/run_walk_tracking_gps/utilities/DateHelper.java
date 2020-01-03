package com.run_walk_tracking_gps.utilities;

import android.content.Context;
import android.support.v4.util.Pair;

import com.run_walk_tracking_gps.model.enumerations.Language;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import java.util.TimeZone;


public final class DateHelper {
    private final static String TAG = DateHelper.class.getName();

    private boolean is24H;
    private static Locale locale;
    private Calendar calendar;

    private DateHelper(Context context){
        locale = Language.getLocale(context);

        calendar = Calendar.getInstance(locale);
        is24H = !locale.equals(Locale.ENGLISH);

        /*Log.d(TAG, "Is 24 H = "+is24H);
        Log.d(TAG, "Time Zone = "+ TimeZone.getDefault().getID());*/
    }

    // TODO: 12/30/2019 UTILIZZARE SYNGLETON (CONTEXT DELL'APPLICAZIONE)
    public static DateHelper create(Context context){
        return new DateHelper(context);
    }

    public boolean isIs24Hour(){
        return is24H;
    }

    public Calendar getCalendar(){
        return calendar;
    }

    public long getCurrentDate(){
        return calendar.getTime().getTime()/1000;
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

    public String formatForDB(Date date){
        if(date==null) return "";
        return new SimpleDateFormat("yyyy-MM-dd", locale).format(date);
    }

    public long getUnixTime(Date date){
        if(date==null) return 0L;
        return date.getTime()/1000L;
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
        final Calendar c = Calendar.getInstance(TimeZone.getDefault(), locale);
        c.setTime(dateWithTime);
        return new SimpleDateFormat(is24H? "dd/MM/yyyy HH:mm" : "MM/dd/yyyy hh:mm a", locale).format(c.getTime());
    }

// ------------------ //

    public Date parseShortToDate(String stringDate) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd", locale).parse(stringDate);
    }

    public Date parseShortDateTimeToDate(long dateWithTime)  {
        final Calendar c = Calendar.getInstance(TimeZone.getDefault(), locale);
        c.setTime(new Date(dateWithTime*1000L));
        return c.getTime();
    }


// GETTER
    public String getMonth(int month){
        return (new DateFormatSymbols(locale).getMonths()[month-1]).toUpperCase();
    }
}
