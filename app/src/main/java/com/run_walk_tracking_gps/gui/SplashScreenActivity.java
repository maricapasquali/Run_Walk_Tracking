package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.service.SyncServiceHandler;

public class SplashScreenActivity extends AppCompatActivity {

    private final static String TAG = SplashScreenActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");

        if( getIntent().getAction()!=null &&
            ( getIntent().getAction().equals(ActionReceiver.RUNNING_WORKOUT) ||
              getIntent().getAction().equals(ActionReceiver.STOP_ACTION)))
        {

            startActivity(new Intent(this, ApplicationActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setAction(getIntent().getAction()));

        }else{
            if(Preferences.Session.isLogged(this)) {
                // TODO: 12/26/2019 REQUEST SYNC
                NetworkHelper.HttpRequest.syncInForeground(this);
            } else {
                startActivity(new Intent(SplashScreenActivity.this, BootAppActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
