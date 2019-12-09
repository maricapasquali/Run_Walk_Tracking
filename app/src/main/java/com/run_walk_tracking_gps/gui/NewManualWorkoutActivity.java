package com.run_walk_tracking_gps.gui;

import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.exception.DataException;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewInformationAdapter;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewManualWorkoutAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.components.dialog.DistanceDialog;
import com.run_walk_tracking_gps.gui.components.dialog.DurationDialog;
import com.run_walk_tracking_gps.gui.components.dialog.EnergyDialog;
import com.run_walk_tracking_gps.gui.components.dialog.SportDialog;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.Workout;

import org.json.JSONException;
import org.json.JSONObject;


public class NewManualWorkoutActivity extends NewInformationActivity implements NewInformationActivity.OnAddInfoListener, Response.Listener<JSONObject> {

    private final static String TAG = NewManualWorkoutActivity.class.getName();

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
                DateTimePickerDialog.createDateTimePicker(NewManualWorkoutActivity.this, (date, calendar) -> {
                    workout.setDate(calendar.getTime());
                    detail.setText(date);
                }).show();
                break;
            case SPORT:
                SportDialog.create(NewManualWorkoutActivity.this, detail.getText(), (val, description) -> {
                    detail.setText(val.getStrId());
                    detail.setCompoundDrawablesWithIntrinsicBounds(getDrawable(val.getIconId()), null, null, null);

                    workout.setSport(val);
                }).show();
                break;
            case TIME:
                DurationDialog.create(NewManualWorkoutActivity.this, (durationMeasure) -> {
                   if(durationMeasure!=null){
                       workout.getDuration().setValue(true, durationMeasure.getValue(true));
                       detail.setText(durationMeasure.toString());
                   }
                   workout.setMiddleSpeed();
                }).show();
                break;
            case DISTANCE:
                DistanceDialog.create(NewManualWorkoutActivity.this, (distanceMeasure) -> {
                    if(distanceMeasure==null){
                        detail.setText(R.string.no_available_abbr);
                        workout.getDistance().setValue(true, 0d);
                    }else{
                        detail.setText(distanceMeasure.toString());
                        workout.getDistance().setValue(false, distanceMeasure.getValue(false));
                    }
                    workout.setMiddleSpeed();
                }).show();
                break;
            case CALORIES:
                EnergyDialog.create(NewManualWorkoutActivity.this, (caloriesMeasure)  -> {
                    if(caloriesMeasure==null){
                        detail.setText(R.string.no_available_abbr);
                        workout.getCalories().setValue(true, 0d);
                    }else{
                        detail.setText(caloriesMeasure.toString());
                        workout.getCalories().setValue(false, caloriesMeasure.getValue(false));
                    }
                }).show();
                break;
        }
    }

    @Override
    public void onClickAddInfo() {
        // Set workout
        try{
            Log.d(TAG, "" +workout.getDistance().getValue(false));
            if(!workout.isMinimalSet())
                throw new DataException(this, Workout.class);

            final JSONObject bodyJson = new JSONObject().put(HttpRequest.Constant.ID_USER, Integer.valueOf(DefaultPreferencesUser.getIdUserLogged(this)))
                                                        .put(HttpRequest.Constant.SPORT, workout.getSport())
                                                        .put(HttpRequest.Constant.DURATION, workout.getDuration().getValue(true))
                                                        .put(HttpRequest.Constant.DATE, workout.getDate().getTime());
            if(!Measure.isNullOrEmpty(workout.getDistance())) bodyJson.put(HttpRequest.Constant.DISTANCE, workout.getDistance().getValue(true));
            if(!Measure.isNullOrEmpty(workout.getCalories())) bodyJson.put(HttpRequest.Constant.CALORIES, workout.getCalories().getValue(true));

            HttpRequest.requestNewWorkout(this, bodyJson, this);

        }catch (JSONException je){
            je.printStackTrace();
        } catch (InternetNoAvailableException e) {
            e.alert();
        } catch (DataException e) {
            e.alert();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            int id_workout = response.getInt(HttpRequest.Constant.ID_WORKOUT);
            // save and send to workouts list
            final Intent newManualWorkoutIntent = new Intent();
            workout.setIdWorkout(id_workout);
            newManualWorkoutIntent.putExtra(KeysIntent.NEW_WORKOUT_MANUAL, workout);
            setResult(RESULT_OK, newManualWorkoutIntent);
            finish();

        } catch (JSONException e) {
           e.printStackTrace();
        }
    }

}
