package com.run_walk_tracking_gps.gui.dialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.run_walk_tracking_gps.utilities.DateUtilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;


public class DateTimePickerDialog {

    private static final String TAG = DateTimePickerDialog.class.getName();

    private static boolean is24HourView = DateUtilities.isIs24Hour();
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

    private DateTimePickerDialog(Context context, OnSelectDateTime onSelectDateTime) {
        calendar = DateUtilities.getCalendar();

        datePickerDialog = new DatePickerDialog(context, (datePicker, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            createTimePicker(context, onSelectDateTime).show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }


    private DateTimePickerDialog(Context context, String dateString, OnSelectDateTime onSelectDateTime, boolean alsoTime) {

        calendar = DateUtilities.getCalendar();
        try {
            calendar.setTime(DateUtilities.parseShortToDate(dateString));
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            //Toast.makeText(context, "Data non valida", Toast.LENGTH_SHORT).show();
        }

        datePickerDialog = new DatePickerDialog(context, (datePicker, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            if(alsoTime)
                createTimePicker(context, onSelectDateTime).show();
            else
               onSelectDateTime.setTextView(DateUtilities.parseToString(DateFormat.SHORT, calendar.getTime()), calendar);


        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private TimePickerDialog createTimePicker(Context context, OnSelectDateTime onSelectDateTime){
        return new TimePickerDialog(context, (timePicker, hourOfDay, minute) -> {
            calendar.set(is24HourView ? Calendar.HOUR_OF_DAY : Calendar.HOUR, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            onSelectDateTime.setTextView(DateUtilities.parseShortToString(calendar.getTime()), calendar);

        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24HourView);
    }


    public static DateTimePickerDialog create(Context context, OnSelectDateTime listener) {
        return new DateTimePickerDialog(context, listener);
    }


    public static DateTimePickerDialog create(Context context, String dateString, OnSelectDateTime listener, boolean alsoTime) {
        return new DateTimePickerDialog(context, dateString, listener, alsoTime);
    }

    public void show(){
        datePickerDialog.show();
    }


    public interface OnSelectDateTime {
        void setTextView(String date, Calendar calendar);
    }

}


