package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.MapView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.gui.adapter.listview.DetailsWorkoutAdapter;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.dialog.DistanceDialog;
import com.run_walk_tracking_gps.gui.dialog.DurationDialog;
import com.run_walk_tracking_gps.gui.dialog.EnergyDialog;
import com.run_walk_tracking_gps.gui.dialog.SportDialog;
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
    protected void init() {
        setContentView(R.layout.activity_details_workout);
        getSupportActionBar().setTitle(R.string.modify);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        details_workout = findViewById(R.id.summary);
        ((MapView)findViewById(R.id.summary_map)).setVisibility(View.GONE);
        summary_ok = findViewById(R.id.summary_ok);
        summary_ok.setVisibility(View.GONE);

        if(getIntent()!=null){
            oldWorkout = getIntent().getParcelableExtra(getString(R.string.modify_workout));
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
                    DateTimePickerDialog.create(ModifyWorkoutActivity.this, (date, calendar) -> {
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
                            workout.getDuration().setValue(durationMeasure.getValue());
                            workout.setMiddleSpeed();
                        }
                    }).show();
                    break;
                case DISTANCE:
                    DistanceDialog.create(ModifyWorkoutActivity.this, (distanceMeasure)  -> {
                        if(distanceMeasure==null){
                            detail.setText(R.string.no_available_abbr);
                            workout.getDistance().setValue(0d);
                        }else{
                            detail.setText(distanceMeasure.toString());
                            workout.getDistance().setValueFromGui(distanceMeasure.getValueToGui());
                            workout.setMiddleSpeed();
                        }
                    }).show();
                    break;
                case CALORIES:
                    EnergyDialog.create(ModifyWorkoutActivity.this,(caloriesMeasure) ->{

                        if(caloriesMeasure==null){
                            detail.setText(R.string.no_available_abbr);
                            workout.getCalories().setValue(0d);
                        }else{
                            detail.setText(caloriesMeasure.toString());
                            workout.getCalories().setValueFromGui(caloriesMeasure.getValueToGui());
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

            final JSONObject bodyJson = new JSONObject().put(FieldDataBase.ID_WORKOUT.toName(), workout.getIdWorkout());
            if(!workout.getDate().equals(oldWorkout.getDate())){
                bodyJson.put(FieldDataBase.DATE.toName(), workout.getDate());
            }
            if(!workout.getSport().equals(oldWorkout.getSport())){
                bodyJson.put(FieldDataBase.SPORT.toName(), workout.getSport());
            }
            if(!workout.getDuration().getValue().equals(oldWorkout.getDuration().getValue())){
                bodyJson.put(FieldDataBase.DURATION.toName(), workout.getDuration().getValue());
            }
            if(!workout.getDistance().getValue().equals(oldWorkout.getDistance().getValue())){
                bodyJson.put(FieldDataBase.DISTANCE.toName(), workout.getDistance().getValue());
            }
            if(!workout.getCalories().getValue().equals(oldWorkout.getCalories().getValue())){
                bodyJson.put(FieldDataBase.CALORIES.toName(), workout.getCalories().getValue());
            }

            Log.d(TAG, bodyJson.toString());
            if(!HttpRequest.requestUpdateWorkout(this, bodyJson, this))
            {
                Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if(HttpRequest.someError(response)){
                Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                if(response.getBoolean("update")){
                    returnIntent.putExtra(getString(R.string.changed_workout), workout);
                }
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}



