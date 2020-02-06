package com.run_walk_tracking_gps.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.SqlLiteWorkoutDao;
import com.run_walk_tracking_gps.db.tables.WorkoutDescriptor;
import com.run_walk_tracking_gps.gui.components.adapter.listview.DetailsWorkoutAdapter;
import com.run_walk_tracking_gps.gui.fragments.MapFragment;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.service.NetworkServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class DetailsWorkoutActivity extends  CommonActivity{

    private final static String TAG = DetailsWorkoutActivity.class.getName();

    private final static int REQUEST_MODIFY = 0;

    private MenuItem cancel;
    private ListView summary_workout;
    private FloatingActionButton summary_ok;

    private DetailsWorkoutAdapter adapter;

    private boolean isSummary = false;
    private Workout workout;

    @SuppressLint("RestrictedApi")
    @Override
    protected void init(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate");
        setContentView(R.layout.activity_details_workout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        summary_workout = findViewById(R.id.summary);
        summary_ok = findViewById(R.id.summary_ok);

        workout = (Workout)getIntent().getParcelableExtra(KeysIntent.SUMMARY);
        if(workout!=null){ // SUMMARY_DETAILS
            isSummary = true;
            workout.setContext(this);
            Log.d(TAG, workout.toString());

            setMapView(workout.getMapRoute());

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(R.string.summary_workout);
        }
        else { // DETAILS
            workout = (Workout)getIntent().getParcelableExtra(KeysIntent.DETAIL);
            if(workout!=null){
                workout.setContext(this);

                Log.d(TAG, workout.toString());
                setMapView(workout.getMapRoute());

                summary_ok.setVisibility(View.GONE);
                getSupportActionBar().setTitle(R.string.detail_workout);
            }
        }
       if(workout!=null){
           adapter = new DetailsWorkoutAdapter(this, workout.details(true));
           summary_workout.setAdapter(adapter);
       }
    }

    private void setMapView(String mapRoute){
        if(mapRoute!=null && !mapRoute.equals("null")){
            Log.e(TAG, "MAP ROUTE "+(mapRoute.equals("null")));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.summary_map, MapFragment.createWithArguments(mapRoute))
                    .commit();
        }else {
            findViewById(R.id.summary_map).setVisibility(View.GONE);
        }
    }

    @Override
    protected void listenerAction() {
        summary_ok.setOnClickListener(v ->saveWorkout());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_workout, menu);

        if(isSummary){
            cancel = menu.findItem(R.id.summary_cancel_workout);
            menu.findItem(R.id.summary_modify_workout).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.summary_modify_workout:
                final Intent intentModify = new Intent(this, ModifyWorkoutActivity.class);
                intentModify.putExtra(KeysIntent.MODIFY_WORKOUT, workout);
                startActivityForResult(intentModify, REQUEST_MODIFY);
                break;

            case R.id.summary_cancel_workout:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.delete_workout_mex)
                        .setPositiveButton(R.string.delete, (dialog, id) -> {

                            if(isSummary)
                                setResult(RESULT_CANCELED, new Intent());
                            else {
                                if(SqlLiteWorkoutDao.create(this).delete(workout.getIdWorkout())){
                                    Preferences.Session.update(this);

                                    try {

                                        NetworkServiceHandler.getInstance(this, NetworkHelper.Constant.DELETE,
                                                NetworkHelper.Constant.WORKOUT,
                                                new JSONObject().put(WorkoutDescriptor.ID_WORKOUT,
                                                        workout.getIdWorkout()).toString())
                                                .startService();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    setResult(RESULT_OK, new Intent());
                                }else
                                    setResult(RESULT_CANCELED, new Intent());
                            }
                            finish();
                        })
                        .setNegativeButton(R.string.cancel, null).create().show();
                break;

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        switch (requestCode){
            case REQUEST_MODIFY:
                if(resultCode==Activity.RESULT_OK) {
                    //Toast.makeText(this, KeysIntent.CHANGED_WORKOUT, Toast.LENGTH_LONG).show();
                    Workout work = (Workout) data.getParcelableExtra(KeysIntent.CHANGED_WORKOUT);
                    if(work!=null){
                        work.setContext(this);
                        adapter.updateDetails(work);
                        workout = work;
                        Log.d(TAG, "Workout= " + workout);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "OnBackPressed");
        if(isSummary)
            saveWorkout();
        else
            super.onBackPressed();

    }

    private void saveWorkout(){
        Log.d(KeysIntent.NEW_WORKOUT, "Save Workout (Summary)");

        try {
            JSONObject bodyJson = workout.toJson(this);
            Log.d(TAG, bodyJson.toString());
            long id_workout = SqlLiteWorkoutDao.create(this).insert(bodyJson);
            if(id_workout!=-1){
                Preferences.Session.update(this);

                NetworkServiceHandler.getInstance(this, NetworkHelper.Constant.INSERT,
                        NetworkHelper.Constant.WORKOUT, bodyJson.put(WorkoutDescriptor.ID_WORKOUT, id_workout).toString())
                        .startService();


                setResult(RESULT_OK, new Intent());
            }else {
                setResult(RESULT_CANCELED, new Intent());
            }
            finish();
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

}



