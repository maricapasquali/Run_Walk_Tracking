package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.os.Bundle;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.CommonActivity;

public class InfoActivity extends CommonActivity {

    @Override
    protected void init() {
        setContentView(R.layout.activity_info);
        getSupportActionBar().setTitle(R.string.info);
    }

    @Override
    protected void listenerAction() {

    }
}
