package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;


import android.os.Build;
import android.support.annotation.RequiresApi;


import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Spinner;

import android.widget.Switch;
import android.widget.Toast;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.activity_of_settings.InfoActivity;
import com.run_walk_tracking_gps.gui.adapter.spinner.SportAdapterSpinner;
import com.run_walk_tracking_gps.gui.adapter.spinner.TargetAdapterSpinner;
import com.run_walk_tracking_gps.gui.activity_of_settings.MeasureUnitActivity;
import com.run_walk_tracking_gps.gui.activity_of_settings.ProfileActivity;


public class SettingActivity extends CommonActivity {

    private static final String TAG = SettingActivity.class.getName();

    private Spinner sport;
    private Spinner target;

    private LinearLayout profile;
    private LinearLayout location;
    private Switch locationOnOff;
    private LinearLayout unit;
    private LinearLayout language;

    private LinearLayout playlist;
    private LinearLayout coach;

    private LinearLayout info;
    private LinearLayout exit;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initGui() {
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle(getString(R.string.setting));

        // sport
        sport = findViewById(R.id.setting_spinner_sport);
        final SportAdapterSpinner spinnerAdapterSport = new SportAdapterSpinner(this, false);
        sport.setAdapter(spinnerAdapterSport);


        // target
        target = findViewById(R.id.setting_spinner_target);
        final TargetAdapterSpinner spinnerAdapterTarget = new TargetAdapterSpinner(this, false);
        target.setAdapter(spinnerAdapterTarget);

        profile = findViewById(R.id.profile);
        location = findViewById(R.id.location);
        locationOnOff = findViewById(R.id.location_on_off);
        unit = findViewById(R.id.unit);
        language = findViewById(R.id.language);

        playlist = findViewById(R.id.playlist);
        coach = findViewById(R.id.voacal_coach);

        info = findViewById(R.id.info);
        exit = findViewById(R.id.exit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void listenerAction() {

        location.setOnClickListener(v -> Toast.makeText(this, getString(R.string.location), Toast.LENGTH_SHORT).show());
        locationOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> Toast.makeText(this,getString(isChecked ?  R.string.on : R.string.off), Toast.LENGTH_SHORT).show());
        language.setOnClickListener(v -> Toast.makeText(this, getString(R.string.language), Toast.LENGTH_SHORT).show());
        playlist.setOnClickListener(v -> Toast.makeText(this, getString(R.string.playlist), Toast.LENGTH_SHORT).show());
        coach.setOnClickListener(v -> Toast.makeText(this, getString(R.string.vocal_coach), Toast.LENGTH_SHORT).show());
        info.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.info), Toast.LENGTH_SHORT).show();
            final Intent intent = new Intent(this, InfoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        unit.setOnClickListener(v -> startActivity(new Intent(this, MeasureUnitActivity.class)));
        exit.setOnClickListener(v -> {
            final Intent intent = new Intent(this, BootAppActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
