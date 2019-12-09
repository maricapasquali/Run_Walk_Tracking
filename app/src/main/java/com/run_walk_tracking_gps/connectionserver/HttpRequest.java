package com.run_walk_tracking_gps.connectionserver;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.exception.SomeErrorHttpException;
import com.run_walk_tracking_gps.gui.components.dialog.RequestDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.transform.ErrorListener;

public class HttpRequest {

    public static class Constant{
        private static final String ERROR = "Error";

        public static final String RESET = "reset";
        public static final String IMEI = "imei";
        public static final String FIRST_LOGIN ="first_login";
        public static final String TOKEN = "token";
        public static final String ID_USER = "id_user";
        public static final String NAME = "name";
        public static final String LAST_NAME = "last_name";
        public static final String BIRTH_DATE = "birth_date";
        public static final String EMAIL = "email";
        public static final String CITY = "city";
        public static final String PHONE = "phone";
        public static final String GENDER = "gender";
        public static final String HEIGHT = "height";
        public static final String IMG_ENCODE = "img_encode";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String SPORT = "sport";
        public static final String TARGET = "target";
        public static final String WEIGHT = "weight";
        public static final String ID_WEIGHT = "id_weight";
        public static final String ID_WORKOUT = "id_workout";
        public static final String DURATION = "duration";
        public static final String MAP_ROUTE = "map_route";
        public static final String DISTANCE  = "distance";
        public static final String CALORIES = "calories";
        public static final String DATE = "date";
        public static final String VALUE ="value";
        public static final String FILTER = "filter";

        public static final String WEIGHTS = "weights";
        public static final String WORKOUTS = "workouts";
        public static final String REQUEST_PASSWORD_FORGOT_SEND = "request_password_forgot_send";

        public static final String UPDATE = "update";
        public static final String DELETE = "delete";

        public static final String USER ="user";
        public static final String APP ="app";
        public static final String SETTINGS = "settings";
        public static final String UNIT_MEASURE ="unit_measure";

        public static final String ENERGY = "energy";


        private static List<String> fieldRequiredForReset(){
            return Arrays.asList(IMEI);
        }
        //ACCOUNT
        private static List<String> fieldRequiredForSignUp(){
            return Arrays.asList(NAME, LAST_NAME, GENDER, BIRTH_DATE, EMAIL,
                            CITY,PHONE,HEIGHT, TARGET, WEIGHT,
                            USERNAME, PASSWORD);
        }

        private static List<String> fieldRequiredForSignIn(){
            return Arrays.asList(USERNAME, PASSWORD, IMEI);
        }

        private static List<String> fieldRequiredForFirstSignIn(){
            return Arrays.asList(ID_USER, TOKEN, IMEI);
        }

        private static List<String> fieldRequiredForImgProfile() {
            return Arrays.asList(ID_USER, IMEI);
        }

        private static List<String> fieldRequiredForUpdatePassword(){
            return Arrays.asList(ID_USER,PASSWORD);
        }

        private static List<String> fieldRequiredForForgotPassword(){
            return Collections.singletonList(EMAIL);
        }

        private static List<String> fieldSupportedForUpdateUserInformation(){
            return Arrays.asList(ID_USER,  IMG_ENCODE,  NAME,  LAST_NAME,  GENDER,
                    BIRTH_DATE,  EMAIL,  CITY,  PHONE, HEIGHT);
        }

        private static List<String> fieldRequiredForDeleteUser(){
            return Collections.singletonList(ID_USER);
        }

        // SETTINGS
        private static  List<String> fieldRequiredForUpdateSetting(){
            return Arrays.asList(ID_USER,  FILTER,  VALUE);
        }

        // WORKOUTS

        private static List<String> fieldSupportedForNewWorkout(){
            return Arrays.asList(ID_USER, DATE, SPORT, DURATION, MAP_ROUTE, DISTANCE, CALORIES);
        }

