package com.run_walk_tracking_gps.gui.adapter.spinner;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import android.widget.TextView;


import com.run_walk_tracking_gps.utilities.EnumUtilities;
import com.run_walk_tracking_gps.model.enumerations.Sport;

public class SportAdapterSpinner extends CustomSpinnerAdapter<Integer> {

    public SportAdapterSpinner(Context context, boolean isDisabledFirst) {
        super(context, isDisabledFirst?
                EnumUtilities.valuesWithDescription(Sport.class)
                : EnumUtilities.valuesStrId(Sport.class), isDisabledFirst);
    }


    public SportAdapterSpinner(Context context, boolean isDisabledFirst, boolean isCenter) {
        super(context, isDisabledFirst? EnumUtilities.valuesWithDescription(Sport.class)
                : EnumUtilities.valuesStrId(Sport.class), isDisabledFirst);
        setTextViewInCenter(isCenter);
    }

    @Override
    protected void setItemSpinner(TextView textView, Integer item) {
        textView.setText(item);

        if (!isTextViewCenter()) {
            if (EnumUtilities.isNotDescription(Sport.class, item))
                textView.setCompoundDrawablesWithIntrinsicBounds(
                        getContext().getDrawable(EnumUtilities.iconOfStrId(Sport.class, item)),
                        null, null, null);
        }
    }
}
