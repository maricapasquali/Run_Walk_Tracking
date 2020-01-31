package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.model.StatisticsData;


public class NewWeightAdapter extends NewInformationAdapter<StatisticsData.InfoWeight> {

    private final String TAG = NewWeightAdapter.class.getName();

    public NewWeightAdapter(Context context, View.OnFocusChangeListener listener) {
        super(context, StatisticsData.InfoWeight.values(), listener);
    }

    @Override
    protected void addInfo(TextInputLayout title, TextInputEditText detail, StatisticsData.InfoWeight item, int position) {
        title.setHint(getContext().getString(item.getStrId()));
        detail.setCompoundDrawablesWithIntrinsicBounds(darkIcon(item.getIconId()), null, null, null);
    }
}
