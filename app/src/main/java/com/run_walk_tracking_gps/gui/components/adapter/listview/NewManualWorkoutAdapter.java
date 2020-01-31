package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Workout;

public class NewManualWorkoutAdapter extends NewInformationAdapter<Workout.Info> {

    private final String TAG = NewManualWorkoutAdapter.class.getName();

    public NewManualWorkoutAdapter(Context context, View.OnFocusChangeListener listener) {
        super(context, Workout.Info.infoWorkoutNoSpeed(), listener);
    }

    @Override
    protected void addInfo(TextInputLayout title, TextInputEditText detail, Workout.Info item, int position) {
        title.setHint(getContext().getString(item.getStrId()));
        detail.setCompoundDrawablesWithIntrinsicBounds(darkIcon(item.getIconId()), null, null, null);
        if(Workout.Info.valuesNotRequired(item)) detail.setText(R.string.no_available_abbr);
    }
}
