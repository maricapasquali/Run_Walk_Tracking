package com.run_walk_tracking_gps.gui;

import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.adapter.listview.NewInformationAdapter;
import com.run_walk_tracking_gps.gui.adapter.listview.NewManualWorkoutAdapter;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.dialog.DistanceDialog;
import com.run_walk_tracking_gps.gui.dialog.DurationDialog;
import com.run_walk_tracking_gps.gui.dialog.EnergyDialog;
import com.run_walk_tracking_gps.gui.dialog.SportDialog;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.Workout;

import org.json.JSONException;
import org.json.JSONObject;


public class NewManualWorkoutActivity extends NewInformationActivity implements NewInformationActivity.OnAddInfoListener, Response.Listener<JSONObject> {

    private  final static String TAG = NewManualWorkoutActivity.class.getName();
    private Workout workout;


    public NewManualWorkoutActivity() {
        super(R.string.workout);
    }

    @Override
    protected void setModel() {
        workout = new Workout(this);
    }

    @Override
    public NewInformationAdapter getAdapterListView() {
        return new NewManualWorkoutAdapter(this);
    }


    @Override
    public void onSetInfo(AdapterView<?> parent, View view, int position, long id) {
        final Workout.Info title =(Workout.Info) parent.getAdapter().getItem(position);
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
                DurationDialog.create(NewManualWorkoutActivity.this, (durationMeasure) -> {
                   if(durationMeasure!=null){
                       workout.getDuration().setValue(durationMeasure.getValue());
                       detail.setText(durationMeasure.toString(this));
                   }
                   workout.setMiddleSpeed();
                }).show();
                break;
            case DISTANCE:
                DistanceDialog.create(NewManualWorkoutActivity.this, (distanceMeasure) -> {
                    if(distanceMeasure==null){
                        detail.setText(R.string.no_available_abbr);
                        workout.getDistance().setValue(0d);
                    }else{
                        detail.setText(distanceMeasure.toString(this));
                        workout.getDistance().setValueFromGui(distanceMeasure.getValueToGui());

                    }
                    workout.setMiddleSpeed();
                }).show();
                break;
            case CALORIES:
                EnergyDialog.create(NewManualWorkoutActivity.this, (caloriesMeasure)  -> {
                    if(caloriesMeasure==null){
                        detail.setText(R.string.no_available_abbr);
                        workout.getCalories().setValue(0d);
                    }else{
                        detail.setText(caloriesMeasure.toString(this));
                        workout.getCalories().setValueFromGui(caloriesMeasure.getValueToGui());
                    }
                }).show();
                break;
        }
    }

    @Override
    public void onClickAddInfo() {
        // Set workout
        try{
            Log.e(TAG, "" +workout.getDistance().getValue());
            if(!workout.isMinimalSet())
                throw new NullPointerException("Workout doesn't correctly set !! " + workout);

            final JSONObject bodyJson = new JSONObject().put(FieldDataBase.ID_USER.toName(), Integer.valueOf(Preferences.getIdUserLogged(this)))
                                                        .put(FieldDataBase.SPORT.toName(), workout.getSport())
                                                        .put(FieldDataBase.DURATION.toName(), workout.getDuration().getValue())
                                                        .put(FieldDataBase.DATE.toName(), workout.getDate());
            if(!Measure.isNullOrEmpty(workout.getDistance())) bodyJson.put(FieldDataBase.DISTANCE.toName(), workout.getDistance().getValue());
            if(!Measure.isNullOrEmpty(workout.getCalories())) bodyJson.put(FieldDataBase.CALORIES.toName(), workout.getCalories().getValue());


            if(!HttpRequest.requestNewWorkout(this, bodyJson, this)){
                Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
            }

        }catch (NullPointerException e){
            e.printStackTrace();
            //Log.e(TAG, e.getMessage());
            Toast.makeText(this, getString(R.string.workout_not_set_correctly), Toast.LENGTH_LONG).show();
        }catch (JSONException je){
            Log.e(TAG, je.getMessage());
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if(HttpRequest.someError(response)){
                Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
            }else {
                int id_workout = response.getInt(FieldDataBase.ID_WORKOUT.toName());
                // save and send to workouts list
                final Intent newManualWorkoutIntent = new Intent();
                workout.setIdWorkout(id_workout);
                newManualWorkoutIntent.putExtra(getString(R.string.new_workout_manual), workout);
                setResult(RESULT_OK, newManualWorkoutIntent);
                finish();
            }
        } catch (JSONException e) {
           Log.e(TAG, e.getMessage());
        }
    }

}
