package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.model.StatisticsData;

import java.util.List;

public class ModifyWeightAdapter extends NewWeightAdapter{

    private List<String> modifyWeight;

    public ModifyWeightAdapter(Context _context_, List<String> modifyWeight, View.OnClickListener onClickListener) {
        super(_context_, onClickListener);
        this.modifyWeight = modifyWeight;
    }

    @Override
    protected void addInfo(TextInputLayout title, TextInputEditText detail, StatisticsData.InfoWeight item, int position) {
        title.setHint(getContext().getString(item.getStrId()));
        detail.setCompoundDrawablesWithIntrinsicBounds(darkIcon(item.getIconId()), null, null, null);
        detail.setText(modifyWeight.get(position));
    }
}
