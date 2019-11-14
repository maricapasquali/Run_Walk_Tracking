package com.run_walk_tracking_gps.gui.dialog;

import android.content.Context;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;

public class WeightDialog extends MeasureDialog {

    private static final int MIN_VALUES_INT_WEIGHT = 30;
    private static final int MAX_VALUES_INT_WEIGHT = 300;
    private static final int MAX_VALUES_DEC_WEIGHT = 9;

    private WeightDialog(Context context, OnSelectedListener onSelectedListener) {
        super(context, Measure.Type.WEIGHT, onSelectedListener);
    }

    public static WeightDialog create(Context context, OnSelectedListener onSelectedListener){
        return new WeightDialog(context,onSelectedListener );
    }

    @Override
    protected void setMinAndMax(NumberPicker integer, NumberPicker decimal) {
        integer.setMinValue(MIN_VALUES_INT_WEIGHT);
        integer.setMaxValue(MAX_VALUES_INT_WEIGHT);
        decimal.setMaxValue(MAX_VALUES_DEC_WEIGHT);
    }

    @Override
    protected void setTitleDialog(TextView titleDialog) {
        titleDialog.setText(R.string.weight);
    }

}
