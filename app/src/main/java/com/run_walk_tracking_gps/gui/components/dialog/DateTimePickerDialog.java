package com.run_walk_tracking_gps.gui.components.dialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;

import com.run_walk_tracking_gps.utilities.DateHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;


public class DateTimePickerDialog {

    private static final String TAG = DateTimePickerDialog.class.getName();

    private DateHelper dateHelper;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

    private DateTimePickerDialog(Context context){
        dateHelper = DateHelper.create(context);
        calendar = dateHelper.getCalendar();
    }

    private DateTimePickerDialog(Context context, OnSelectDateTime onSelectDateTime) {
        this(context);

        datePickerDialog = new DatePickerDialog(context, (datePicker, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            createTimePicker(context, onSelectDateTime).show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }


    private DateTimePickerDialog(Context context, String dateString, OnSelectDateTime onSelectDateTime, boolean alsoTime) {
        this(context);
        try {
            calendar.setTime(dateHelper.parseShortToDate(dateString));
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            //Toast.makeText(context, "Data non valida", Toast.LENGTH_SHORT).show();
        }

        datePickerDialog = new DatePickerDialog(context, (datePicker, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            if(alsoTime)
                createTimePicker(context, onSelectDateTime).show();
            else
               onSelectDateTime.setTextView(dateHelper.parseToString(DateFormat.SHORT, calendar.getTime()), calendar);


        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private TimePickerDialog createTimePicker(Context context, OnSelectDateTime onSelectDateTime){
        return new TimePickerDialog(context, (timePicker, hourOfDay, minute) -> {
            calendar.set(dateHelper.getHour(), hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            onSelectDateTime.setTextView(dateHelper.parseShortToString(calendar.getTime()), calendar);

        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), dateHelper.isIs24Hour());
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


