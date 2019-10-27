package com.run_walk_tracking_gps.gui.adapter.spinner;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.TextView;

import com.run_walk_tracking_gps.utilities.EnumUtilities;
import com.run_walk_tracking_gps.model.enumerations.Target;

public class TargetAdapterSpinner extends CustomSpinnerAdapter<Integer> {

    public TargetAdapterSpinner(Context context, boolean isDisabledFirst) {
        super(context, isDisabledFirst ?
                       EnumUtilities.valuesWithDescription(Target.class) :
                       EnumUtilities.valuesStrId(Target.class), isDisabledFirst);
    }


    @Override
    protected void setItemSpinner(TextView textView, Integer item) {
        // Set the disable item text color
        textView.setText(item);

        if(EnumUtilities.isNotDescription(Target.class, item))
            textView.setCompoundDrawablesWithIntrinsicBounds(
                    getContext().getDrawable(EnumUtilities.iconOfStrId(Target.class,item)),
                    null, null, null);
    }
}
