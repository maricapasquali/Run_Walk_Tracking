package com.run_walk_tracking_gps.gui.components.dialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import com.run_walk_tracking_gps.utilities.DateHelper;

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


    private DateTimePickerDialog(Context context, OnSelectDateTime onSelectDateTime, boolean alsoTime) {
        this(context);

        datePickerDialog = new DatePickerDialog(context, (datePicker, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            if(alsoTime)
                createTimePicker(context, onSelectDateTime).show();
            else onSelectDateTime.setTextView(dateHelper.formatShortToString(calendar.getTime()), calendar);


        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private TimePickerDialog createTimePicker(Context context, OnSelectDateTime onSelectDateTime){
        return new TimePickerDialog(context, (timePicker, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            onSelectDateTime.setTextView(dateHelper.formatShortDateTimeToString(calendar.getTime()), calendar);

        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), dateHelper.isIs24Hour());
    }


    public static DateTimePickerDialog createDateTimePicker(Context context, OnSelectDateTime listener) {
        return new DateTimePickerDialog(context, listener, true);
    }


    public static DateTimePickerDialog createDatePicker(Context context, OnSelectDateTime listener) {
        return new DateTimePickerDialog(context, listener, false);
    }

    public void show(){
        datePickerDialog.show();
    }


    public interface OnSelectDateTime {
        void setTextView(String date, Calendar calendar);
    }

}


