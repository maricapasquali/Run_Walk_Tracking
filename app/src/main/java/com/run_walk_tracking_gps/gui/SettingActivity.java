package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.controller.ErrorQueue;
import com.run_walk_tracking_gps.db.dao.SqlLiteSettingsDao;
import com.run_walk_tracking_gps.gui.activity_of_settings.InfoActivity;
import com.run_walk_tracking_gps.gui.activity_of_settings.VoiceCoachActivity;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.SportAdapterSpinner;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.TargetAdapterSpinner;
import com.run_walk_tracking_gps.gui.activity_of_settings.MeasureUnitActivity;
import com.run_walk_tracking_gps.gui.activity_of_settings.UserActivity;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.model.VoiceCoach;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.model.enumerations.Target;
import com.run_walk_tracking_gps.service.NetworkServiceHandler;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.RequiresApi;

public class SettingActivity extends CommonActivity {

    private static final String TAG = SettingActivity.class.getName();

    private Spinner sport;
    private Spinner target;

    private LinearLayout profile;
    private Switch locationOnOff;
    private LinearLayout location;
    private LinearLayout unit;

    private LinearLayout playlist;
    private TextView coach;
    private Switch coachOnOff;

    private LinearLayout info;
    private LinearLayout exit;

    private Sport sportDefault;

    private Target targetDefault;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle(getString(R.string.setting));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // sport
        sport = findViewById(R.id.setting_spinner_sport);
        final SportAdapterSpinner spinnerAdapterSport = new SportAdapterSpinner(this);
        sport.setAdapter(spinnerAdapterSport);

        // target
        target = findViewById(R.id.setting_spinner_target);
        final TargetAdapterSpinner spinnerAdapterTarget = new TargetAdapterSpinner(this);
        target.setAdapter(spinnerAdapterTarget);

        profile = findViewById(R.id.profile);
        locationOnOff = findViewById(R.id.location_on_off);
        location = findViewById(R.id.location);
        unit = findViewById(R.id.unit);

        playlist = findViewById(R.id.playlist);
        coach = findViewById(R.id.setting_vocal);
        coachOnOff = findViewById(R.id.setting_vocal_on_off);


        info = findViewById(R.id.info);
        exit = findViewById(R.id.exit);

        try {
            sportDefault = Sport.valueOf(SqlLiteSettingsDao.create(this).getSportDefault());
            sport.setSelection(Stream.of(Sport.values()).collect(Collectors.toList()).indexOf(sportDefault));

            targetDefault = Target.valueOf(SqlLiteSettingsDao.create(this).getTargetDefault());
            target.setSelection(Stream.of(Target.values()).collect(Collectors.toList()).indexOf(targetDefault));


            coachOnOff.setChecked(VoiceCoach.create(this).isActive());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        ErrorQueue.getErrors(this);
        // TODO: 1/3/2020 ADD SYNC SERVICE ONRESUME
        //SyncServiceHandler.create(this).start();

        coachOnOff.setChecked(VoiceCoach.create(this).isActive());

        if((LocationUtilities.isGpsEnable(this) && !locationOnOff.isChecked()) ||
                (!LocationUtilities.isGpsEnable(this) && locationOnOff.isChecked())){

            locationOnOff.setChecked(LocationUtilities.isGpsEnable(this));
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onChangeSpinnerSelection(Enum type){

        if(SqlLiteSettingsDao.create(this).update(type, type.toString())){
            Preferences.Session.update(this);

            try {
                JSONObject data = new JSONObject().put(NetworkHelper.Constant.VALUE, type.toString());
                Log.e(TAG, data.toString());
                String filter = null;
                if(type instanceof Sport){
                    sportDefault = (Sport) type;
                    filter = NetworkHelper.Constant.SPORT;
                }else if(type instanceof Target){
                    targetDefault = (Target) type;
                    filter = NetworkHelper.Constant.TARGET;
                }
                NetworkServiceHandler.getInstance(this, NetworkHelper.Constant.UPDATE, filter, data.toString())
                                     .startService();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void listenerAction() {

        sport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Sport selectedSport = (Sport) parent.getSelectedItem();
                if(sportDefault!=selectedSport){
                    onChangeSpinnerSelection(selectedSport);
                    Log.e(TAG, "Sport = " + selectedSport.toString());
                 }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        target.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Target selectedTarget = (Target) parent.getSelectedItem();
                if (targetDefault != selectedTarget) {
                    onChangeSpinnerSelection(selectedTarget);
                    Log.e(TAG, "Target = " + selectedTarget.toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        location.setOnClickListener(v -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)));

        playlist.setOnClickListener(v -> Toast.makeText(this, getString(R.string.playlist), Toast.LENGTH_SHORT).show());


        coach.setOnTouchListener((v, event) -> {
            final LinearLayout parent = ((LinearLayout)((TextView)v).getParent());
            if(event.getAction()==MotionEvent.ACTION_DOWN ||
               event.getAction()==MotionEvent.ACTION_UP ||
               event.getAction()==MotionEvent.ACTION_CANCEL  )
                    parent.setPressed(!parent.isPressed());

            return false;
        });
        coach.setOnClickListener(v -> startActivity(new Intent(this, VoiceCoachActivity.class)));
        coachOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> VoiceCoach.create(this).setActive(isChecked));

        info.setOnClickListener(v -> startActivity(new Intent(this, InfoActivity.class)));

        profile.setOnClickListener(v -> startActivity(new Intent(this, UserActivity.class)));

        unit.setOnClickListener(v -> startActivity(new Intent(this, MeasureUnitActivity.class)));

        exit.setOnClickListener(v -> {
            //SyncServiceHandler.create(this).stop();
            Preferences.Session.logout(this);
            startActivity(new Intent(this, BootAppActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });
    }

}
