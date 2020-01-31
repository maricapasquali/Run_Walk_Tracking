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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.SqlLiteWorkoutDao;
import com.run_walk_tracking_gps.db.tables.WorkoutDescriptor;
import com.run_walk_tracking_gps.gui.components.adapter.listview.DetailsWorkoutAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.components.dialog.DistanceDialog;
import com.run_walk_tracking_gps.gui.components.dialog.DurationDialog;
import com.run_walk_tracking_gps.gui.components.dialog.EnergyDialog;
import com.run_walk_tracking_gps.gui.components.dialog.SportDialog;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.service.NetworkServiceHandler;
import com.run_walk_tracking_gps.utilities.EnumUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ModifyWorkoutActivity extends  CommonActivity{

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

                final DetailsWorkoutAdapter adapter =
                        new DetailsWorkoutAdapter(this,
                                oldWorkout.details(false), showDialog());
                details_workout.setAdapter(adapter);
            }
        }
    }

    private View.OnFocusChangeListener showDialog(){
        return (view, hasFocus) ->{
            if(hasFocus){
                final TextInputEditText detail = (TextInputEditText)view;
                final TextInputLayout detailTitle = (TextInputLayout) detail.getParent().getParent();

                Workout.Info info = null;
                try{
                    info = (Workout.Info)EnumUtilities.getEnumFromString(Workout.Info.class , this, detailTitle.getHint().toString());
                }catch (Exception ignored){
                }
                Log.d(TAG, detailTitle.getHint().toString());

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

            }
        };
    }

    @Override
    protected void listenerAction() {
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

            final JSONObject bodyJson = new JSONObject().put(WorkoutDescriptor.ID_WORKOUT, workout.getIdWorkout());
            if(!workout.getDate().equals(oldWorkout.getDate())){
                bodyJson.put(WorkoutDescriptor.DATE, workout.getUnixTime());
            }
            if(!workout.getSport().equals(oldWorkout.getSport())){
                bodyJson.put(WorkoutDescriptor.SPORT, workout.getSport());
            }
            if(!workout.getDuration().getValue(true).equals(oldWorkout.getDuration().getValue(true))){
                bodyJson.put(WorkoutDescriptor.DURATION, workout.getDuration().getValue(true));
            }
            if(!workout.getDistance().getValue(true).equals(oldWorkout.getDistance().getValue(true))){
                bodyJson.put(WorkoutDescriptor.DISTANCE, workout.getDistance().getValue(true));
            }
            if(!workout.getCalories().getValue(true).equals(oldWorkout.getCalories().getValue(true))){
                bodyJson.put(WorkoutDescriptor.CALORIES, workout.getCalories().getValue(true));
            }

            Log.d(TAG, bodyJson.toString());
            if(SqlLiteWorkoutDao.create(this).update(bodyJson)){
                Preferences.Session.update(this);

                NetworkServiceHandler.getInstance(this, NetworkHelper.Constant.UPDATE,
                        NetworkHelper.Constant.WORKOUT, bodyJson.toString())
                        .bindService();

                setResult(Activity.RESULT_OK, new Intent().putExtra(KeysIntent.CHANGED_WORKOUT, workout));
            } else
                setResult(RESULT_CANCELED, new Intent());
            finish();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}



