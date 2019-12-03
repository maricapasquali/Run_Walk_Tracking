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
import com.run_walk_tracking_gps.connectionserver.DefaultPreferencesUser;
import com.run_walk_tracking_gps.gui.components.adapter.listview.MeasureAdapter;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.utilities.EnumUtilities;

import org.json.JSONException;
import org.json.JSONObject;

public class MeasureUnitActivity extends CommonActivity implements RadioGroup.OnCheckedChangeListener,  Response.Listener<JSONObject>{

    private final static String TAG = MeasureUnitActivity.class.getName();

    private String filter;
    private String value;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_measure_unit);
        getSupportActionBar().setTitle(R.string.measure_unit);

        final ListView measure_unit = findViewById(R.id.measures_units);

        measure_unit.setAdapter(new MeasureAdapter(this,this, Measure.Type.getUnitDefaultForUser(this)));
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
            Measure.Unit unit;
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
                    final String  id_user = DefaultPreferencesUser.getIdUserLogged(this);
                    JSONObject bodyJson = new JSONObject();
                    bodyJson.put(HttpRequest.Constant.ID_USER, id_user)
                            .put(HttpRequest.Constant.FILTER, filter)
                            .put(HttpRequest.Constant.VALUE, value);

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
                 DefaultPreferencesUser.setUnitMeasure(this, filter, value);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