        private static List<String> fieldSupportedForUpdateWorkout(){
            return Arrays.asList(ID_WORKOUT,  DATE,  SPORT,  DURATION,  MAP_ROUTE,
                    DISTANCE,  CALORIES);
        }

        private static List<String> fieldRequiredForDeleteWorkout(){
            return Collections.singletonList(ID_WORKOUT);
        }

        // STATISTICS
        private static  List<String> fieldRequiredForNewWeight(){
            return Arrays.asList(ID_USER,  DATE,  VALUE);
        }

        private static List<String> fieldSupportedForUpdateWeight(){
            return Arrays.asList(ID_WEIGHT,  DATE,  VALUE);
        }

        private static List<String> fieldRequiredForDeleteWeight() {
            return Collections.singletonList(ID_WEIGHT);
        }
    }

    private static final String TAG = HttpRequest.class.getName();
    private static final String BODY_JSON_NOT_NULL = "bodyJson not null";

    private static final String SERVER = "https://runwalktracking.000webhostapp.com/";

    private static final String ACCOUNT = SERVER + "account/";
    private static final String WORKOUT = SERVER + "workout/";
    private static final String STATISTICS = SERVER + "statistics/";
    private static final String SETTINGS = SERVER + "settings/";


    private static final String DATA_AFTER_ACCESS = ACCOUNT + "data_after_access.php";

    private static final String RESET = SERVER +"reset.php";

    // ACCOUNT
    private static final String SIGN_UP = ACCOUNT + "signup.php";
    private static final String FIRST_SIGN_IN = ACCOUNT + "first_signin.php";
    private static final String SIGN_IN = ACCOUNT + "signin.php";

    private static final String UPDATE_USER_INFO = ACCOUNT + "update_profile.php";
    private static final String DELETE_USER = ACCOUNT + "delete_account.php";
    private static final String UPDATE_PASSWORD = ACCOUNT + "change_password.php";

    private static final String FORGOT_PASSWORD = SERVER + "request_change_password.php";

    // SETTINGS
    private static final String UPDATE_SETTING = SETTINGS + "update_setting.php";

    // WORKOUTS
    private static final String NEW_WORKOUT = WORKOUT + "new_workout.php";
    private static final String UPDATE_WORKOUT = WORKOUT + "update_workout.php";
    private static final String DELETE_WORKOUT = WORKOUT + "delete_workout.php";

    // STATISTICS
    private static final String NEW_WEIGHT = STATISTICS + "new_weight.php";
    private static final String UPDATE_WEIGHT = STATISTICS + "update_weight.php";
    private static final String DELETE_WEIGHT = STATISTICS + "delete_weight.php";


    private static RequestQueue queue;

    public static boolean requestReset(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws InternetNoAvailableException {
        check(bodyJson, HttpRequest.Constant.fieldRequiredForReset());
        return HttpRequest.requestJsonPostToServerVolley(context, RESET, bodyJson,responseJsonListener);
    }

    // ACCOUNT
    public static boolean requestSignUp(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {

        if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
        final List<String> fieldRequired =  HttpRequest.Constant.fieldRequiredForSignUp();
        checkNotRequiredField(bodyJson::keys, s -> !fieldRequired.contains(s) && !s.equals(HttpRequest.Constant.IMG_ENCODE));
        checkRequiredField(bodyJson::keys, (fieldSubmit) -> fieldRequired.stream().filter(f -> !fieldSubmit.contains(f)).toArray());
        return HttpRequest.requestJsonPostToServerVolley(context, SIGN_UP, bodyJson,responseJsonListener);
    }

    public static boolean requestSignIn(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        check(bodyJson,  HttpRequest.Constant.fieldRequiredForSignIn());
        return HttpRequest.requestJsonPostToServerVolley(context, SIGN_IN, bodyJson,responseJsonListener);
    }

    public static boolean requestFirstSignIn(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        check(bodyJson,  HttpRequest.Constant.fieldRequiredForFirstSignIn());
        return HttpRequest.requestJsonPostToServerVolley(context, FIRST_SIGN_IN, bodyJson,responseJsonListener);
    }


    public static boolean requestDataAfterAccess(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        check(bodyJson,  HttpRequest.Constant.fieldRequiredForImgProfile());
        return HttpRequest.requestJsonToServerVolleyWithoutProgressBar(context, DATA_AFTER_ACCESS, Request.Method.POST, bodyJson,responseJsonListener);
    }

    public static boolean requestDelayedUpdateUserInformation(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener,
                                                              RequestDialog dialog)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {

        final List<String> fieldSupported =  HttpRequest.Constant.fieldSupportedForUpdateUserInformation();
        if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
        checkNotRequiredField(bodyJson::keys, s -> !fieldSupported.contains(s));

        final AtomicBoolean errRequest = new AtomicBoolean(false);
        if(!isNetWorkAvailable(context)) throw new InternetNoAvailableException(context);

        final RequestQueue queue = createRequest(context, Request.Method.POST, UPDATE_USER_INFO,  bodyJson,  responseJsonListener);
        queue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
            if (dialog.isShowing()) dialog.dismiss();
        });

        return !errRequest.get();
    }

