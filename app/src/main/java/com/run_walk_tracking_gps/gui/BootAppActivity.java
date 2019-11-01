package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.widget.Button;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;

public class BootAppActivity extends CommonActivity {

    private Button registration;
    private Button login;

    @Override
    protected void init() {
//SharedPreferences sharedPreferences = Preferences.getSharedPreferencesUserLogged(this);
        if(Preferences.isJustUserLogged(this)){
            int id_user = Integer.valueOf(Preferences.getIdUserLogged(this));
            // TODO: 10/30/2019 REQUEST SETTINGS (NEL CASO LE AVESSI MODIFICATE IN UN ALTRO PHONE ) e anche le info del profilo
            Intent intent = new Intent(this, ApplicationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_bootapp);
        registration = findViewById(R.id.recbtn);
        login = findViewById(R.id.loginbtn);
    }

    @Override
    protected void listenerAction() {
        registration.setOnClickListener(v ->{
            startActivity(new Intent(this, SignUpActivity.class));
        });
        login.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}
