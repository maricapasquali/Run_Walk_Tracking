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

import static java.lang.String.format;

public class WeightDialog extends MeasureDialog {

    private static final int MIN_VALUES_INT_WEIGHT = 30;
    private static final int MAX_VALUES_INT_WEIGHT = 300;
    private static final int MAX_VALUES_DEC_WEIGHT = 9;

    private OnSelectWeightListener onSelectWeightListener;


    private WeightDialog(Context context, OnSelectWeightListener onSelectWeightListener) {
        super(context, Measure.WEIGHT);
        this.onSelectWeightListener = onSelectWeightListener;
    }

    public static WeightDialog create(Context context, OnSelectWeightListener onSelectWeightListener){
        return new WeightDialog(context,onSelectWeightListener );
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


    protected void setPositiveListener(DialogInterface dialog, int which){
        onSelectWeightListener.setPositiveListener(getValue(Measure.WEIGHT), getValueDouble());
    }

    public interface OnSelectWeightListener{
        void setPositiveListener(String weightString, Double weight);
    }

    private Double getValueDouble(){
        return Double.valueOf( getValue(Measure.WEIGHT).split(" ")[0]);
    }

}
