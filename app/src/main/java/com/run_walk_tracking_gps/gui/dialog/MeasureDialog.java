package com.run_walk_tracking_gps.gui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.enumeration.Measure;
import com.run_walk_tracking_gps.gui.enumeration.MeasureUnit;

import org.json.JSONException;

import static java.lang.String.format;


public abstract class MeasureDialog extends AlertDialog.Builder {

    protected static final String FORMAT_NUMBER_DOUBLE ="%02d";
    protected static final String FORMAT_NUMBER_SINGLE ="%d";
    private final static String FORMAT_DURATION = "%02d:%02d:00";

    private static final int MIN_INTEGER_DEFAULT = 0;
    private static final int MIN_DECIMAL_DEFAULT = 0;

    protected String NO_AVAILABLE = getContext().getResources().getString(R.string.no_available_abbr);

    private View theView;
    private TextView titleDialog;
    private NumberPicker integer;
    private NumberPicker decimal;
    private TextView unit ;

    protected MeasureDialog(Context context, Measure measure) {
        super(context);

        theView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_measure, null);

        titleDialog = theView.findViewById(R.id.dialog_title);
        integer = theView.findViewById(R.id.integer);
        decimal = theView.findViewById(R.id.decimal);
        unit = theView.findViewById(R.id.measure_unit);

        setView(theView);

        setTitleDialog(titleDialog);

        decimal.setFormatter(i -> format(FORMAT_NUMBER_DOUBLE, i));

        setUnit(measure);
        if(measure==Measure.DURATION){
            final TextView separation = theView.findViewById(R.id.separation);
            separation.setText(MeasureUnit.HOURS.getStrId());
            unit.setText(MeasureUnit.MINUTES.getStrId());
        }

        integer.setMinValue(MIN_INTEGER_DEFAULT);
        decimal.setMinValue(MIN_DECIMAL_DEFAULT);

        setMinAndMax(integer, decimal);

        setPositiveButton(R.string.ok, this::setPositiveListener);

        setNegativeButton(R.string.cancel, (dialog, which) -> {});
    }

    protected String getValue(Measure measure){
        if(measure==Measure.DURATION) return format(FORMAT_DURATION, integer.getValue(), decimal.getValue());

        return format("%d"+getContext().getString(R.string.dot) +
                ( measure==Measure.HEIGHT ? FORMAT_NUMBER_DOUBLE : FORMAT_NUMBER_SINGLE)
                + " %s",integer.getValue(), decimal.getValue(), unit.getText());
    }

    private void setUnit(Measure measure) {
        try{
            switch (measure){

                case HEIGHT:

                    if(!Preferences.isJustUserLogged(getContext()) ||
                            Preferences.getUnitHeightDefault(getContext()).equals(getContext().getString(MeasureUnit.METER.getStrId())))
                        unit.setText(MeasureUnit.METER.getStrId());
                    else
                        unit.setText(MeasureUnit.FEET.getStrId());
                    break;
                case DISTANCE:
                    if(!Preferences.isJustUserLogged(getContext()) ||
                            Preferences.getUnitDistanceDefault(getContext()).equals(getContext().getString(MeasureUnit.KILOMETER.getStrId())))
                        unit.setText(MeasureUnit.KILOMETER.getStrId());
                    else
                        unit.setText(MeasureUnit.MILE.getStrId());
                    break;
                case WEIGHT:
                    if(!Preferences.isJustUserLogged(getContext()) ||
                            Preferences.getUnitWeightDefault(getContext()).equals(getContext().getString(MeasureUnit.KILOGRAM.getStrId())))
                        unit.setText(MeasureUnit.KILOGRAM.getStrId());
                    else
                        unit.setText(MeasureUnit.POUND.getStrId());
                    break;
                case ENERGY:
                    unit.setText(MeasureUnit.KILO_CALORIES.getStrId());
                    break;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    protected void setCurrentValue(Measure measure, String valueStr){
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

    public boolean isAvailable(){
       return !(integer.getValue()==0 && decimal.getValue()==0);
    }

    protected abstract void setMinAndMax(NumberPicker integer, NumberPicker decimal);

    protected abstract void setTitleDialog(TextView titleDialog);

    protected abstract void setPositiveListener(DialogInterface dialog, int which);
}
