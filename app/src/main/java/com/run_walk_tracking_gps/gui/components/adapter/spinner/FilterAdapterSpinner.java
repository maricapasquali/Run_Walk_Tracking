package com.run_walk_tracking_gps.gui.components.adapter.spinner;

import android.content.Context;
import android.widget.TextView;

import com.run_walk_tracking_gps.model.enumerations.FilterTime;

public class FilterAdapterSpinner extends CustomSpinnerAdapter<FilterTime> {

    public FilterAdapterSpinner(Context context, boolean isWorkouts) {
        super(context, isWorkouts ? FilterTime.valuesWorkouts() : FilterTime.values(), false);
        setTextViewInCenter(true);
    }

    @Override
    protected void setItemSpinner(TextView textView, FilterTime item) {
        textView.setText(item.getStrId());
    }
}
