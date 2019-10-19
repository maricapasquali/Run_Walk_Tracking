package com.run_walk_tracking_gps.gui;


import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;

public class LoginActivity extends CommonActivity {

    private EditText username;
    private EditText password;
    private TextView forgotPassword;
    private Button login;

    @Override
    protected void initGui() {
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle(getString(R.string.login));

        username = findViewById(R.id.access_username);
        password = findViewById(R.id.access_password);
        forgotPassword = findViewById(R.id.forgot_password);
        login = findViewById(R.id.login);

    }

    @Override
    protected void listenerAction() {

        forgotPassword.setOnClickListener(v ->{
            Toast.makeText(this,getString(R.string.forgot_password) , Toast.LENGTH_LONG).show();
        });

        login.setOnClickListener(v ->{
            Intent intent = new Intent(this, ApplicationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(new Intent(this, ApplicationActivity.class));
            finish();
        });
    }
}
