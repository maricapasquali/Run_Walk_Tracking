package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Bundle;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.service.WorkoutService;
import com.run_walk_tracking_gps.task.CheckSongTask;
import com.run_walk_tracking_gps.utilities.PermissionUtilities;
import com.run_walk_tracking_gps.utilities.ServiceUtilities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private final static String TAG = SplashScreenActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(!PermissionUtilities.hasReadExternalStoragePermission(this)){
            PermissionUtilities.setReadExternalStoragePermission(this);
        }else{
            CheckSongTask.create(this).execute();//startService(new Intent(this, CheckSongService.class));
            goOn();
        }
    }

    private void goOn(){

        if( getIntent().getAction()!=null &&
                ( getIntent().getAction().equals(ActionReceiver.RUNNING_WORKOUT) ||
                        getIntent().getAction().equals(ActionReceiver.STOP_ACTION)))
        {
            startApplicationActivity(getIntent().getAction());

        }else if(ServiceUtilities.isServiceRunning(this, WorkoutService.class)){
            startApplicationActivity(ActionReceiver.RUNNING_WORKOUT);
        }
        else {
            if(Preferences.Session.isLogged(this)) {
                NetworkHelper.HttpRequest.syncInForeground(this);
            } else {
                startActivity(new Intent(SplashScreenActivity.this, BootAppActivity.class));
                finish();
            }
        }
    }

    private void startApplicationActivity(String action){
        startActivity(new Intent(this, ApplicationActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                .setAction(action));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PermissionUtilities.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE: {
                PermissionUtilities.onRequestPermissionsResult(grantResults, new PermissionUtilities.OnPermissionListener() {
                    @Override
                    public void onGranted() {
                        CheckSongTask.create(SplashScreenActivity.this).execute();//startService(new Intent(this, CheckSongService.class));
                    }

                    @Override
                    public void onDenied() {
                    }
                });

                goOn();
            }
            break;
        }
    }
}
