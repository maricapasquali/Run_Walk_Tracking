package com.run_walk_tracking_gps.gui.adapter.listview;

import android.content.Context;
import android.widget.TextView;

import com.run_walk_tracking_gps.gui.enumeration.InfoWeight;

import java.util.List;

public class ModifyWeightAdapter extends NewWeightAdapter{

    private List<String> modifyWeight;

    public ModifyWeightAdapter(Context _context_, List<String> modifyWeight) {
        super(_context_);
        this.modifyWeight = modifyWeight;
    }


    @Override
    protected void addInfo(TextView title, TextView detail, InfoWeight item, int position) {
        title.setText(item.getStrId());
        detail.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(item.getIconId()), null, null, null);
        detail.setText(modifyWeight.get(position));
    }
}
