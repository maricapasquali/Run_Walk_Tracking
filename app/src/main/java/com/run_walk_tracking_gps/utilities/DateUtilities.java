package com.run_walk_tracking_gps.utilities;

import android.support.v4.util.Pair;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtilities {

    private static String timeZone = "Europe/Rome";
    private static boolean is24Hour = true;
    private static Locale locale = Locale.ITALY;

    private static String FORMAT_DATE = "%02d/%02d/%d";
    private static String FORMAT_TIME = "%02d:%02d";

    private static Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone), locale);

    public static Calendar getCalendar() {
        return calendar;
    }

    public static void setCalendar(Calendar calendar) {
        DateUtilities.calendar = calendar;
    }

    public static TimeZone getTimeZone() {
        return TimeZone.getTimeZone(timeZone);
    }

    public static void setTimeZone(String timeZone) {
        DateUtilities.timeZone = timeZone;
    }

    public static boolean isIs24Hour() {
        return is24Hour;
    }

    public static void setIs24Hour(boolean is24Hour) {
        DateUtilities.is24Hour = is24Hour;
    }

    public static Locale getLocale() {
        return locale;
    }

    public static void setLocale(Locale locale) {
        DateUtilities.locale = locale;
    }

    public static Date parseShortToDate(String stringDate) throws ParseException {
        return parseToDate(DateFormat.SHORT, stringDate);
    }

    public static Pair<Date, Date> getRangeLastWeek( Date lastInsert){
        return getRangeOne(Calendar.DAY_OF_MONTH, lastInsert);
    }

    public static Pair<Date, Date> getRangeLastMonth( Date lastInsert){
        return getRangeOne(Calendar.MONTH, lastInsert);
    }

    public static Pair<Date, Date> getRangeLastYear( Date lastInsert) {
        return getRangeOne(Calendar.YEAR, lastInsert);
    }

    public static boolean isIntoRange(Date d1, Pair<Date, Date> range){
        return d1.compareTo(range.first)>=0 && d1.compareTo(range.second)<=0;
    }

    private static Pair<Date, Date> getRangeOne(int filter, Date lastInsert) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone), locale);
        //final Date today = calendar.getTime();
        calendar.setTime(lastInsert);
        calendar.add(filter, filter==Calendar.DAY_OF_MONTH ? -7 : -1);
        final Date ago = calendar.getTime();
        return Pair.create(ago, lastInsert);
    }

    // TODO: 10/15/2019 DA RIGUARDARE PER TIME FORMAT

    /**
     *
     * @param dateWithTime to format
     * @return date string short format with time
     */
    public static String parseShortToString(Date dateWithTime){
        if(dateWithTime==null) return "";
        final Calendar c = Calendar.getInstance();
        c.setTime(dateWithTime);

        return parseToString(DateFormat.SHORT, dateWithTime)+ " "+
                String.format(FORMAT_TIME, c.get(is24Hour ? Calendar.HOUR_OF_DAY :Calendar.HOUR), c.get(Calendar.MINUTE));
    }

    // TODO: 10/15/2019 MIGLIORARE
    public static Date parseStringWithTimeToDateString(String dateWithTime){
        final Calendar c = Calendar.getInstance();

        try {
            String[] split = dateWithTime.split(" ");
            Date date = parseShortToDate(split[0]);
            c.setTime(date);
            String[] split_time = split[1].split(":");
            c.set(is24Hour ? Calendar.HOUR_OF_DAY :Calendar.HOUR, Integer.valueOf(split_time[0]));
            c.set(Calendar.MINUTE, Integer.valueOf(split_time[1]));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return c.getTime();
    }

    public static String getMonth(int month){
        return (new DateFormatSymbols().getMonths()[month-1]).toUpperCase();
    }

    public static  String parseFullToString(Date date){
        if(date==null) return "";
        return DateFormat.getDateInstance(DateFormat.FULL, locale).format(date);
    }

    public static Date parseToDate(int style, String stringDate) throws ParseException {
        final DateFormat df = DateFormat.getDateInstance(style, locale);
        return df.parse(stringDate);
    }

    public static String parseToString(int style, Date date) {
        return DateFormat.getDateInstance(style, locale).format(date);
    }


}
