package com.run_walk_tracking_gps.gui.components.dialog;

import android.content.Context;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;

public class DistanceDialog extends MeasureDialog {

    private static final int MAX_VALUES_INT_DISTANCE = 1000;
    private static final int MAX_VALUES_DEC_DISTANCE = 9;

    private DistanceDialog(Context context, OnSelectedListener onSelectedListener) {
        super(context, Measure.Type.DISTANCE, onSelectedListener);
    }

    public static DistanceDialog create(Context context,OnSelectedListener onSelectedListener){
        return new DistanceDialog(context, onSelectedListener);
    }

    @Override
    protected void setMinAndMax(NumberPicker integer, NumberPicker decimal) {
        integer.setMaxValue(MAX_VALUES_INT_DISTANCE);
        decimal.setMaxValue(MAX_VALUES_DEC_DISTANCE);
    }

    @Override
    protected void setTitleDialog(TextView titleDialog) {
        titleDialog.setText(R.string.distance);
    }

}
