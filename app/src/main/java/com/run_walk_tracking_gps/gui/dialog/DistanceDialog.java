package com.run_walk_tracking_gps.gui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.enumeration.Measure;
import com.run_walk_tracking_gps.gui.enumeration.MeasureUnit;

public class DistanceDialog extends MeasureDialog {

    private static final int MAX_VALUES_INT_DISTANCE = 1000;
    private static final int MAX_VALUES_DEC_DISTANCE = 9;

    private OnSelectDistanceListener onSelectDistanceListener;

    private DistanceDialog(Context context, OnSelectDistanceListener onSelectDistanceListener) {
        super(context);
        this.onSelectDistanceListener = onSelectDistanceListener;
    }

    public static DistanceDialog create(Context context,OnSelectDistanceListener onSelectDistanceListener){
        return new DistanceDialog(context, onSelectDistanceListener);
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

    @Override
    protected void setUnit(TextView unit) {
        unit.setText(MeasureUnit.KILOMETER.getStrId());
    }

    @Override
    protected void setPositiveListener(DialogInterface dialog, int which) {
        onSelectDistanceListener.setPositiveListener(isAvailable()? getValue(Measure.DISTANCE) : NO_AVAILABLE,
                isAvailable()?  getValueDouble() : 0.0);
    }

    private Double getValueDouble(){
        return Double.valueOf(getValue(Measure.DISTANCE).split(" ")[0]);
    }

    public interface OnSelectDistanceListener{
        void setPositiveListener(String distanceStr, Double distance);
    }

}
