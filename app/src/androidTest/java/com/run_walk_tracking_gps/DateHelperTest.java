package com.run_walk_tracking_gps;

import android.content.Context;
import android.util.Log;

import com.run_walk_tracking_gps.utilities.DateHelper;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import androidx.test.InstrumentationRegistry;

// TODO: 2/17/2020 RIMUOVERE
public class DateHelperTest {

    private String TAG =  DateHelperTest.class.getName();
    private Context context = InstrumentationRegistry.getTargetContext();

   @Test
    public void testing(){
        Calendar calendar = DateHelper.create(context).getCalendar();
        Date date = calendar.getTime();
        long mill = 1576007079550L;
        Log.e(TAG, new Date(mill).toString());
        Log.e(TAG, "Date unix : "+ DateHelper.create(context).getCurrentDate());



        /*final String dateShortToDate = DateHelper.create(context).formatShortToString(date);
        final String dateShortDateTimeToDate = DateHelper.create(context).formatShortDateTimeToString(date);
        final String dateFullToString = DateHelper.create(context).formatFullToString(date);

        // OK
        Log.e(TAG, dateShortToDate);
        Log.e(TAG, dateShortDateTimeToDate);
        Log.e(TAG, dateFullToString);

        try {
            Log.e(TAG, DateHelper.create(context).parseShortToDate(dateShortToDate).toString());
            Log.e(TAG, DateHelper.create(context).parseShortDateTimeToDate(dateShortDateTimeToDate).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

    }
}
