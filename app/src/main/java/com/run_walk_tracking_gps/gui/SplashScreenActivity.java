package com.run_walk_tracking_gps.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.intent.KeysIntent;
import com.run_walk_tracking_gps.model.builder.StatisticsBuilder;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.model.Workout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.stream.Stream;

public class SplashScreenActivity extends AppCompatActivity implements Response.Listener<JSONObject> {

    private final static String TAG = SplashScreenActivity.class.getName();
    private static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(Preferences.isJustUserLogged(this)){
            int id_user = Integer.valueOf(Preferences.getIdUserLogged(this));

            try {
                // REQUEST IMAGE PROFILE, WEIGHTS AND WORKOUTS
                JSONObject bodyJson = new JSONObject().put(FieldDataBase.ID_USER.toName(), id_user);
                if(!HttpRequest.requestDataAfterAccess(this, bodyJson, this)){
                    Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
            // TODO: 11/26/2019 PER TEST MAPROUTE
            //startActivity(new Intent(this, ApplicationActivity.class));

        }else {
            intent = new Intent(SplashScreenActivity.this, BootAppActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        if(dataAccessResponse(this, response)) finishAffinity();
    }

    public static boolean dataAccessResponse(final Context context, final JSONObject response){
        try {
            if(HttpRequest.someError(response)) {
                Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();
                return false;
            } else {
                Log.e(TAG, response.toString());
                if(Stream.of(response.keys()).anyMatch(i -> i.next().equals(FieldDataBase.FIRST_LOGIN.toName()))
                        && response.getBoolean(FieldDataBase.FIRST_LOGIN.toName())){
                    final Intent intent = new Intent(context, TokenActivity.class);
                    intent.putExtra(FieldDataBase.ID_USER.toName(), response.getInt(FieldDataBase.ID_USER.toName()));
                    context.startActivity(intent);
                } else {
                    Preferences.setImage(context, response);
                    Preferences.setSettings(context, response);
                    intent = new Intent(context, ApplicationActivity.class);
                    final ArrayList<Workout> workouts = Workout.createList(context, (JSONArray)response.get("workouts"));
                    final ArrayList<StatisticsData> statisticsWeight = new ArrayList<>();
                    // TODO: 11/2/2019 MIGLIORARE
                    final JSONArray array = (JSONArray)response.get("weights");
                    for(int i = 0; i < array.length(); i++){
                        JSONObject s = (JSONObject)array.get(i);
                        StatisticsData statisticsData = StatisticsBuilder.createStatisticWeight(context)
                                                                         .setDate(s.getString("date"))
                                                                         .setValue(s.getDouble("weight"))
                                                                         .build();
                        statisticsData.setId(s.getInt(FieldDataBase.ID_WEIGHT.toName()));
                        statisticsWeight.add(statisticsData);
                    }
                    Log.d(TAG, statisticsWeight.toString()); Log.d(TAG, workouts.toString());
                    intent.putExtra(KeysIntent.WORKOUTS, workouts);
                    intent.putExtra(KeysIntent.WEIGHTS, statisticsWeight);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
