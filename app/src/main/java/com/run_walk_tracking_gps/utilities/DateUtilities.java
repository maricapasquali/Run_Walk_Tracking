package com.run_walk_tracking_gps.utilities;

import android.content.Context;
import android.support.v4.util.Pair;
import android.util.Log;

import com.run_walk_tracking_gps.model.enumerations.Language;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateUtilities {
    private final static String TAG = DateUtilities.class.getName();

    private int HOUR;
    private boolean is24H;
    private static Locale locale;
    private Calendar calendar;

    private DateUtilities(Context context){
        locale = Language.defaultForUser(context).getLocale(context);
        calendar = Calendar.getInstance(locale);
        is24H = !locale.equals(Locale.ENGLISH);
        HOUR = is24H ? Calendar.HOUR_OF_DAY : Calendar.HOUR;
    }

    public static DateUtilities create(Context context){
        return new DateUtilities(context);
    }

    public boolean isIs24Hour(){
        return is24H;
    }

    public Calendar getCalendar(){
        return calendar;
    }

    public Date parseShortToDate(String stringDate) throws ParseException {
        return parseToDate(DateFormat.SHORT, stringDate);
    }

    public Pair<Date, Date> getRangeLastWeek(Date lastInsert){
        return getRangeOne(Calendar.DAY_OF_MONTH, lastInsert);
    }

    public Pair<Date, Date> getRangeLastMonth( Date lastInsert){
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

    /**
     *
     * @param dateWithTime to format
     * @return date string short format with time
     */
    public String parseShortToString(Date dateWithTime){
        if(dateWithTime==null) return "";
        final Calendar c = (Calendar)getCalendar().clone();
        c.setTime(dateWithTime);

        //final String pattern =(is24H? "dd/MM/yy HH:mm":"MM/dd/yy hh:mm aa"); return new SimpleDateFormat(pattern).format(dateWithTime);

        final String pattern =(is24H? "HH:mm":"hh:mm aa");
        return parseToString(DateFormat.SHORT, dateWithTime) + " " + new SimpleDateFormat(pattern).format(dateWithTime);
    }

    public String parseShortToStringNoTime(Date date){
        return parseToString(DateFormat.SHORT, date);
    }

    public Date parseStringWithTimeToDateString(String dateWithTime){
        final Calendar c = (Calendar)getCalendar().clone();
        try {
            final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
            c.setTime(df.parse(dateWithTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    public String getMonth(int month){
        return (new DateFormatSymbols(locale).getMonths()[month-1]).toUpperCase();
    }

    public String parseFullToString(Date date){
        if(date==null) return "";
        return DateFormat.getDateInstance(DateFormat.FULL, locale).format(date);
    }

    public Date parseToDate(int style, String stringDate) throws ParseException {
        final DateFormat df = DateFormat.getDateInstance(style, locale);
        return df.parse(stringDate);
    }

    public String parseToString(int style, Date date) {
        return DateFormat.getDateInstance(style, locale).format(date);
    }

    public int getHour() {
        return HOUR;
    }
}
