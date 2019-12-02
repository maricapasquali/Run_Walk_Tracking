package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.os.Bundle;
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
import com.run_walk_tracking_gps.gui.components.adapter.listview.MeasureAdapter;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.model.Measure;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

public class MeasureUnitActivity extends CommonActivity implements RadioGroup.OnCheckedChangeListener,  Response.Listener<JSONObject>{

    private final static String TAG = MeasureUnitActivity.class.getName();

    private ListView measure_unit;

    private int distance;
    private int weight;
    private int height;

    private String filter;
    private String value;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_measure_unit);
        getSupportActionBar().setTitle(R.string.measure_unit);

        measure_unit = findViewById(R.id.measures_units);

        try {
            JSONObject unit_measure = Preferences.getUnitsMeasureDefault(this);

            distance = Measure.Type.DISTANCE.indexMeasureUnit(this, unit_measure.getString(FieldDataBase.DISTANCE.toName()))+1;
            weight = Measure.Type.WEIGHT.indexMeasureUnit(this, unit_measure.getString(FieldDataBase.WEIGHT.toName()))+1;
            height = Measure.Type.HEIGHT.indexMeasureUnit(this, unit_measure.getString(FieldDataBase.HEIGHT.toName()))+1;

            final Map<Measure.Type, Integer> map = new HashMap<>();
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

        Measure.Type type = null;
        Measure.Unit unit;

        // TODO: 11/17/2019 MIGLIORARE
        final String measure = ((TextView)((RelativeLayout)group.getParent()).getChildAt(0)).getText().toString();
        if(measure.equals(getString(Measure.Type.DISTANCE.getStrId())))
            type = Measure.Type.DISTANCE;
        else if(measure.equals(getString(Measure.Type.WEIGHT.getStrId())))
            type = Measure.Type.WEIGHT;
        else if(measure.equals(getString(Measure.Type.HEIGHT.getStrId())))
            type = Measure.Type.HEIGHT;

        if(type!=null){
            filter = type.toString().toLowerCase();

            switch (checkedId){
                case R.id.unit_1:
                    unit = type.getMeasureUnitDefault();
                    break;
                case R.id.unit_2:
                    unit = type.getMeasureUnit()[1];
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            if(unit!=null){
                value = getString(unit.getStrId());

                Log.d(TAG, "Filter = "+ filter +", Value = "+value);

                try {
                    final String  id_user = Preferences.getIdUserLogged(this);
                    JSONObject bodyJson = new JSONObject();
                    bodyJson.put(FieldDataBase.ID_USER.toName(), id_user)
                            .put(FieldDataBase.FILTER.toName(), filter)
                            .put(FieldDataBase.VALUE.toName(), value);

                    if(!HttpRequest.requestUpdateSetting(this, bodyJson, this)){
                        Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        }

    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if(HttpRequest.someError(response) || !(boolean)response.get("update")){
                Snackbar.make(findViewById(R.id.snake), response.toString(), Snackbar.LENGTH_LONG).show();
            }else {
                 Preferences.setUnitMeasure(this, filter, value);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
