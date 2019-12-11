package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.utilities.DeviceUtilities;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationSplashScreenActivity extends AppCompatActivity {
    private static final String TAG = NotificationSplashScreenActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();

        if(getIntent().getAction()!=null &&
                (getIntent().getAction().equals(ActionReceiver.RUNNING_WORKOUT) || getIntent().getAction().equals(ActionReceiver.STOP_ACTION))) {

            if(isTaskRoot()){
                int id_user = Integer.valueOf(DefaultPreferencesUser.getIdUserLogged(this));

                try {
                    // REQUEST IMAGE PROFILE, WEIGHTS AND WORKOUTS
                    final JSONObject bodyJson = new JSONObject().put(HttpRequest.Constant.ID_USER, id_user)
                            .put(HttpRequest.Constant.IMEI, DeviceUtilities.getIdDevice(this));

                    HttpRequest.requestDataAfterAccess(this, bodyJson, response -> {
                        Intent intent = SplashScreenActivity.restoreData(response, this).setAction(getIntent().getAction());

                        startActivity(intent);
                        finish();
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InternetNoAvailableException e) {
                    e.alert();
                }
            }
            else{

                startActivity(new Intent(this, ApplicationActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setAction(getIntent().getAction()));
            }
        }
    }

}
