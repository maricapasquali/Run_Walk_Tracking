package com.run_walk_tracking_gps.gui.adapter.spinner;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.TextView;

import com.run_walk_tracking_gps.gui.enumeration.FilterTime;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.utilities.EnumUtilities;

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
