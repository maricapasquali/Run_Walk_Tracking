package com.run_walk_tracking_gps;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.run_walk_tracking_gps.model.DatabaseNameFields;
import com.run_walk_tracking_gps.model.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;


import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpRequestVolleyTest {

    private static final String TAG = HttpRequestVolleyTest.class.getName();

    @Test
    public void requestSignUpVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {
            //bodyJson.put(KEY_ID, VALUE_ID);
            bodyJson.put(DatabaseNameFields.KEY_NAME, "Mario");
            bodyJson.put(DatabaseNameFields.KEY_LAST_NAME, "Rossi");

            HttpRequest.requestSignUp(context, bodyJson, response -> {
                Log.d(TAG, response.toString());
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestSignInVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {
            bodyJson.put(DatabaseNameFields.KEY_USERNAME, "marioRossi$1");
            //bodyJson.put(DatabaseNameFields.KEY_NAME, "Mario");
            //bodyJson.put(DatabaseNameFields.KEY_LAST_NAME, "Rossi");

            HttpRequest.requestSignIn(context, bodyJson, response -> {
                Log.d(TAG, response.toString());
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestUserInfoVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {
            //bodyJson.put(DatabaseNameFields.KEY_ID_USER, 27);
            bodyJson.put(DatabaseNameFields.KEY_NAME, "Mario");

            HttpRequest.requestUserInformation(context, bodyJson, response -> {
                Log.d(TAG, response.toString());
            });

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

}
