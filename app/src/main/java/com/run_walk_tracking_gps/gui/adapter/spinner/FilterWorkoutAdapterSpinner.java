package com.run_walk_tracking_gps.gui.adapter.spinner;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.TextView;

import com.run_walk_tracking_gps.gui.enumeration.FilterTime;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.utilities.EnumUtilities;

public class FilterWorkoutAdapterSpinner extends CustomSpinnerAdapter<FilterTime> {

    public FilterWorkoutAdapterSpinner(Context context, boolean isDisabledFirst) {
        super(context, FilterTime.values(), isDisabledFirst);
    }

    public FilterWorkoutAdapterSpinner(Context context, boolean isDisabledFirst, boolean isCenter) {
        super(context, FilterTime.values(), isDisabledFirst);
        setTextViewInCenter(isCenter);
    }

    @Override
    protected void setItemSpinner(TextView textView, FilterTime item) {
        textView.setText(item.getStrId());
    }
}
