package com.run_walk_tracking_gps.utilities;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.DateFormat;
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
