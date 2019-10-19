package com.run_walk_tracking_gps.gui.adapter.listview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.enumeration.InfoWorkout;

public class NewManualWorkoutAdapter extends NewInformationAdapter<InfoWorkout> {

    private final String TAG = NewManualWorkoutAdapter.class.getName();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NewManualWorkoutAdapter(Context context) {
        super(context, InfoWorkout.infoWorkoutNoSpeed());
    }


    @Override
    protected void addInfo(TextView title, TextView detail, InfoWorkout item) {
        title.setText(item.getStrId());
        if(item.getIconId()!=0)
            detail.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(item.getIconId()), null, null, null);
        if(item==InfoWorkout.DISTANCE || item==InfoWorkout.CALORIES) {
            detail.setText(R.string.no_available_abbr);
        }
    }
}
