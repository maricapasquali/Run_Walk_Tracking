package com.run_walk_tracking_gps.gui;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import com.run_walk_tracking_gps.R;


public class BootAppActivity extends AttachBaseContextActivity {

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
