package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Workout;

public class NewManualWorkoutAdapter extends NewInformationAdapter<Workout.Info> {

    private final String TAG = NewManualWorkoutAdapter.class.getName();

    public NewManualWorkoutAdapter(Context context) {
        super(context, Workout.Info.infoWorkoutNoSpeed());
    }


    @Override
    protected void addInfo(TextView title, TextView detail, Workout.Info item, int position) {
        title.setText(item.getStrId());
        if(item.getIconId()!=0)
            detail.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(item.getIconId()), null, null, null);

        if(Workout.Info.valuesNotRequired(item)) detail.setText(R.string.no_available_abbr);
    }
}
