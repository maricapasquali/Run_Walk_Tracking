package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;
import android.widget.TextView;

import com.run_walk_tracking_gps.model.StatisticsData;


public class NewWeightAdapter extends NewInformationAdapter<StatisticsData.InfoWeight> {

    private final String TAG = NewWeightAdapter.class.getName();

    public NewWeightAdapter(Context context) {
        super(context, StatisticsData.InfoWeight.values());
    }

    @Override
    protected void addInfo(TextView title, TextView detail, StatisticsData.InfoWeight item, int position) {
        title.setText(item.getStrId());
        detail.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(item.getIconId()), null, null, null);
    }
}
