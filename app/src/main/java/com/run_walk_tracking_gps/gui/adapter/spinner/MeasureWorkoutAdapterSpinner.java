package com.run_walk_tracking_gps.gui.adapter.spinner;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.TextView;

import com.run_walk_tracking_gps.gui.enumeration.FilterTime;
import com.run_walk_tracking_gps.gui.enumeration.Measure;

public class MeasureWorkoutAdapterSpinner extends CustomSpinnerAdapter<Measure> {

    public MeasureWorkoutAdapterSpinner(Context context, Measure[] objects, boolean isDisabledFirst) {
        super(context, objects, isDisabledFirst);
    }

    public MeasureWorkoutAdapterSpinner(Context context, Measure[] objects, boolean isDisabledFirst, boolean isCenter) {
        super(context, objects, isDisabledFirst);
        setTextViewInCenter(isCenter);
    }

    @Override
    protected void setItemSpinner(TextView textView, Measure item) {
        textView.setText(item.getStrId());
    }
}
