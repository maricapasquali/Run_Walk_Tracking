package com.run_walk_tracking_gps.gui.activity_of_settings;


import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.adapter.listview.MeasureAdapter;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.model.Measure;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

public class MeasureUnitActivity extends CommonActivity implements RadioGroup.OnCheckedChangeListener,  Response.Listener<JSONObject>{

    private final static String TAG = MeasureUnitActivity.class.getName();

    private ListView measure_unit;

    private String id_user;
    private JSONObject appJson ;
    private JSONObject settingsJson ;

    private int distance;
    private int weight;
    private int height;

    @Override
    protected void init() {
        setContentView(R.layout.activity_measure_unit);
        getSupportActionBar().setTitle(R.string.measure_unit);

        measure_unit = findViewById(R.id.measures_units);

        try {
            id_user = Preferences.getIdUserLogged(this);
            appJson = Preferences.getAppJsonUserLogged(this, id_user);
            settingsJson = (JSONObject)appJson.get(FieldDataBase.SETTINGS.toName());

            JSONObject unit_measure = (JSONObject)settingsJson.get(FieldDataBase.UNIT_MEASURE.toName());

            distance = (int)((JSONObject)unit_measure.get(FieldDataBase.DISTANCE.toName())).get(FieldDataBase.ID_UNIT.toName());
            weight = (int)((JSONObject)unit_measure.get(FieldDataBase.WEIGHT.toName())).get(FieldDataBase.ID_UNIT.toName());
            height = (int)((JSONObject)unit_measure.get(FieldDataBase.HEIGHT.toName())).get(FieldDataBase.ID_UNIT.toName());

            Map<Measure.Type, Integer> map = new HashMap<>();
            map.put(Measure.Type.DISTANCE, distance);
            map.put(Measure.Type.WEIGHT, weight);
            map.put(Measure.Type.HEIGHT, height);

            final MeasureAdapter adapter = new MeasureAdapter(this,this, map);
            measure_unit.setAdapter(adapter);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void listenerAction() {
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        boolean is_default = true;
        int id_unit ;
        String filter = "";
        switch (checkedId){
            case R.id.unit_1: id_unit = 1; break;
            case R.id.unit_2: id_unit = 2; break;
            default: throw new IllegalArgumentException();
        }
        String measure = ((TextView)((RelativeLayout)group.getParent()).getChildAt(0)).getText().toString();
        if(measure.equals(getString(Measure.Type.DISTANCE.getStrId()))) {
            filter = FieldDataBase.UNIT_DISTANCE.toName();
            is_default = distance==id_unit;
        }else if(measure.equals(getString(Measure.Type.WEIGHT.getStrId()))){
            filter = FieldDataBase.UNIT_WEIGHT.toName();
            is_default = weight==id_unit;
        }else if(measure.equals(getString(Measure.Type.HEIGHT.getStrId()))){
            filter = FieldDataBase.UNIT_HEIGHT.toName();
            is_default = height==id_unit;
        }

        if(!is_default){
            try {
                JSONObject bodyJson = new JSONObject();
                bodyJson.put(FieldDataBase.ID_USER.toName(), id_user)
                        .put(FieldDataBase.FILTER.toName(), filter)
                        .put(FieldDataBase.VALUE.toName(), id_unit);

                if(!HttpRequest.requestUpdateSetting(this, bodyJson, this)){
                    Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }

    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if(HttpRequest.someError(response) || !(boolean)response.get("update")){
                Snackbar.make(findViewById(R.id.snake), response.toString(), Snackbar.LENGTH_LONG).show();
            }else {
                final Iterable<String> iterable = response::keys;
                final String measure = StreamSupport.stream(iterable.spliterator(), false).filter(i -> !i.equals("update")).findFirst().orElse(null);
                JSONObject unit_measure = ((JSONObject)settingsJson.get(FieldDataBase.UNIT_MEASURE.toName()));
                unit_measure.put(measure, response.get(measure));
                settingsJson.put(FieldDataBase.UNIT_MEASURE.toName(), unit_measure);
                appJson.put(FieldDataBase.SETTINGS.toName(), settingsJson);
                Preferences.getSharedPreferencesSettingUserLogged(this).edit().putString(id_user, appJson.toString()).apply();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
