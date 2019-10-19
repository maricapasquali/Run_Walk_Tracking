package com.run_walk_tracking_gps.gui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.enumeration.Measure;
import com.run_walk_tracking_gps.gui.enumeration.MeasureUnit;

public class EnergyDialog extends MeasureDialog {

    private static final int MAX_VALUES_INT_CALORIES = 5000;
    private static final int MAX_VALUES_DEC_CALORIES = 9;

    private OnSelectCaloriesListener onSelectCaloriesListener;

    private EnergyDialog(Context context, OnSelectCaloriesListener onSelectCaloriesListener) {
        super(context);
        this.onSelectCaloriesListener = onSelectCaloriesListener;
    }

    public static EnergyDialog create(Context context, OnSelectCaloriesListener onSelectCaloriesListener){
        return new EnergyDialog(context,onSelectCaloriesListener);
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

    @Override
    protected void setUnit(TextView unit) {
        unit.setText(MeasureUnit.KILO_CALORIES.getStrId());
    }

    @Override
    protected void setPositiveListener(DialogInterface dialog, int which) {
        onSelectCaloriesListener.setPositiveListener(isAvailable()? getValue(Measure.ENERGY) : NO_AVAILABLE);
    }

    public interface OnSelectCaloriesListener{
        void setPositiveListener(String calories);
    }
}
