package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;

public class BootAppActivity extends CommonActivity {

    private Button registration;
    private Button login;

    @Override
    protected void init() {
        setContentView(R.layout.activity_bootapp);
        registration = findViewById(R.id.recbtn);
        login = findViewById(R.id.loginbtn);
    }

    @Override
    protected void listenerAction() {
        registration.setOnClickListener(v -> {
            final Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
        login.setOnClickListener(v -> {
            final Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
