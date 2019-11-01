package com.run_walk_tracking_gps.gui.dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.enumeration.Measure;
import com.run_walk_tracking_gps.gui.enumeration.MeasureUnit;
import com.run_walk_tracking_gps.utilities.DurationUtilities;


public class DurationDialog extends MeasureDialog {

    private static final int MAX_VALUES_HOURS = 23;
    private static final int MAX_VALUES_MIN = 59;

    private OnSelectDurationListener onSelectDurationListener;

    private DurationDialog(Context context, OnSelectDurationListener onSelectDurationListener) {
        super(context);
        this.onSelectDurationListener = onSelectDurationListener;
    }

    public static DurationDialog create(Context context, OnSelectDurationListener onSelectDurationListener)    {
        return new  DurationDialog(context, onSelectDurationListener);
    }

    @Override
    protected void setMinAndMax(NumberPicker integer, NumberPicker decimal) {
        integer.setMaxValue(MAX_VALUES_HOURS);
        decimal.setMaxValue(MAX_VALUES_MIN);

        integer.setFormatter(i -> String.format(FORMAT_NUMBER_DOUBLE, i));
        decimal.setFormatter(i -> String.format(FORMAT_NUMBER_DOUBLE, i));
    }

    @Override
    protected void setTitleDialog(TextView titleDialog) {
        titleDialog.setText(R.string.time);
    }

    @Override
    protected void setUnit(TextView unit) {
        final View view = getTheView();
        final TextView separation = view.findViewById(R.id.separation);
        separation.setText(MeasureUnit.HOURS.getStrId());

        unit.setText(MeasureUnit.MINUTES.getStrId());
    }

    @Override
    protected void setPositiveListener(DialogInterface dialog, int which) {
        onSelectDurationListener.setPositiveListener(getValue(Measure.DURATION), getValueInt());
    }

    private Integer getValueInt(){
        return DurationUtilities.stringToSeconds(getValue(Measure.DURATION));
    }

    public interface OnSelectDurationListener{
        void setPositiveListener(String durationStr, Integer duration);
    }

}
