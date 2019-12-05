package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.gui.activity_of_settings.InfoActivity;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.SportAdapterSpinner;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.TargetAdapterSpinner;
import com.run_walk_tracking_gps.gui.activity_of_settings.MeasureUnitActivity;
import com.run_walk_tracking_gps.gui.activity_of_settings.UserActivity;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.model.enumerations.Target;
import com.run_walk_tracking_gps.utilities.EnumUtilities;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends CommonActivity {

    private static final String TAG = SettingActivity.class.getName();

    private Spinner sport;
    private Spinner target;

    private LinearLayout profile;
    private Switch locationOnOff;
    private LinearLayout location;
    private LinearLayout unit;
    private LinearLayout language;

    private LinearLayout playlist;
    private LinearLayout coach;

    private LinearLayout info;
    private LinearLayout exit;

    private int idStrTarget;
    private int idStrSport;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle(getString(R.string.setting));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // sport
        sport = findViewById(R.id.setting_spinner_sport);
        final SportAdapterSpinner spinnerAdapterSport = new SportAdapterSpinner(this, false);
        sport.setAdapter(spinnerAdapterSport);

        // target
        target = findViewById(R.id.setting_spinner_target);
        final TargetAdapterSpinner spinnerAdapterTarget = new TargetAdapterSpinner(this, false);
        target.setAdapter(spinnerAdapterTarget);

        profile = findViewById(R.id.profile);
        locationOnOff = findViewById(R.id.location_on_off);
        location = findViewById(R.id.location);
        unit = findViewById(R.id.unit);
        language = findViewById(R.id.language);

        playlist = findViewById(R.id.playlist);
        coach = findViewById(R.id.voacal_coach);

        info = findViewById(R.id.info);
        exit = findViewById(R.id.exit);


        idStrSport = DefaultPreferencesUser.getSportDefault(this).getStrId();
        sport.setSelection(spinnerAdapterSport.getPositionOf(idStrSport));

        idStrTarget = DefaultPreferencesUser.getTargetDefault(this).getStrId();
        target.setSelection(spinnerAdapterTarget.getPositionOf(idStrTarget));

    }

    @Override
    protected void onResume() {
        super.onResume();

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

    @Override
    protected void listenerAction() {

        sport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(idStrSport!=(int)parent.getSelectedItem()){
                    Sport sport = (Sport) EnumUtilities.getEnumFromStrId(Sport.class, (int)parent.getSelectedItem());
                    Log.e(TAG, sport.toString());
                    onChangeSpinnerSelection(Sport.class.getSimpleName().toLowerCase(), sport);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        target.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (idStrTarget != (int) parent.getSelectedItem()) {
                    Target target = (Target) EnumUtilities.getEnumFromStrId(Target.class, (int) parent.getSelectedItem());
                    Log.e(TAG, target.toString());
                    onChangeSpinnerSelection(Target.class.getSimpleName().toLowerCase(), target);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        location.setOnClickListener(v -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
/*      language.setOnClickListener(v -> {

            Log.e("Language", "Language default: " + Language.defaultForUser(this));
            LanguageDialog.create(this, Language.defaultForUser(this),
                        (val, description) -> {
                            try {
                                Log.e("Language", "Language : " + val + ", string : " + description);
                                final Configuration newConfiguration = Language.Utilities.changeConfiguration(SettingActivity.this, val);
                                Preferences.setLanguage(SettingActivity.this, getString(val.getCode()));

                                try {
                                    JSONObject bodyJson = new JSONObject()
                                                            .put(FieldDataBase.ID_USER.toName(), Integer.valueOf(id_user))
                                                            .put(FieldDataBase.FILTER.toName(), FieldDataBase.LANGUAGE.toName())
                                                            .put(FieldDataBase.VALUE.toName(), Preferences.getLanguageDefault(this));

                                    if (!HttpRequest.requestUpdateSetting(this, bodyJson, response -> {
                                        if(HttpRequest.someError(response))
                                            Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
                                        else {

                                            onConfigurationChanged(newConfiguration);
                                            Log.d("Language", "Configuration : " + newConfiguration);
                                        }

                                    } )) {
                                        Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
            }).show();
        });
*/

        playlist.setOnClickListener(v -> Toast.makeText(this, getString(R.string.playlist), Toast.LENGTH_SHORT).show());

        coach.setOnClickListener(v -> Toast.makeText(this, getString(R.string.vocal_coach), Toast.LENGTH_SHORT).show());

        info.setOnClickListener(v -> startActivity(new Intent(this, InfoActivity.class)));

        profile.setOnClickListener(v -> startActivity(new Intent(this, UserActivity.class)));

        unit.setOnClickListener(v -> startActivity(new Intent(this, MeasureUnitActivity.class)));

        exit.setOnClickListener(v -> {
            DefaultPreferencesUser.unSetUserLogged(this);

            final Intent intent = new Intent(this, BootAppActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }


    private void onChangeSpinnerSelection(String type, Enum value){
        try {

            String id_user = DefaultPreferencesUser.getIdUserLogged(this);
            JSONObject settingsJson = DefaultPreferencesUser.getSettingsJsonUserLogged(this, id_user);

            String s_default = settingsJson.getString(type);
            String id = value.toString();
            Log.d(TAG, "Selected " + type + " = " + id + ", Default = " + s_default);

            if(!id.equals(s_default)){
                // request update
                JSONObject bodyJson = new JSONObject();
                bodyJson.put(HttpRequest.Constant.ID_USER, Integer.valueOf(id_user))
                        .put(HttpRequest.Constant.FILTER, type)
                        .put(HttpRequest.Constant.VALUE, id);

                HttpRequest.requestUpdateSetting(SettingActivity.this, bodyJson, response -> {
                    try {
                        DefaultPreferencesUser.updateSpinnerSettings(this, type, response.getString(type));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InternetNoAvailableException e) {
            e.alert();
        }
    }
}
