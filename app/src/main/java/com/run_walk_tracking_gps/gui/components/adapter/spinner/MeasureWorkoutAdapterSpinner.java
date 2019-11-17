package com.run_walk_tracking_gps.gui.components.adapter.spinner;

import android.content.Context;
import android.widget.TextView;


import com.run_walk_tracking_gps.model.Measure;

public class MeasureWorkoutAdapterSpinner extends CustomSpinnerAdapter<Measure.Type> {

    public MeasureWorkoutAdapterSpinner(Context context, Measure.Type[] objects, boolean isDisabledFirst) {
        super(context, objects, isDisabledFirst);
    }

    public MeasureWorkoutAdapterSpinner(Context context, Measure.Type[] objects, boolean isDisabledFirst, boolean isCenter) {
        super(context, objects, isDisabledFirst);
        setTextViewInCenter(isCenter);
    }

    @Override
    protected void setItemSpinner(TextView textView, Measure.Type item) {
        textView.setText(item.getStrId());
    }
}
