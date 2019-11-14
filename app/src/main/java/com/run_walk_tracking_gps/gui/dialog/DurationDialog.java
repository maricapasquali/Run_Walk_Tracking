package com.run_walk_tracking_gps.gui.dialog;

import android.content.Context;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;


public class DurationDialog extends MeasureDialog {

    private static final int MAX_VALUES_HOURS = 23;
    private static final int MAX_VALUES_MIN = 59;

    private DurationDialog(Context context, OnSelectedListener onSelectedListener) {
        super(context, Measure.Type.DURATION, onSelectedListener);
    }

    public static DurationDialog create(Context context, OnSelectedListener onSelectedListener)    {
        return new  DurationDialog(context, onSelectedListener);
    }

    @Override
    protected void setMinAndMax(NumberPicker integer, NumberPicker decimal) {
        integer.setMaxValue(MAX_VALUES_HOURS);
        decimal.setMaxValue(MAX_VALUES_MIN);

        integer.setFormatter(i -> String.format(Measure.Format.FORMAT_NUMBER_DOUBLE, i));
        decimal.setFormatter(i -> String.format(Measure.Format.FORMAT_NUMBER_DOUBLE, i));
    }

    @Override
    protected void setTitleDialog(TextView titleDialog) {
        titleDialog.setText(R.string.time);
    }

}
