package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.gui.components.adapter.listview.MeasureAdapter;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.utilities.EnumUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class MeasureUnitActivity extends CommonActivity implements RadioGroup.OnCheckedChangeListener,  Response.Listener<JSONObject>{

    private final static String TAG = MeasureUnitActivity.class.getName();

    private String filter;
    private Measure.Unit unit;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_measure_unit);
        getSupportActionBar().setTitle(R.string.measure_unit);

        final ListView measure_unit = findViewById(R.id.measures_units);

        measure_unit.setAdapter(new MeasureAdapter(this,this, getUnitDefaultForUser()));
    }

    private LinkedHashMap<Measure.Type, Measure.Unit> getUnitDefaultForUser(){
        final Measure.Type[] typeChangeable = Measure.Type.getMeasureChangeable();
        final LinkedHashMap<Measure.Type, Measure.Unit> map = new LinkedHashMap<>();
        for (Measure.Type type : typeChangeable){
            final String valueDefault = DefaultPreferencesUser.getUnitDefault(MeasureUnitActivity.this, type.toString().toLowerCase());
            map.put(type, type.getValueOfMeasureUnitDefault(valueDefault));
        }
        return map;
    }

    @Override
    protected void listenerAction() {
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        // TODO: 11/17/2019 MIGLIORARE
        final String measure = ((TextView)((RelativeLayout)group.getParent()).getChildAt(0)).getText().toString();
        final Measure.Type type = (Measure.Type) EnumUtilities.getEnumFromString(Measure.Type.class, MeasureUnitActivity.this, measure);

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
                Log.e(TAG, "Filter = "+ filter +", Value = "+unit);
                try {
                    final String  id_user = DefaultPreferencesUser.getIdUserLogged(this);
                    JSONObject bodyJson = new JSONObject();
                    bodyJson.put(HttpRequest.Constant.ID_USER, id_user)
                            .put(HttpRequest.Constant.FILTER, filter)
                            .put(HttpRequest.Constant.VALUE, unit);

                    HttpRequest.requestUpdateSetting(this, bodyJson, this);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InternetNoAvailableException e) {
                    e.alert();
                }
            }

        }

    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            DefaultPreferencesUser.setUnitMeasure(this, filter, unit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
