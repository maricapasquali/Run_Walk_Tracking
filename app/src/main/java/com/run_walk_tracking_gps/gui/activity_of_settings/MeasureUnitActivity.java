package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.gui.components.adapter.listview.MeasureAdapter;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.service.NetworkServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.stream.Stream;

public class MeasureUnitActivity extends CommonActivity implements MeasureAdapter.OnCheckNewMeasureListener{

    private final static String TAG = MeasureUnitActivity.class.getName();

    @Override
    protected void init(Bundle savedInstanceState) {
        Log.d(TAG, "init");
        setContentView(R.layout.activity_measure_unit);
        getSupportActionBar().setTitle(R.string.measure_unit);

        final ListView measure_unit = findViewById(R.id.measures_units);

        try {
            measure_unit.setAdapter(new MeasureAdapter(this,this,getUnitDefaultForUser()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private LinkedHashMap<Measure.Type, Measure.Unit> getUnitDefaultForUser() throws JSONException {
        final Measure.Type[] typeChangeable = Measure.Type.getMeasureChangeable();
        final LinkedHashMap<Measure.Type, Measure.Unit> map = new LinkedHashMap<>();
        final JSONObject measure = DaoFactory.getInstance(MeasureUnitActivity.this).getSettingDao().getUnitMeasureDefault();
        Stream.of(typeChangeable).forEach(type -> {
            try {
                map.put(type, Measure.Unit.valueOf(measure.getString(type.toString().toLowerCase())));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        return map;
    }

    @Override
    protected void listenerAction() {
    }

    @Override
    public void onCheckNewMeasure(Measure.Type filter, Measure.Unit unit) {
        Log.d(TAG, "Filter = " + filter + ", value = " + unit);
        if(DaoFactory.getInstance(this).getSettingDao().update(filter, unit.toString())){
            Preferences.Session.update(this);
            try {
                JSONObject data = new JSONObject().put(NetworkHelper.Constant.VALUE, unit.toString());
                String filterNetwork = null;
                switch (filter){
                    case DISTANCE:
                        filterNetwork = NetworkHelper.Constant.UNIT_DISTANCE;
                        break;
                    case WEIGHT:
                        filterNetwork = NetworkHelper.Constant.UNIT_WEIGHT;
                        break;
                    case HEIGHT:
                        filterNetwork = NetworkHelper.Constant.UNIT_HEIGHT;
                        break;
                }
                NetworkServiceHandler.getInstance(this, NetworkHelper.Constant.UPDATE, filterNetwork, data.toString()).startService();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