    public static boolean requestForgotPassword(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        check(bodyJson, HttpRequest.Constant.fieldRequiredForForgotPassword());
        return HttpRequest.requestJsonPostToServerVolley(context, FORGOT_PASSWORD, bodyJson,responseJsonListener);
    }

    public static boolean requestUpdatePassword(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        check(bodyJson,  HttpRequest.Constant.fieldRequiredForUpdatePassword());
        return HttpRequest.requestJsonPostToServerVolley(context, UPDATE_PASSWORD, bodyJson,responseJsonListener);
    }

    public static boolean requestDeleteUser(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        check(bodyJson, HttpRequest.Constant.fieldRequiredForDeleteUser());
        return HttpRequest.requestJsonPostToServerVolley(context, DELETE_USER, bodyJson,responseJsonListener);
    }

    // SETTINGS
    public static boolean requestUpdateSetting(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
            check(bodyJson,  HttpRequest.Constant.fieldRequiredForUpdateSetting());
            return HttpRequest.requestJsonPostToServerVolley(context, UPDATE_SETTING, bodyJson,responseJsonListener);
    }

    // WORKOUTS
    public static boolean requestNewWorkout(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
        final List<String> fieldSupported =  HttpRequest.Constant.fieldSupportedForNewWorkout();
        checkNotRequiredField(bodyJson::keys, s -> !fieldSupported.contains(s));
        return HttpRequest.requestJsonPostToServerVolley(context, NEW_WORKOUT, bodyJson,responseJsonListener);
    }

    public static boolean requestUpdateWorkout(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
        final List<String> fieldSupported =  HttpRequest.Constant.fieldSupportedForUpdateWorkout();
        checkNotRequiredField(bodyJson::keys, s -> !fieldSupported.contains(s));
        return HttpRequest.requestJsonPostToServerVolley(context, UPDATE_WORKOUT, bodyJson,responseJsonListener);
    }

    public static boolean requestDeleteWorkout(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        check(bodyJson,  HttpRequest.Constant.fieldRequiredForDeleteWorkout());
        return HttpRequest.requestJsonPostToServerVolley(context, DELETE_WORKOUT, bodyJson,responseJsonListener);
    }

    // STATISTICS

    public static boolean requestNewWeight(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        check(bodyJson,  HttpRequest.Constant.fieldRequiredForNewWeight());
        return HttpRequest.requestJsonPostToServerVolley(context, NEW_WEIGHT, bodyJson, responseJsonListener);
    }

    public static boolean requestUpdateWeight(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
        final List<String> fieldSupported =  HttpRequest.Constant.fieldSupportedForUpdateWeight();
        checkNotRequiredField(bodyJson::keys, s -> !fieldSupported.contains(s));
        return HttpRequest.requestJsonPostToServerVolley(context, UPDATE_WEIGHT, bodyJson,responseJsonListener);
    }

