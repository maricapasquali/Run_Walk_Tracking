package com.run_walk_tracking_gps.connectionserver;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.util.Log;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.run_walk_tracking_gps.gui.dialog.RequestDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class HttpRequest {

    private static final String TAG = HttpRequest.class.getName();

    private static final String BODY_JSON_NOT_NULL = "bodyJson not null";
    public static final String ERROR = "Error";
    private static final String ip = "192.168.1.132"; //"10.10.10.3";//
    private static final String SERVER = "http://"+ip+"/run_walk_tracking/";

    private static final String ACCOUNT = SERVER + "account/";
    private static final String WORKOUT = SERVER + "workout/";
    private static final String STATISTICS = SERVER + "statistics/";
    private static final String SETTINGS = SERVER + "settings/";

    // ACCOUNT
    private static final String SIGN_UP = ACCOUNT + "signup.php";
    private static final String SIGN_IN = ACCOUNT + "signin.php";
    private static final String FIRST_SIGN_IN = ACCOUNT + "first_signin.php";
    private static final String USER = ACCOUNT + "profile.php?"+  FieldDataBase.ID_USER.toName()+"=";
    private static final String UPDATE_USER_INFO = ACCOUNT + "update_profile.php";
    private static final String DELETE_USER = ACCOUNT + "delete_account.php";
    private static final String UPDATE_PASSWORD = ACCOUNT + "";

    private static final String FORGOT_PASSWORD = SERVER + "request_change_password.php";

    // SETTINGS
    private static final String UPDATE_SETTING = SETTINGS + "update_setting.php";

    // WORKOUTS
    private static final String ALL_WORKOUTS = WORKOUT + "all_workouts.php?"+  FieldDataBase.ID_USER.toName()+"=";
    private static final String NEW_WORKOUT = WORKOUT + "new_workout.php";
    private static final String UPDATE_WORKOUT = WORKOUT + "update_workout.php";
    private static final String DELETE_WORKOUT = WORKOUT + "delete_workout.php";

    // STATISTICS
    private static final String ALL_STATISTICS = STATISTICS + "statistics.php";
    private static final String NEW_WEIGHT = STATISTICS + "new_weight.php";
    private static final String UPDATE_WEIGHT = STATISTICS + "update_weight.php";
    private static final String DELETE_WEIGHT = STATISTICS + "delete_weight.php";

    public static boolean someError(JSONObject response){
        return Stream.of(response.keys()).anyMatch(i -> i.next().equals(ERROR));
    }

    // ACCOUNT
    public static boolean requestSignUp(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException{

        if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
        final List<String> fieldRequired =  FieldDataBase.fieldRequiredForSignUp();
        checkNotRequiredField(bodyJson::keys, s -> !fieldRequired.contains(s) && !s.equals(FieldDataBase.IMG_ENCODE.toName()));
        checkRequiredField(bodyJson::keys, (fieldSubmit) -> fieldRequired.stream().filter(f -> !fieldSubmit.contains(f)).toArray());
        return HttpRequest.requestJsonPostToServerVolley(context, SIGN_UP, bodyJson,responseJsonListener);
    }

    public static boolean requestSignIn(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException{
        check(bodyJson,  FieldDataBase.fieldRequiredForSignIn());
        return HttpRequest.requestJsonPostToServerVolley(context, SIGN_IN, bodyJson,responseJsonListener);
    }

    public static boolean requestFirstSignIn(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException{
        check(bodyJson,  FieldDataBase.fieldRequiredForFirstSignIn());
        return HttpRequest.requestJsonPostToServerVolley(context, FIRST_SIGN_IN, bodyJson,responseJsonListener);
    }

    public static boolean requestUserInformation(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, JSONException {

        check(bodyJson,  FieldDataBase.fieldRequiredForUserInformation());

        final int id_user = (Integer) bodyJson.get(FieldDataBase.ID_USER.toName());
        return HttpRequest.requestJsonGetToServerVolley(context, USER + id_user, bodyJson,responseJsonListener);
    }

    public static boolean requestUpdateUserInformation(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException {
        final List<String> fieldSupported =  FieldDataBase.fieldSupportedForUpdateUserInformation();
        if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
        checkNotRequiredField(bodyJson::keys, s -> !fieldSupported.contains(s));
        return HttpRequest.requestJsonPostToServerVolley(context, UPDATE_USER_INFO, bodyJson,responseJsonListener);
    }

    public static boolean requestForgotPassword(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException{
        check(bodyJson, FieldDataBase.fieldRequiredForForgotPassword());
        return HttpRequest.requestJsonPostToServerVolley(context, FORGOT_PASSWORD, bodyJson,responseJsonListener);
    }

    // NON ANCORA IMPLEMENTATO IL PHP
    public static boolean requestUpdatePassword(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException{
        check(bodyJson,  FieldDataBase.fieldRequiredForUpdatePassword());
        return HttpRequest.requestJsonPostToServerVolley(context, UPDATE_PASSWORD, bodyJson,responseJsonListener);
    }

    public static boolean requestDeleteUser(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException {
        check(bodyJson, FieldDataBase.fieldRequiredForDeleteUser());
        return HttpRequest.requestJsonPostToServerVolley(context, DELETE_USER, bodyJson,responseJsonListener);
    }

    // SETTINGS
    public static boolean requestUpdateSetting(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
        throws NullPointerException, IllegalArgumentException {
            check(bodyJson,  FieldDataBase.fieldRequiredForUpdateSetting());
            return HttpRequest.requestJsonPostToServerVolley(context, UPDATE_SETTING, bodyJson,responseJsonListener);
    }

    // WORKOUTS
    public static boolean requestWorkouts(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, JSONException {
        check(bodyJson,  FieldDataBase.fieldRequiredForAllWorkouts());
        final int id_user = (Integer) bodyJson.get( FieldDataBase.ID_USER.toName());
        return HttpRequest.requestJsonGetToServerVolley(context, ALL_WORKOUTS + id_user, bodyJson,responseJsonListener);
    }

    public static boolean requestNewWorkout(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException {
        if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
        final List<String> fieldSupported =  FieldDataBase.fieldSupportedForNewWorkout();
        checkNotRequiredField(bodyJson::keys, s -> !fieldSupported.contains(s));
        return HttpRequest.requestJsonPostToServerVolley(context, NEW_WORKOUT, bodyJson,responseJsonListener);
    }

    public static boolean requestUpdateWorkout(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException {
        if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
        final List<String> fieldSupported =  FieldDataBase.fieldSupportedForUpdateWorkout();
        checkNotRequiredField(bodyJson::keys, s -> !fieldSupported.contains(s));
        return HttpRequest.requestJsonPostToServerVolley(context, UPDATE_WORKOUT, bodyJson,responseJsonListener);
    }

    public static boolean requestDeleteWorkout(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException {
        check(bodyJson,  FieldDataBase.fieldRequiredForDeleteWorkout());
        return HttpRequest.requestJsonPostToServerVolley(context, DELETE_WORKOUT, bodyJson,responseJsonListener);
    }

    // STATISTICS
    public static boolean requestStatistics(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, JSONException {
        check(bodyJson,  FieldDataBase.fieldRequiredForAllStatistics());
        final int id_user = (Integer) bodyJson.get( FieldDataBase.ID_USER.toName());
        final String filter = (String) bodyJson.get( FieldDataBase.FILTER.toName());
        return HttpRequest.requestJsonGetToServerVolley(context,
                ALL_STATISTICS + "?" +  FieldDataBase.ID_USER.toName() + "=" + id_user + "&" +  FieldDataBase.FILTER.toName() + "=" + filter,
                bodyJson,responseJsonListener);
    }

    public static boolean requestNewWeight(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException {
        check(bodyJson,  FieldDataBase.fieldRequiredForNewWeight());
        return HttpRequest.requestJsonPostToServerVolley(context, NEW_WEIGHT, bodyJson, responseJsonListener);
    }

    public static boolean requestUpdateWeight(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException {
        if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
        final List<String> fieldSupported =  FieldDataBase.fieldSupportedForUpdateWeight();
        checkNotRequiredField(bodyJson::keys, s -> !fieldSupported.contains(s));
        return HttpRequest.requestJsonPostToServerVolley(context, UPDATE_WEIGHT, bodyJson,responseJsonListener);
    }

    public static boolean requestDeleteWeight(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException {
        check(bodyJson,  FieldDataBase.fieldRequiredForDeleteWeight());
        return HttpRequest.requestJsonPostToServerVolley(context, DELETE_WEIGHT, bodyJson,responseJsonListener);
    }


// CHECKS -----------------------------
    private static void check(JSONObject bodyJson, List<String> fieldRequired) throws NullPointerException, IllegalArgumentException{
        if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
        checkNotRequiredField(bodyJson::keys, s -> !fieldRequired.contains(s));
        checkRequiredField(bodyJson::keys, (fieldSubmit) -> fieldRequired.stream().filter(f-> !fieldSubmit.contains(f)).toArray());
    }

    private static void checkNotRequiredField(Iterable<String> iterable, Function<String, Boolean> fieldRequired)
            throws IllegalArgumentException{
        Object[] notRequired = StreamSupport.stream(iterable.spliterator(), false)
                .filter(k -> fieldRequired.apply(k)).toArray();
        if(notRequired.length>0) throw new IllegalArgumentException("NOT required : " +  Arrays.toString(notRequired));
    }

    private static void checkRequiredField(Iterable<String> iterable, Function<List<String>, Object[]> fieldRequired) throws
            IllegalArgumentException{
        Object[] missing = fieldRequired.apply(StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList()));
        if(missing.length>0) throw new IllegalArgumentException("Missing : " +  Arrays.toString(missing));
    }

// REQUEST UTILITIES -----------------------------

    private static boolean isNetWorkAvailable(Context context){
        final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork!=null && activeNetwork.isConnected();
    }

    private static boolean requestJsonGetToServerVolley(final Context context, final String url, final JSONObject bodyJson,
                                                       Response.Listener<JSONObject> listenerResponse){

        return requestJsonToServerVolley(context, url, Request.Method.GET, bodyJson, listenerResponse);
    }

    private static boolean requestJsonPostToServerVolley(final Context context, final String url, final JSONObject bodyJson,
                                                        Response.Listener<JSONObject> listenerResponse){

        return requestJsonToServerVolley(context, url, Request.Method.POST, bodyJson, listenerResponse);
    }


    private static boolean requestJsonToServerVolley(final Context context, final String url, final int method, final JSONObject bodyJson,
                                                     Response.Listener<JSONObject> listenerResponse){
        final AtomicBoolean errRequest = new AtomicBoolean(false);
        if(!isNetWorkAvailable(context)) return false;

        final RequestQueue queue = Volley.newRequestQueue(context);
        final JsonObjectRequest stringRequest = new JsonObjectRequest(method, url, bodyJson,
                listenerResponse,
                error -> {
                    Log.e(TAG, error.toString());
                    Toast.makeText(context, errRequest.toString(), Toast.LENGTH_LONG).show();
                    errRequest.set(true);
                });

        queue.add(stringRequest);
        RequestDialog progressDialog = RequestDialog.create(context);
        progressDialog.show();
        queue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
            if (progressDialog.isShowing()) progressDialog.dismiss();
        });
        return !errRequest.get();
    }
/*
    public static boolean requestStringToServerVolley(final Context context, final String url, final int method, final JSONObject jsonBody,
                                                Response.Listener<String> listener){

        final AtomicBoolean errRequest = new AtomicBoolean(false);
        if(!isNetWorkAvailable(context)) return false;
        String mRequestBody = jsonBody.toString();

        final StringRequest stringRequest = new StringRequest(method, url, listener, error -> {
            Log.e(TAG, error.toString());
            Toast.makeText(context, errRequest.toString(), Toast.LENGTH_LONG).show();
            errRequest.set(true);
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",mRequestBody, "utf-8");
                    return null;
                }
            }
        };

        final RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
        return!errRequest.get();
    }

*/
}
