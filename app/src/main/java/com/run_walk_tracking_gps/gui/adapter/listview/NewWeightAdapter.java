package com.run_walk_tracking_gps.gui.adapter.listview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;
import android.widget.TextView;
import com.run_walk_tracking_gps.gui.enumeration.InfoWeight;


public class NewWeightAdapter extends NewInformationAdapter<InfoWeight> {

    private final String TAG = NewWeightAdapter.class.getName();

    public NewWeightAdapter(Context context) {
        super(context, InfoWeight.values());
    }

    @Override
    protected void addInfo(TextView title, TextView detail, InfoWeight item) {
        title.setText(item.getStrId());
        detail.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(item.getIconId()), null, null, null);
    }
}
