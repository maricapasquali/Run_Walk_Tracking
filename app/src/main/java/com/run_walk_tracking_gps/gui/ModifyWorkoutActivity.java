package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.gui.components.adapter.listview.DetailsWorkoutAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.components.dialog.DistanceDialog;
import com.run_walk_tracking_gps.gui.components.dialog.DurationDialog;
import com.run_walk_tracking_gps.gui.components.dialog.EnergyDialog;
import com.run_walk_tracking_gps.gui.components.dialog.SportDialog;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.model.Workout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ModifyWorkoutActivity extends  CommonActivity implements Response.Listener<JSONObject>  {

    private final static String TAG = ModifyWorkoutActivity.class.getName();

    private ListView details_workout;
    private ImageButton summary_ok;

    private Workout oldWorkout;
    private Workout workout;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_details_workout);
        getSupportActionBar().setTitle(R.string.modify);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        details_workout = findViewById(R.id.summary);
        findViewById(R.id.summary_map).setVisibility(View.GONE);
        summary_ok = findViewById(R.id.summary_ok);
        summary_ok.setVisibility(View.GONE);

        if(getIntent()!=null){
            oldWorkout = getIntent().getParcelableExtra(KeysIntent.MODIFY_WORKOUT);
            if(oldWorkout!=null){
                oldWorkout.setContext(this);
                workout = oldWorkout.clone();

                Log.d(TAG,"OLD " + oldWorkout.toString());
                Log.d(TAG,"CLONE " + workout.toString());

                final DetailsWorkoutAdapter adapter = new DetailsWorkoutAdapter(this, oldWorkout.details(false));
                details_workout.setAdapter(adapter);
            }
        }
    }


    @Override
    protected void listenerAction() {
        details_workout.setOnItemClickListener((parent, view, position, id) -> {
            final Map.Entry<Workout.Info, Object> entry = (Map.Entry<Workout.Info, Object>)parent.getAdapter().getItem(position);
            final Workout.Info info = entry.getKey();
            Log.d(TAG,"Item = " + entry);

            final TextView detail = (TextView)view.findViewById(R.id.detail_description);

            switch (info){
                case DATE:
                    DateTimePickerDialog.createDateTimePicker(ModifyWorkoutActivity.this, (date, calendar) -> {
                        detail.setText(date);
                        workout.setDate(calendar.getTime());
                    }).show();

                    break;
                case SPORT:
                    SportDialog.create(ModifyWorkoutActivity.this, detail.getText(), (val, description) -> {
                        detail.setText(description);
                        detail.setCompoundDrawablesWithIntrinsicBounds(getDrawable(val.getIconId()), null, null,null);
                        workout.setSport(val);
                    }).show();

                    break;

                case TIME:
                    DurationDialog.create(ModifyWorkoutActivity.this, durationMeasure -> {
                        if (durationMeasure!=null){
                            detail.setText(durationMeasure.toString());
                            workout.getDuration().setValue(true, durationMeasure.getValue(true));
                            workout.setMiddleSpeed();
                        }
                    }).show();
                    break;
                case DISTANCE:
                    DistanceDialog.create(ModifyWorkoutActivity.this, (distanceMeasure)  -> {
                        if(distanceMeasure==null){
                            detail.setText(R.string.no_available_abbr);
                            workout.getDistance().setValue(true, 0d);
                        }else{
                            detail.setText(distanceMeasure.toString());
                            workout.getDistance().setValue(false,distanceMeasure.getValue(false));
                            workout.setMiddleSpeed();
                        }
                    }).show();
                    break;
                case CALORIES:
                    EnergyDialog.create(ModifyWorkoutActivity.this,(caloriesMeasure) ->{

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
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_profile:
                saveWorkoutChanged();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveWorkoutChanged(){
        try {

            final JSONObject bodyJson = new JSONObject().put(HttpRequest.Constant.ID_WORKOUT, workout.getIdWorkout());
            if(!workout.getDate().equals(oldWorkout.getDate())){
                bodyJson.put(HttpRequest.Constant.DATE, workout.getDate());
            }
            if(!workout.getSport().equals(oldWorkout.getSport())){
                bodyJson.put(HttpRequest.Constant.SPORT, workout.getSport());
            }
            if(!workout.getDuration().getValue(true).equals(oldWorkout.getDuration().getValue(true))){
                bodyJson.put(HttpRequest.Constant.DURATION, workout.getDuration().getValue(true));
            }
            if(!workout.getDistance().getValue(true).equals(oldWorkout.getDistance().getValue(true))){
                bodyJson.put(HttpRequest.Constant.DISTANCE, workout.getDistance().getValue(true));
            }
            if(!workout.getCalories().getValue(true).equals(oldWorkout.getCalories().getValue(true))){
                bodyJson.put(HttpRequest.Constant.CALORIES, workout.getCalories().getValue(true));
            }

            Log.d(TAG, bodyJson.toString());
            HttpRequest.requestUpdateWorkout(this, bodyJson, this);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InternetNoAvailableException e) {
            e.alert();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {

            //Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();
            Intent returnIntent = new Intent();
            if(response.getBoolean(HttpRequest.Constant.UPDATE)){
                returnIntent.putExtra(KeysIntent.CHANGED_WORKOUT, workout);
            }
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}



