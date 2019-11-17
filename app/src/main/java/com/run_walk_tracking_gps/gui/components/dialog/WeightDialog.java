package com.run_walk_tracking_gps.gui.components.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;

import static java.lang.String.format;

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

    @SuppressLint("DefaultLocale")
    @Override
    protected void setMinAndMax(NumberPicker integer, NumberPicker decimal) {
        integer.setMinValue(MIN_VALUES_INT_WEIGHT);
        integer.setMaxValue(MAX_VALUES_INT_WEIGHT);
        decimal.setMaxValue(MAX_VALUES_DEC_WEIGHT);

        decimal.setFormatter(i -> format(Measure.Format.FORMAT_NUMBER_SINGLE, i));
    }

    @Override
    protected void setTitleDialog(TextView titleDialog) {
        titleDialog.setText(R.string.weight);
    }

}
