package com.run_walk_tracking_gps.gui.components.dialog;

import android.content.Context;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;

public class HeightDialog extends MeasureDialog {

    private static final int MAX_VALUES_INT_HEIGHT = 10;
    private static final int MAX_VALUES_DEC_HEIGHT = 99;

    private HeightDialog(Context context, OnSelectedListener onSelectedListener) {
        super(context, Measure.Type.HEIGHT, onSelectedListener);
    }

    private HeightDialog(Context context, String valueStr, OnSelectedListener onSelectedListener) {
        super(context, Measure.Type.HEIGHT, onSelectedListener);
       // super.setCurrentValue(Measure.Type.HEIGHT, valueStr);
    }

    public static HeightDialog create(Context context, OnSelectedListener onSelectedListener){
        return new HeightDialog(context, onSelectedListener);
    }

    public static HeightDialog create(Context context,String valueStr, OnSelectedListener onSelectedListener){
        return new HeightDialog(context, valueStr, onSelectedListener);
    }

    @Override
    protected void setMinAndMax(NumberPicker integer, NumberPicker decimal) {
        integer.setMaxValue(MAX_VALUES_INT_HEIGHT);
        decimal.setMaxValue(MAX_VALUES_DEC_HEIGHT);
    }

    @Override
    protected void setTitleDialog(TextView titleDialog) {
        titleDialog.setText(R.string.height);
    }

}
