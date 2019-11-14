package com.run_walk_tracking_gps.gui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;

import static java.lang.String.format;

public abstract class MeasureDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener {

    private static final int MIN_INTEGER_DEFAULT = 0;
    private static final int MIN_DECIMAL_DEFAULT = 0;


    private View theView;
    private TextView titleDialog;
    private NumberPicker integer;
    private NumberPicker decimal;
    private TextView unit ;

    private OnSelectedListener onSelectedListener;
    private Measure measure;

    protected MeasureDialog(Context context, Measure.Type measure, OnSelectedListener onSelectedListener) {
        super(context);

        this.onSelectedListener = onSelectedListener;
        theView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_measure, null);

        titleDialog = theView.findViewById(R.id.dialog_title);
        integer = theView.findViewById(R.id.integer);
        decimal = theView.findViewById(R.id.decimal);
        unit = theView.findViewById(R.id.measure_unit);

        setView(theView);
        setTitleDialog(titleDialog);

        decimal.setFormatter(i -> format(Measure.Format.FORMAT_NUMBER_DOUBLE, i));

        setUnit(measure);

        integer.setMinValue(MIN_INTEGER_DEFAULT);
        decimal.setMinValue(MIN_DECIMAL_DEFAULT);

        setMinAndMax(integer, decimal);

        setPositiveButton(R.string.ok, this);

        setNegativeButton(R.string.cancel, (dialog, which) -> {});
    }

    private void setUnit(Measure.Type measure){
        this.measure = Measure.create(getContext(), measure);

        if(measure.equals(Measure.Type.DURATION)){
            final TextView separation = theView.findViewById(R.id.separation);
            separation.setText(Measure.Unit.HOURS.getStrId());
            unit.setText(Measure.Unit.MINUTES.getStrId());
        }else {
            unit.setText(this.measure.getUnit().getStrId());
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        onSelectedListener.setPositiveListener(getMeasureValue());
    }

    public interface OnSelectedListener{
        void setPositiveListener(Measure measure);
    }

    private Measure getMeasureValue(){
        return isAvailable()? this.measure.getMeasure(integer.getValue(), decimal.getValue()): null;
    }

    private boolean isAvailable(){
        return !(integer.getValue()==0 && decimal.getValue()==0);
    }
/*
    protected void setCurrentValue(Measure.Type measure, String valueStr){
        int iValue=0, dValue=0;
        switch (measure){
            case HEIGHT:
                int[] values = valueHeightSplit(valueStr);
                iValue = values[0]; dValue = values[1];

                break;
        }
        integer.setValue(iValue);
        decimal.setValue(dValue);
    }

    private int[] valueHeightSplit(String heightStr){
        final String REGEX_INT_DEC = "\\.";
        final String REGEX_DEC_UNIT = getContext().getResources().getString(R.string.space);

        String[] hSplit = heightStr.split(REGEX_INT_DEC);
        final String integer = hSplit[0];
        final String decimal = hSplit[1].split(REGEX_DEC_UNIT)[0];
        return new int[]{
                Integer.valueOf(integer), Integer.valueOf(decimal)
        };
    }
*/

    protected abstract void setMinAndMax(NumberPicker integer, NumberPicker decimal);

    protected abstract void setTitleDialog(TextView titleDialog);
}
