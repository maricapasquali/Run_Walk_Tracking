package com.run_walk_tracking_gps.gui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.enumeration.Measure;
import com.run_walk_tracking_gps.gui.enumeration.MeasureUnit;

import org.json.JSONException;

public class HeightDialog extends MeasureDialog {

    private static final int MAX_VALUES_INT_HEIGHT = 10;
    private static final int MAX_VALUES_DEC_HEIGHT = 99;

    private OnSelectHeightListener onSelectHeightListener;

    private HeightDialog(Context context, OnSelectHeightListener onSelectHeightListener) {
        super(context, Measure.HEIGHT);
        this.onSelectHeightListener = onSelectHeightListener;
    }

    private HeightDialog(Context context, String valueStr, OnSelectHeightListener onSelectHeightListener) {
        super(context, Measure.HEIGHT);
        this.onSelectHeightListener = onSelectHeightListener;
        super.setCurrentValue(Measure.HEIGHT, valueStr);
    }

    public static HeightDialog create(Context context, OnSelectHeightListener onSelectHeightListener){
        return new HeightDialog(context, onSelectHeightListener);
    }

    public static HeightDialog create(Context context,String valueStr, OnSelectHeightListener onSelectHeightListener){
        return new HeightDialog(context, valueStr, onSelectHeightListener);
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

    protected void setPositiveListener(DialogInterface dialog, int which){
        onSelectHeightListener.setPositiveListener(getValue(Measure.HEIGHT), getValueDouble());
    }

    public interface OnSelectHeightListener{
        void setPositiveListener(String heightString, Double height);
    }

    private Double getValueDouble(){
        return Double.valueOf( getValue(Measure.HEIGHT).split(" ")[0]);
    }
}
