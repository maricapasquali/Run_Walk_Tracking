package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ListView;

import android.widget.Toast;


import com.google.android.gms.maps.MapView;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.gui.adapter.listview.DetailsWorkoutAdapter;

import com.run_walk_tracking_gps.model.Workout;


import org.json.JSONException;
import org.json.JSONObject;

public class DetailsWorkoutActivity extends  CommonActivity {
    private final static String TAG = DetailsWorkoutActivity.class.getName();

    private final static int REQUEST_MODIFY = 0;

    private MapView summary_map;

    private ListView summary_workout;
    private ImageButton summary_ok;

    private DetailsWorkoutAdapter adapter;

    private boolean isChangedWorkout = false;
    private boolean isSummary = false;
    private Workout workout;

    @Override
    protected void init() {
        setContentView(R.layout.activity_details_workout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        summary_map = findViewById(R.id.summary_map);
        summary_workout = findViewById(R.id.summary);
        summary_ok = findViewById(R.id.summary_ok);


        workout = (Workout)getIntent().getParcelableExtra(getString(R.string.summary_workout));
        if(workout!=null){ // SUMMARY_DETAILS
            isSummary = true;
            workout.setContext(this);
            Log.d(TAG, workout.toString());


            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(R.string.summary_workout);

        }
        else { // DETAILS
            workout = (Workout)getIntent().getParcelableExtra(getString(R.string.detail_workout));
            if(workout!=null){
                workout.setContext(this);

                Log.d(TAG, workout.toString());
                if(workout.getMapRoute()==null)summary_map.setVisibility(View.GONE);

                summary_ok.setVisibility(View.GONE);
                getSupportActionBar().setTitle(R.string.detail_workout);
            }
        }
       if(workout!=null){
           adapter = new DetailsWorkoutAdapter(this, workout.details(true));
           summary_workout.setAdapter(adapter);
       }
    }

    @Override
    protected void listenerAction() {
        summary_ok.setOnClickListener(v ->saveWorkout());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!isSummary) getMenuInflater().inflate(R.menu.menu_detail_workout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.summary_modify_workout:
                Toast.makeText(this, getString(R.string.modify), Toast.LENGTH_LONG).show();

                final Intent intentModify = new Intent(this, ModifyWorkoutActivity.class);
                //intentModify.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentModify.putExtra(getString(R.string.modify_workout), workout);
                startActivityForResult(intentModify, REQUEST_MODIFY);
                break;

            case R.id.summary_cancel_workout:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.delete_workout_mex)
                        .setPositiveButton(R.string.delete, (dialog, id) -> {

                            try{
                                JSONObject bodyJson = new JSONObject().put(FieldDataBase.ID_WORKOUT.toName(), workout.getIdWorkout());

                                if(!HttpRequest.requestDeleteWorkout(this, bodyJson, response -> {

                                    try {
                                        if(HttpRequest.someError(response)){
                                            Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
                                        }else{
                                            if(response.getBoolean("delete")){
                                                final Intent resultIntent = new Intent();
                                                resultIntent.putExtra(getString(R.string.delete_workout), workout.getIdWorkout());
                                                setResult(Activity.RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                })){
                                    Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                                }

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
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
                    Toast.makeText(this, getString(R.string.changed_workout), Toast.LENGTH_LONG).show();
                    Workout work = (Workout) data.getParcelableExtra(getString(R.string.changed_workout));
                    isChangedWorkout = (work!=null);
                    if(isChangedWorkout){
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
        if(isSummary){
            // TODO: 11/2/2019 REQUEST INSERT AUTO-WORKOUT
            saveWorkout();
        }
        if(isChangedWorkout){
            Log.d(TAG, "Return Workout changed");
            Intent returnIntent = new Intent();
            returnIntent.putExtra(getString(R.string.changed_workout), workout);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        super.onBackPressed();
    }

    private void saveWorkout(){
        Log.d(getString(R.string.new_workout), "Summary : Workout = "+ workout);
        final Intent resultIntent = new Intent();
        resultIntent.putExtra(getString(R.string.new_workout), workout);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}



