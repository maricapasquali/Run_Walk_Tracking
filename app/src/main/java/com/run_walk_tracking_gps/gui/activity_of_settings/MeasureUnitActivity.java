package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.widget.ListView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.adapter.listview.MeasureAdapter;
import com.run_walk_tracking_gps.gui.CommonActivity;

public class MeasureUnitActivity extends CommonActivity {

    private final static String TAG = MeasureUnitActivity.class.getName();

    private ListView measure_unit;

    @Override
    protected void initGui() {
        setContentView(R.layout.activity_measure_unit);
        getSupportActionBar().setTitle(R.string.measure_unit);

        measure_unit = findViewById(R.id.measures_units);
        final MeasureAdapter adapter = new MeasureAdapter(this);

        measure_unit.setAdapter(adapter);

    }

    @Override
    protected void listenerAction() {

    }
}