    public static boolean requestDeleteWeight(Activity context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener)
            throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
        check(bodyJson,  HttpRequest.Constant.fieldRequiredForDeleteWeight());
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

    private static void checkRequiredField(Iterable<String> iterable, Function<List<String>, Object[]> fieldRequired)
            throws IllegalArgumentException{
        Object[] missing = fieldRequired.apply(StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList()));
        if(missing.length>0) throw new IllegalArgumentException("Missing : " +  Arrays.toString(missing));
    }

// REQUEST UTILITIES -----------------------------

    private static boolean isNetWorkAvailable(Context context){
        final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork!=null && activeNetwork.isConnected();
    }

    private static boolean requestJsonPostToServerVolley(final Activity context, final String url, final JSONObject bodyJson,
                                                         final Response.Listener<JSONObject> listenerResponse)
            throws InternetNoAvailableException {

        return requestJsonToServerVolleyWithProgressBar(context, url, Request.Method.POST, bodyJson, listenerResponse);
    }

    private static boolean requestJsonToServerVolleyWithoutProgressBar(final Activity context, final String url, final int method,
                                                                       final JSONObject bodyJson,
                                                                       final Response.Listener<JSONObject> listenerResponse)
            throws InternetNoAvailableException {
        return  requestJsonToServerVolley(context, url, method, bodyJson,listenerResponse, false);
    }

    private static boolean requestJsonToServerVolleyWithProgressBar(final Activity context, final String url, final int method, final JSONObject bodyJson,
                                                                    final Response.Listener<JSONObject> listenerResponse)
            throws InternetNoAvailableException {
        return  requestJsonToServerVolley(context, url, method, bodyJson,listenerResponse, true);
    }

    private static boolean requestJsonToServerVolley(final Activity context, final String url, final int method, final JSONObject bodyJson,
                                                     final Response.Listener<JSONObject> listenerResponse, final boolean withProgressBar)
            throws InternetNoAvailableException {
        if(!isNetWorkAvailable(context)) throw new InternetNoAvailableException(context);

        final RequestQueue queue = createRequest(context, method, url,  bodyJson,  listenerResponse);
        if(withProgressBar){
            RequestDialog progressDialog = RequestDialog.create(context);
            progressDialog.show();
            queue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            });
        }
        return true;
    }



    public static void cancelAllRequestPending(Object tag){
        if(tag!=null){
            queue.cancelAll(tag);
            Log.d(TAG, "Richiesta ANNULLATA..");
        }
    }

    private static RequestQueue createRequest(Context context, int method, String url, JSONObject bodyJson,
                                              Response.Listener<JSONObject>  responseJsonListener){

        queue = Volley.newRequestQueue(context);

        final StringRequest jsonRequest = new StringRequest(method, url, response -> {
            Log.d(TAG, "onResponse");
            try {
                JSONObject JSONResponse = new JSONObject(response);

                if(JSONResponse.has(Constant.ERROR))
                    throw new SomeErrorHttpException(context, JSONResponse.getString(Constant.ERROR));

                responseJsonListener.onResponse(JSONResponse);

            } catch (JSONException e) {
                error(context, response, null);
                queue.stop();
            } catch (SomeErrorHttpException e) {
                error(context, e.getMessage(), e);
                // TODO: 12/8/2019 NON LO SO 
                //DefaultPreferencesUser.unSetUserLogged(context);
            }
        }, error -> error(context, error.toString(), null)){
           @Override
           public String getBodyContentType() {
               return "application/json; charset=utf-8";
           }

           @Override
           public byte[] getBody() {
               return bodyJson.toString().getBytes();
           }
        };

        jsonRequest.setTag(bodyJson);
// TODO: 12/5/2019 DA RIGUARDARE
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonRequest);
        return queue;
    }

    private static  void error(Context context, String response, SomeErrorHttpException someErrorHttpException){
        Log.e(TAG, response);
        if(someErrorHttpException==null) new SomeErrorHttpException(context, response).alert();
        else someErrorHttpException.alert();
    }
}
