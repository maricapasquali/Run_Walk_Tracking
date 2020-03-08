package com.run_walk_tracking_gps.gui.components.adapter.spinner;

import android.content.Context;
import android.widget.TextView;

import com.run_walk_tracking_gps.model.enumerations.Gender;

public class GenderAdapterSpinner extends CustomSpinnerAdapter<Gender> {

    public GenderAdapterSpinner(Context context) {
        super(context, Gender.values(), false);
    }

    @Override
    protected void setItemSpinner(TextView textView, Gender item) {
        textView.setText(item.getStrId());
        textView.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(item.getIconId()),
                null, null, null);
    }
}
