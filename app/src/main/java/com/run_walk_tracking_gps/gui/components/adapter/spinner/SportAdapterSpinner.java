package com.run_walk_tracking_gps.gui.components.adapter.spinner;

import android.content.Context;

import android.widget.TextView;

import com.run_walk_tracking_gps.model.enumerations.Sport;

public class SportAdapterSpinner extends CustomSpinnerAdapter<Sport> {

    public SportAdapterSpinner(Context context) {
        super(context, Sport.values(), false);
    }

    @Override
    protected void setItemSpinner(TextView textView, Sport item) {
        textView.setText(item.getStrId());
        textView.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(item.getIconId()),
                null, null, null);
    }
}
