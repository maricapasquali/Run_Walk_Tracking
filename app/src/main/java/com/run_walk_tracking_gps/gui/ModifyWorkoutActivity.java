package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.adapter.listview.DetailsWorkoutAdapter;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.dialog.DistanceDialog;
import com.run_walk_tracking_gps.gui.dialog.DurationDialog;
import com.run_walk_tracking_gps.gui.dialog.EnergyDialog;
import com.run_walk_tracking_gps.gui.dialog.SportDialog;

import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.gui.enumeration.InfoWorkout;

public class ModifyWorkoutActivity extends  CommonActivity {
    private final static String TAG = ModifyWorkoutActivity.class.getName();

    private ListView details_workout;
    private ImageButton summary_ok;

    private Workout workout ;



    @Override
    protected void initGui() {
        setContentView(R.layout.activity_details_workout);
        getSupportActionBar().setTitle(R.string.modify);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        details_workout = findViewById(R.id.summary);

        summary_ok = findViewById(R.id.summary_ok);
        summary_ok.setVisibility(View.GONE);

        if(getIntent()!=null){
            workout = getIntent().getParcelableExtra(getString(R.string.modify_workout));
            if(workout!=null){
               Log.d(TAG, workout.toString());

                final DetailsWorkoutAdapter adapter = new DetailsWorkoutAdapter(this, workout.toArrayString(), true);
                details_workout.setAdapter(adapter);
            }
        }
    }



    @Override
    protected void listenerAction() {
        details_workout.setOnItemClickListener((parent, view, position, id) -> {
            final InfoWorkout info = (InfoWorkout)parent.getAdapter().getItem(position);
            Toast.makeText(ModifyWorkoutActivity.this,
                    "Item = " + parent.getAdapter().getItem(position), Toast.LENGTH_LONG).show();

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
                    DurationDialog.create(ModifyWorkoutActivity.this, duration -> {
                        detail.setText(duration);
                        workout.setTime(duration);
                    }).show();
                    break;
                case DISTANCE:
                    DistanceDialog.create(ModifyWorkoutActivity.this, distance -> {
                        detail.setText(distance);
                        workout.setDistance(distance);
                    }).show();
                    break;
                case CALORIES:
                    EnergyDialog.create(ModifyWorkoutActivity.this, calories ->{
                        detail.setText(calories);
                        workout.setCalories(calories);
                    }).show();
                    break;
            }

            // TODO: 10/17/2019 RICALCOLARE VELOCITA SE CAMBIATE DURATA E/O DISTANZA
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
        Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.changed_workout), workout);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}



