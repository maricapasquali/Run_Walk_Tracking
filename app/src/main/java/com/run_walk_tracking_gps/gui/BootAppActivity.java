package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


import com.google.android.material.button.MaterialButton;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.utilities.ImageFileHelper;

import androidx.annotation.RequiresApi;

public class BootAppActivity extends CommonActivity {

    private MaterialButton registration;
    private MaterialButton login;

    @Override
    protected void init(Bundle savedInstanceState) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        ImageFileHelper.create(this).deleteTmpDir();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
