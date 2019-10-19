package com.run_walk_tracking_gps.gui.adapter.spinner;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import android.widget.TextView;

import com.run_walk_tracking_gps.utilities.EnumUtilities;
import com.run_walk_tracking_gps.model.enumerations.Gender;

public class GenderAdapterSpinner extends CustomSpinnerAdapter<Integer> {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GenderAdapterSpinner(Context context, boolean isDisabledFirst) {
        super(context, isDisabledFirst ?
                       EnumUtilities.valuesWithDescription(Gender.class):
                       EnumUtilities.valuesStrId(Gender.class), isDisabledFirst);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void setItemSpinner(TextView textView, Integer item) {
        textView.setText(item);

        if(EnumUtilities.isNotDescription(Gender.class, item))
            textView.setCompoundDrawablesWithIntrinsicBounds(
                    getContext().getDrawable(EnumUtilities.iconOfStrId(Gender.class, item)),
                    null, null, null);
    }
}
