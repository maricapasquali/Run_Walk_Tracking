package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.adapter.listview.NewInformationAdapter;
import com.run_walk_tracking_gps.gui.adapter.listview.NewManualWorkoutAdapter;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.dialog.DistanceDialog;
import com.run_walk_tracking_gps.gui.dialog.DurationDialog;
import com.run_walk_tracking_gps.gui.dialog.EnergyDialog;
import com.run_walk_tracking_gps.gui.dialog.SportDialog;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.gui.enumeration.InfoWorkout;


public class NewManualWorkoutActivity extends NewInformationActivity implements NewInformationActivity.OnAddInfoListener {

    private  final static String TAG = NewManualWorkoutActivity.class.getName();
    private Workout workout = new Workout();


    public NewManualWorkoutActivity() {
        super(R.string.workout);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public NewInformationAdapter getAdapterListView() {
        return new NewManualWorkoutAdapter(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSetInfo(AdapterView<?> parent, View view, int position, long id) {
        final InfoWorkout title =(InfoWorkout) parent.getAdapter().getItem(position);
        final TextView detail = view.findViewById(R.id.detail_description);

        switch (title){
            case DATE:
                DateTimePickerDialog.create(NewManualWorkoutActivity.this, (date, calendar) -> {
                    workout.setDate(calendar.getTime());
                    detail.setText(date);
                }).show();
                break;
            case SPORT:
                SportDialog.create(NewManualWorkoutActivity.this, detail.getText(), (val, description) -> {
                    detail.setText(val.getStrId());
                    detail.setCompoundDrawablesWithIntrinsicBounds(getDrawable(val.getIconId()), null, null, null);
                    Toast.makeText(NewManualWorkoutActivity.this, description, Toast.LENGTH_SHORT).show();
                    workout.setSport(val);
                }).show();
                break;
            case TIME:
                DurationDialog.create(NewManualWorkoutActivity.this, duration -> {
                    workout.setTime(duration);
                    detail.setText(duration);
                }).show();
                break;
            case DISTANCE:
                DistanceDialog.create(NewManualWorkoutActivity.this, distance -> {
                    workout.setDistance(distance);
                    detail.setText(distance);
                }).show();
                break;
            case CALORIES:
                EnergyDialog.create(NewManualWorkoutActivity.this, calories -> {
                    workout.setCalories(calories);
                    detail.setText(calories);
                }).show();
                break;
        }
    }

    @Override
    public void onClickAddInfo() {
        // Set workout
        try{
            if(!workout.isMinimalSet())
                throw new NullPointerException("Workout doesn't correctly set !! " + workout);

            // save and send to workouts list
            final Intent newManualWorkoutIntent = new Intent();
            workout.setId(12);
            newManualWorkoutIntent.putExtra(getString(R.string.new_workout_manual), workout);
            setResult(RESULT_OK, newManualWorkoutIntent);
            finish();
        }catch (NullPointerException e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(this, getString(R.string.workout_not_set_correctly), Toast.LENGTH_LONG).show();
        }
    }
}
