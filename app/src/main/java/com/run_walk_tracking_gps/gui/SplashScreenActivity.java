package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.controller.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity implements Response.Listener<JSONObject> {
    private final static String TAG = SplashScreenActivity.class.getName();
    private final static int TIMEOUT = 500;
    private Intent intent;
    private int id_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {

            //SharedPreferences sharedPreferences = Preferences.getSharedPreferencesUserLogged(this);
            if(Preferences.isJustUserLogged(this)){
                id_user = Integer.valueOf(Preferences.getIdUserLogged(this));

                // TODO: 10/30/2019 REQUEST SETTINGS(STORAGE INTO SHARED_PREFERENCE)

                try {
                    // REQUEST IMAGE PROFILE
                    JSONObject bodyJson = new JSONObject().put(FieldDataBase.ID_USER.toName(), id_user);
                    if(!HttpRequest.requestImgProfile(this, bodyJson, this)){
                        Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                intent = new Intent(SplashScreenActivity.this, BootAppActivity.class);
                startActivity(intent);
                finish();
            }

        }, TIMEOUT);
    }


    @Override
    public void onResponse(JSONObject response) {

        try {
            if(HttpRequest.someError(response)){
                Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
            }else {

                String img = response.getString(FieldDataBase.IMG_ENCODE.toName());
                if(img!=null)
                    Preferences.setImageProfile(this, id_user, img);

                Log.e(TAG, img);
                intent = new Intent(this, ApplicationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
