package com.run_walk_tracking_gps.gui.components.adapter.spinner;

import android.content.Context;
import android.widget.TextView;

import com.run_walk_tracking_gps.model.enumerations.Target;

public class TargetAdapterSpinner extends CustomSpinnerAdapter<Target> {

    public TargetAdapterSpinner(Context context) {
        super(context, Target.values(), false);
    }

    @Override
    protected void setItemSpinner(TextView textView, Target item) {
        textView.setText(item.getStrId());
        textView.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(item.getIconId()),
                null, null, null);
    }
}
