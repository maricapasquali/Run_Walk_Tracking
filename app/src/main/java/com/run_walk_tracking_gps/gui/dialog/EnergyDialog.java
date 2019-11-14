package com.run_walk_tracking_gps.gui.dialog;

import android.content.Context;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;


public class EnergyDialog extends MeasureDialog {

    private static final int MAX_VALUES_INT_CALORIES = 5000;
    private static final int MAX_VALUES_DEC_CALORIES = 9;

    private EnergyDialog(Context context, OnSelectedListener onSelectedListener) {
        super(context, Measure.Type.ENERGY, onSelectedListener);
    }

    public static EnergyDialog create(Context context, OnSelectedListener onSelectedListener){
        return new EnergyDialog(context,onSelectedListener);
    }

    @Override
    protected void setMinAndMax(NumberPicker integer, NumberPicker decimal) {
        integer.setMaxValue(MAX_VALUES_INT_CALORIES);
        decimal.setMaxValue(MAX_VALUES_DEC_CALORIES);
    }

    @Override
    protected void setTitleDialog(TextView titleDialog) {
        titleDialog.setText(R.string.calories);
    }
}
