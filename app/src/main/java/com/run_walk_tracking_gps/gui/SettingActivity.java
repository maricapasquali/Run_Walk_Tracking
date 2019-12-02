package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.activity_of_settings.InfoActivity;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.SportAdapterSpinner;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.TargetAdapterSpinner;
import com.run_walk_tracking_gps.gui.activity_of_settings.MeasureUnitActivity;
import com.run_walk_tracking_gps.gui.activity_of_settings.UserActivity;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.gui.components.dialog.LanguageDialog;
import com.run_walk_tracking_gps.intent.KeysIntent;
import com.run_walk_tracking_gps.model.UserBuilder;
import com.run_walk_tracking_gps.model.enumerations.Language;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Stream;

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

    private String id_user;

    private JSONObject settingsJson;


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

        try{

            id_user = Preferences.getIdUserLogged(this);
            settingsJson = Preferences.getSettingsJsonUserLogged(this, id_user);

            int sport_default = (int) ((JSONObject)settingsJson.get(FieldDataBase.SPORT.toName())).get(FieldDataBase.ID_SPORT.toName()) -1;
            int target_default = (int) ((JSONObject)settingsJson.get(FieldDataBase.TARGET.toName())).get(FieldDataBase.ID_TARGET.toName()) -1;

            sport.setSelection(sport_default);
            target.setSelection(target_default);

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
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
                onChangeSpinnerSelection(FieldDataBase.SPORT.toName(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        target.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onChangeSpinnerSelection(FieldDataBase.TARGET.toName(), position);
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
        info.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.info), Toast.LENGTH_SHORT).show();
            final Intent intent = new Intent(this, InfoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
        profile.setOnClickListener(v ->{
            try {

                final JSONObject bodyJson = new JSONObject().put(FieldDataBase.ID_USER.toName(), Integer.valueOf(Preferences.getIdUserLogged(this)));
                if(!HttpRequest.requestUserInformation(this, bodyJson, response -> {

                    if(HttpRequest.someError(response))
                        Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
                    else {
                        try {
                            final JSONObject userInfo = (JSONObject) response.get("user");
                            final Intent intent = new Intent(this, UserActivity.class);
                            intent.putExtra(KeysIntent.USER_INFO, UserBuilder.create(this, userInfo).build());
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })){
                    Toast.makeText(this, getString(R.string.internet_not_available), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
        unit.setOnClickListener(v -> startActivity(new Intent(this, MeasureUnitActivity.class)));
        exit.setOnClickListener(v -> {
            Preferences.unSetUserLogged(this);

            final Intent intent = new Intent(this, BootAppActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }


    private void onChangeSpinnerSelection(String type, int position){
        try {
            int id = position +1;
            int s_default = (int) ((JSONObject)settingsJson.get(type)).get("id_"+type);
            Log.d(TAG, "Selected " + type + " = " + id + ", Default = " + s_default);
            if(id != s_default){
                // request update
                JSONObject bodyJson = new JSONObject();
                bodyJson.put(FieldDataBase.ID_USER.toName(), Integer.valueOf(id_user))
                        .put(FieldDataBase.FILTER.toName(), type)
                        .put(FieldDataBase.VALUE.toName(), id);

                if(!HttpRequest.requestUpdateSetting(SettingActivity.this, bodyJson, response -> {
                    try {
                        if(Stream.of(response.keys()).anyMatch(i -> i.next().equals(HttpRequest.ERROR)) || !(boolean)response.get("update")){
                            Snackbar.make(findViewById(R.id.snake), response.toString(), Snackbar.LENGTH_LONG).show();
                        }else {

                            final JSONObject appJson = Preferences.getAppJsonUserLogged(this, id_user);
                            settingsJson.put(type, (JSONObject)response.get(type));
                            appJson.put(FieldDataBase.SETTINGS.toName(), settingsJson);
                            Preferences.getSharedPreferencesSettingUserLogged(this).edit().putString(id_user, appJson.toString()).apply();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                })){
                    Toast.makeText(SettingActivity.this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
