package com.run_walk_tracking_gps.gui.components.adapter.spinner;

import android.content.Context;

import android.widget.TextView;

import com.run_walk_tracking_gps.utilities.EnumUtilities;
import com.run_walk_tracking_gps.model.enumerations.Gender;

public class GenderAdapterSpinner extends CustomSpinnerAdapter<Integer> {

    public GenderAdapterSpinner(Context context, boolean isDisabledFirst) {
        super(context, isDisabledFirst ?
                       EnumUtilities.valuesWithDescription(Gender.class):
                       EnumUtilities.valuesStrId(Gender.class), isDisabledFirst);
    }


    @Override
    protected void setItemSpinner(TextView textView, Integer item) {
        textView.setText(item);

        if(EnumUtilities.isNotDescription(Gender.class, item))
            textView.setCompoundDrawablesWithIntrinsicBounds(
                    getContext().getDrawable(EnumUtilities.iconOfStrId(Gender.class, item)),
                    null, null, null);
    }
}
