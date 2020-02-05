package com.run_walk_tracking_gps.connectionserver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.SqlLiteImageDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteSettingsDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteStatisticsDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteUserDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteWorkoutDao;
import com.run_walk_tracking_gps.db.tables.ImageProfileDescriptor;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.gui.ApplicationActivity;
import com.run_walk_tracking_gps.gui.ActivationAccountActivity;
import com.run_walk_tracking_gps.gui.BootAppActivity;
import com.run_walk_tracking_gps.gui.LoginActivity;
import com.run_walk_tracking_gps.gui.SplashScreenActivity;
import com.run_walk_tracking_gps.gui.components.dialog.RequestDialog;
import com.run_walk_tracking_gps.gui.fragments.HomeFragment;
import com.run_walk_tracking_gps.gui.fragments.StatisticsFragment;
import com.run_walk_tracking_gps.gui.fragments.WorkoutsFragment;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.service.SyncServiceHandler;
import com.run_walk_tracking_gps.task.CompressionBitMapTask;
import com.run_walk_tracking_gps.task.DecompressionEncodeImageTask;
import com.run_walk_tracking_gps.utilities.AppUtilities;
import com.run_walk_tracking_gps.utilities.ImageFileHelper;
import com.run_walk_tracking_gps.utilities.SessionUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class NetworkHelper {

    static final String TAG = NetworkHelper.class.getName();

    public interface OnUpdateGuiListener {
        void onChangeStateDB();
    }

    public static class Constant{
        public static final String ERROR = "Error";

        public static final String SIGN_IN ="signin";
        public static final String FORGOT_PASSWORD ="forgot_password";
        public static final String CHANGE_PASSWORD ="change_password";

        public static final String DEVICE = "device";
        public static final String STATE = "state";
        public static final String CODE = "code";
        public static final String DESCRIPTION = "description";

        public static final String SIGN_UP = "sign_up";
        public static final String FIRST_LOGIN ="first_login";
        public static final String LAST_UPDATE = "last_update";
        public static final String TOKEN = "token";
        public static final String SESSION = "session";
        public static final String DATA = "data";

        public static final String DB_EXIST = "db_exist";

        public static final String ID_USER = "id_user";

        public static final String NAME = "name";
        public static final String LAST_NAME = "last_name";
        public static final String BIRTH_DATE = "birth_date";
        public static final String EMAIL = "email";
        public static final String CITY = "city";
        public static final String PHONE = "phone";
        public static final String GENDER = "gender";
        public static final String HEIGHT = "height";

        public static final String IMAGE = "image";
        public static final String IMG_ENCODE = "content";

        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";

        public static final String OLD_PASSWORD = "old_password";
        public static final String NEW_PASSWORD = "new_password";

        public static final String SPORT = "sport";
        public static final String TARGET = "target";
        public static final String WEIGHT = "weight";

        public static final String ID_WEIGHT = "id_weight";

        public static final String WORKOUT = "workout";
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

        public static final String INSERT = "insert";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";

        public static final String USER ="user";
        public static final String SETTINGS = "settings";

        public static final String UNIT_MEASURE ="unit_measure";
        public static final String ENERGY = "energy";

        public static final String UNIT_DISTANCE = "unit_distance";
        public static final String UNIT_WEIGHT = "unit_weight";
        public static final String UNIT_HEIGHT = "unit_height";
        public static final String DELETE_ACCOUNT = "delete_account";

        private static List<String> fieldRequiredForSync(){
            return Arrays.asList(TOKEN,LAST_UPDATE, DB_EXIST, DEVICE);
        }
        //ACCOUNT
        private static List<String> fieldRequiredForSignUp(){
            return Arrays.asList(NAME, LAST_NAME, GENDER, BIRTH_DATE, EMAIL,
                            CITY,PHONE,HEIGHT, TARGET, WEIGHT,
                            USERNAME, PASSWORD);
        }

        private static List<String> fieldRequiredForSignIn(){
            return Arrays.asList(USERNAME, PASSWORD, DEVICE);
        }

        private static List<String> fieldRequiredForFirstSignIn(){
            return Arrays.asList(USERNAME, PASSWORD, TOKEN, DEVICE);
        }

        private static List<String> fieldRequiredForUpdatePassword(){
            return Arrays.asList(TOKEN, USERNAME, OLD_PASSWORD, NEW_PASSWORD);
        }

        private static List<String> fieldRequiredForForgotPassword(){
            return Collections.singletonList(EMAIL);
        }

        private static List<String> fieldSupportedForUpdateUserInformation(){
            return Arrays.asList(IMAGE,  NAME,  LAST_NAME,  GENDER,
                    BIRTH_DATE,  EMAIL,  CITY,  PHONE, HEIGHT);
        }

        // SETTINGS
        private static  List<String> fieldRequiredForUpdateSetting(){
            return Collections.singletonList(VALUE);
        }

        // WORKOUTS

        private static List<String> fieldSupportedForNewWorkout(){
            return Arrays.asList(ID_WORKOUT, DATE, SPORT, DURATION, MAP_ROUTE, DISTANCE, CALORIES);
        }

        private static List<String> fieldSupportedForUpdateWorkout(){
            return Arrays.asList(ID_WORKOUT,  DATE,  SPORT,  DURATION,  MAP_ROUTE, DISTANCE,  CALORIES);
        }

        private static List<String> fieldRequiredForDeleteWorkout(){
            return Collections.singletonList(ID_WORKOUT);
        }

        // WEIGHTS
        private static  List<String> fieldRequiredForNewWeight(){
            return Arrays.asList(ID_WEIGHT, DATE,  VALUE);
        }

        private static List<String> fieldSupportedForUpdateWeight(){
            return Arrays.asList(ID_WEIGHT,  DATE,  VALUE);
        }

        private static List<String> fieldRequiredForDeleteWeight() {
            return Collections.singletonList(ID_WEIGHT);
        }

        public static List<String> fieldRequiredForDownloadImage() {
            return Arrays.asList(TOKEN, IMAGE);
        }
    }

    public static class HttpRequest{

        // TODO: 12/30/2019 UTILIZZARE SYNGLETON (CONTEXT DELL'APPLICAZIONE)
        private static HttpRequest httpRequest;

        private Context context;
        private HttpRequest(Context context){
            this.context = context;
        }

        public static synchronized HttpRequest getInstance(Context context){
            if(httpRequest == null){
                httpRequest = new HttpRequest(context.getApplicationContext());
            }
            return httpRequest;
        }

        private static final String TAG = NetworkHelper.class.getName();
        private static final String BODY_JSON_NOT_NULL = "bodyJson not null";

        private static final String SERVER = "https://runwalktracking.000webhostapp.com/";
        // SERVER LOCALE
        //private static final String SERVER = "http://192.168.1.132/run_walk_tracking_server/";
        //private static final String SERVER = "http://172.20.10.5/run_walk_tracking_server/";

        private static final String FORGOT_PASSWORD = SERVER + "request_change_password.php";

        private static final String INSERT = SERVER + "insert.php";
        private static final String UPDATE = SERVER + "update.php";
        private static final String DELETE = SERVER + "delete.php";

        private static final String UPDATE_ALL = SERVER + "updateAll.php";
        private static final String SYNC = SERVER + "sync.php";

        private static final String CONTINUE_HERE = SERVER + "continueHere.php";

        // ACCOUNT
        private static final String ACCOUNT = SERVER + "account/";
        private static final String DOWNLOAD_IMAGE = ACCOUNT + "download_image.php";

        private static final String SIGN_UP = ACCOUNT + "signup.php";
        private static final String FIRST_SIGN_IN = ACCOUNT + "first_signin.php";
        private static final String SIGN_IN = ACCOUNT + "signin.php";
        private static final String CHANGE_PASSWORD = ACCOUNT + "change_password.php";

        private static RequestQueue queue;

        public boolean requestCUD(String action, String filter, JSONObject data, Response.Listener<JSONObject> responseJsonListener)
                throws NullPointerException, JSONException {
            try {
                if(action!=null){
                    switch (action){
                        case Constant.INSERT:
                            return insert(filter, data, responseJsonListener);
                        case Constant.UPDATE:
                            return update(filter, data, responseJsonListener);
                        case Constant.DELETE:
                            return delete(filter, data, responseJsonListener);
                        default:
                            return false;
                    }
                }
            }catch (InternetNoAvailableException e){
                e.alert();
            }
            return false;
        }

        private boolean insert(String filter, JSONObject data, Response.Listener<JSONObject> responseJsonListener)
                throws NullPointerException, InternetNoAvailableException,JSONException {
            if(data==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
            switch (filter){
                case Constant.WORKOUT:
                    final List<String> fieldSupported =  NetworkHelper.Constant.fieldSupportedForNewWorkout();
                    checkNotRequiredField(data::keys, s -> !fieldSupported.contains(s));
                    break;
                case Constant.WEIGHT:
                    check(data,  NetworkHelper.Constant.fieldRequiredForNewWeight());
                    break;
            }
            return requestJsonPostToServerVolleyWithoutProgressBar(context, INSERT, getBodyRequest(context, filter, data),responseJsonListener);
        }

        private boolean update(String filter, JSONObject data, Response.Listener<JSONObject> responseJsonListener)
                throws NullPointerException, InternetNoAvailableException, JSONException {
            if(data==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
            List<String> fieldSupported =null;
            switch (filter) {
                case Constant.USER:
                    fieldSupported = NetworkHelper.Constant.fieldSupportedForUpdateUserInformation();
                    break;
                case Constant.WORKOUT:
                    fieldSupported =  NetworkHelper.Constant.fieldSupportedForUpdateWorkout();
                    break;
                case Constant.WEIGHT:
                    fieldSupported =  NetworkHelper.Constant.fieldSupportedForUpdateWeight();
                    break;
                default: //SETTINGS (SPORT, TARGET, UNIT_DISTANCE, UNIT_WEIGHT, UNIT_HEIGHT )
                    check(data, Constant.fieldRequiredForUpdateSetting());
                    break;
            }
            if(fieldSupported!=null){
                List<String> finalFieldSupported = fieldSupported;
                checkNotRequiredField(data::keys, s -> !finalFieldSupported.contains(s));
            }

            return requestJsonPostToServerVolleyWithoutProgressBar(context, UPDATE, getBodyRequest(context, filter, data),responseJsonListener);
        }

        private boolean delete(String filter, JSONObject data, Response.Listener<JSONObject> responseJsonListener)
                throws NullPointerException, IllegalArgumentException, InternetNoAvailableException, JSONException{

            switch (filter){
                case Constant.WORKOUT:
                    check(data,  NetworkHelper.Constant.fieldRequiredForDeleteWorkout());
                    break;
                case Constant.WEIGHT:
                    check(data,  NetworkHelper.Constant.fieldRequiredForDeleteWeight());
                    break;
                case Constant.USER:
                    data = new JSONObject();
                    break;
            }
            return requestJsonPostToServerVolleyWithoutProgressBar(context, DELETE, getBodyRequest(context, filter, data),responseJsonListener);
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        public static boolean requestContinueHere(AppCompatActivity activity) {
            try {
                JSONObject s = Preferences.Session.getSession(activity);

                String sessionEncoded = SessionUtilities.getEncodedSession(Preferences.Session.getIdUser(activity),
                                                                           s.getString(Constant.LAST_UPDATE),
                                                                           s.getLong(Constant.LAST_UPDATE),
                                                                           AppUtilities.id(activity));

                final JSONObject session = new JSONObject().put(Constant.SESSION, sessionEncoded);

                return requestJsonPostToServerVolleyWithoutProgressBar(activity, CONTINUE_HERE, session, HttpResponse.onResponseContinueHere(activity));
            }catch (InternetNoAvailableException e){
                e.alert();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return false;
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        private static boolean sync(Context context, OnUpdateGuiListener onUpdateGuiListener) {
            try{
                JSONObject bodyJson = Preferences.Session.getSession(context).put(Constant.DEVICE, AppUtilities.id(context));
                check(bodyJson,  NetworkHelper.Constant.fieldRequiredForSync());
                return requestJsonPostToServerVolleyWithoutProgressBar(context, SYNC, bodyJson, HttpResponse.onResponseSync(context, onUpdateGuiListener));
            }catch (JSONException e) {
                e.printStackTrace();
            }catch (InternetNoAvailableException e){
                e.alert();
            }
            return false;
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        public boolean syncInBackground(OnUpdateGuiListener onUpdateGuiListener) {
            return sync(context, onUpdateGuiListener);
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        public static boolean syncInForeground(AppCompatActivity activity) {
            return sync(activity, HttpResponse.onResponseSyncInForeground(activity));
        }

        private static boolean requestUpdateAll(Context context, JSONObject data, Response.Listener<JSONObject> responseJsonListener)
                throws NullPointerException, InternetNoAvailableException, JSONException, IllegalArgumentException {
            if(data==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
            // TODO: 12/26/2019 CONTROLLI
            JSONObject user = (JSONObject)data.get(Constant.USER);
            if(user.get(Constant.NAME)==null ||
                    user.get(Constant.LAST_NAME)==null ||
                    user.get(Constant.GENDER)==null ||
                    user.get(Constant.BIRTH_DATE)==null ||
                    user.get(Constant.EMAIL)==null ||
                    user.get(Constant.PHONE)==null ||
                    user.get(Constant.CITY)==null ||
                    user.get(Constant.HEIGHT)==null)
                throw new IllegalArgumentException("User : Dati Insufficienti");

            JSONObject settings = (JSONObject)data.get(Constant.SETTINGS);
            if(settings==null ||
                    settings.get(Constant.SPORT)==null ||
                    settings.get(Constant.TARGET)==null ||
                    settings.get(Constant.UNIT_MEASURE)==null ||
                    ((JSONObject)settings.get(Constant.UNIT_MEASURE)).get(Constant.HEIGHT)==null ||
                    ((JSONObject)settings.get(Constant.UNIT_MEASURE)).get(Constant.WEIGHT)==null ||
                    ((JSONObject)settings.get(Constant.UNIT_MEASURE)).get(Constant.DISTANCE)==null)
                throw new IllegalArgumentException("Settings : Dati Insufficienti");

            JSONArray workouts = (JSONArray)data.get(Constant.WORKOUTS);
            for (int i=0; i< workouts.length(); i++){
                JSONObject workout = (JSONObject)workouts.get(i);
                if(workout.get(Constant.ID_WORKOUT)==null ||
                        workout.get(Constant.DATE)==null ||
                        workout.get(Constant.DURATION)==null ||
                        workout.get(Constant.SPORT)==null
                )
                    throw new IllegalArgumentException("Workout : Dati Insufficienti");
            }

            JSONArray weights = (JSONArray)data.get(Constant.WEIGHTS);
            for (int i=0; i< weights.length(); i++){
                JSONObject weight = (JSONObject)weights.get(i);
                if(weight.get(Constant.ID_WEIGHT)==null ||
                        weight.get(Constant.DATE)==null ||
                        weight.get(Constant.VALUE)==null)
                    throw new IllegalArgumentException("Weight : Dati Insufficienti");
            }

            return requestJsonPostToServerVolleyWithoutProgressBar(context, UPDATE_ALL, getBodyRequest(context, data),responseJsonListener);
        }

        // ACCOUNT

        @RequiresApi(api = Build.VERSION_CODES.O)
        public static boolean request(AppCompatActivity activity, String action , JSONObject bodyJson) {
            try {
                switch (action){
                    case Constant.SIGN_UP:
                        return requestSignUp(activity, bodyJson);
                    case Constant.SIGN_IN:
                        //return requestSignIn(context, bodyJson, responseJsonListener);
                        return requestSignIn(activity, bodyJson);
                    case Constant.FIRST_LOGIN:
                        //return requestFirstSignIn(context, bodyJson, responseJsonListener);
                        return requestFirstSignIn(activity, bodyJson);
                    case Constant.FORGOT_PASSWORD:
                        //return requestForgotPassword(context, bodyJson, responseJsonListener);
                        return requestForgotPassword(activity, bodyJson);
                    case Constant.CHANGE_PASSWORD:
                        //return requestChangePassword(context, bodyJson, responseJsonListener);
                        return requestChangePassword(activity, bodyJson);
                    case Constant.DELETE_ACCOUNT:
                        return requestDeleteAccount(activity);
                    default:
                        return false;
                }
            } catch (InternetNoAvailableException e) {
                e.alert();
            } catch (IllegalArgumentException e){
                Log.e(TAG, e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        public static boolean requestSignUp(Activity activity, JSONObject bodyJson)
                throws NullPointerException, IllegalArgumentException, InternetNoAvailableException, JSONException {

            if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
            final List<String> fieldRequired =  NetworkHelper.Constant.fieldRequiredForSignUp();
            checkNotRequiredField(bodyJson::keys, s -> !fieldRequired.contains(s) && !s.equals(Constant.IMAGE));
            checkRequiredField(bodyJson::keys, (fieldSubmit) -> fieldRequired.stream().filter(f -> !fieldSubmit.contains(f)).toArray());

            final JSONObject credential = new JSONObject().put(NetworkHelper.Constant.USERNAME,
                    bodyJson.getString(NetworkHelper.Constant.USERNAME)).put(Constant.PASSWORD, bodyJson.getString(Constant.PASSWORD));

            Response.Listener<JSONObject> responseJsonListener = HttpResponse.onResponseSignUp(activity, credential);

            return requestJsonPostToServerVolleyWithProgressBar(activity, SIGN_UP, bodyJson, responseJsonListener);
        }

        public static boolean requestSignUp(Activity activity, JSONObject bodyJson, RequestDialog progressDialog)
                throws NullPointerException, IllegalArgumentException, InternetNoAvailableException, JSONException {

            if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
            final List<String> fieldRequired =  NetworkHelper.Constant.fieldRequiredForSignUp();
            checkNotRequiredField(bodyJson::keys, s -> !fieldRequired.contains(s) && !s.equals(Constant.IMAGE));
            checkRequiredField(bodyJson::keys, (fieldSubmit) -> fieldRequired.stream().filter(f -> !fieldSubmit.contains(f)).toArray());

            final JSONObject credential = new JSONObject().put(NetworkHelper.Constant.USERNAME,
                    bodyJson.getString(NetworkHelper.Constant.USERNAME)).put(Constant.PASSWORD, bodyJson.getString(Constant.PASSWORD));

            Response.Listener<JSONObject> responseJsonListener = HttpResponse.onResponseSignUp(activity, credential, progressDialog);

            return progressDialog==null ? requestJsonPostToServerVolleyWithProgressBar(activity, SIGN_UP, bodyJson, responseJsonListener) :
                                     requestJsonPostToServerVolleyWithoutProgressBar(activity, SIGN_UP, bodyJson, responseJsonListener);
        }

        private static boolean downloadImageProfile(Context context, JSONObject bodyJson) throws InternetNoAvailableException, JSONException {
            if(bodyJson==null) throw new NullPointerException(BODY_JSON_NOT_NULL);
            bodyJson.put(Constant.TOKEN, Preferences.Session.getSession(context).getString(Constant.TOKEN));
            check(bodyJson, NetworkHelper.Constant.fieldRequiredForDownloadImage());
            return requestJsonPostToServerVolleyWithoutProgressBar(context, DOWNLOAD_IMAGE, bodyJson, HttpResponse.onResponseDownloadImageProfile(context));
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        private static boolean requestSignIn(AppCompatActivity activity, JSONObject bodyJson)
                throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
            check(bodyJson,  NetworkHelper.Constant.fieldRequiredForSignIn());
            //return requestJsonPostToServerVolleyWithProgressBar(context, SIGN_IN, bodyJson,responseJsonListener);
            return requestJsonPostToServerVolleyWithProgressBar(activity, SIGN_IN, bodyJson, HttpResponse.onResponseLogin(activity, bodyJson));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private static boolean requestFirstSignIn(Activity activity, JSONObject bodyJson)
                throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
            check(bodyJson,  NetworkHelper.Constant.fieldRequiredForFirstSignIn());
            //return requestJsonPostToServerVolleyWithProgressBar(context, FIRST_SIGN_IN, bodyJson,responseJsonListener);
            return requestJsonPostToServerVolleyWithProgressBar(activity, FIRST_SIGN_IN, bodyJson,
                    HttpResponse.onResponseFirstLogin(activity));
        }


        private static boolean requestForgotPassword(Activity activity, JSONObject bodyJson)
                throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
            check(bodyJson, NetworkHelper.Constant.fieldRequiredForForgotPassword());
            //return requestJsonPostToServerVolleyWithProgressBar(context, FORGOT_PASSWORD, bodyJson,responseJsonListener);
            return requestJsonPostToServerVolleyWithProgressBar(activity, FORGOT_PASSWORD, bodyJson,
                    HttpResponse.onResponseForgotPassword(activity));
        }


        private static boolean requestChangePassword(Activity activity, JSONObject bodyJson)
                throws NullPointerException, IllegalArgumentException, InternetNoAvailableException {
            check(bodyJson,  NetworkHelper.Constant.fieldRequiredForUpdatePassword());
            //return requestJsonPostToServerVolleyWithProgressBar(context, CHANGE_PASSWORD, bodyJson,responseJsonListener);
            return requestJsonPostToServerVolleyWithProgressBar(activity, CHANGE_PASSWORD, bodyJson, HttpResponse.onResponseChangePassword(activity));
        }


        private static boolean requestDeleteAccount(Activity activity)
                throws NullPointerException, IllegalArgumentException, InternetNoAvailableException, JSONException {
            return requestJsonPostToServerVolleyWithProgressBar(activity, DELETE, getBodyRequest(activity, Constant.USER, new JSONObject()),
                    HttpResponse.onResponseDeleteAccount(activity));
        }


// UTILITIES ---------------------------

        private static JSONObject getBodyRequest(Context context, JSONObject data)
                throws JSONException{
            return Preferences.Session.getSession(context).put(Constant.DATA, data);
        }

        private static JSONObject getBodyRequest(Context context, String filter, JSONObject data)
                throws JSONException{
            return getBodyRequest(context, data).put(Constant.FILTER, filter);
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

        private static boolean requestJsonPostToServerVolleyWithoutProgressBar(final Context context, final String url, final JSONObject bodyJson,
                                                                            final Response.Listener<JSONObject> listenerResponse)
                throws InternetNoAvailableException {

            return requestJsonToServerVolleyWithoutProgressBar(context, url, Request.Method.POST, bodyJson, listenerResponse);
        }

        private static boolean requestJsonPostToServerVolleyWithProgressBar(final Activity context, final String url, final JSONObject bodyJson,
                                                                            final Response.Listener<JSONObject> listenerResponse)
                throws InternetNoAvailableException {

            return requestJsonToServerVolleyWithProgressBar(context, url, Request.Method.POST, bodyJson, listenerResponse);
        }


        // GENERAL
        private static boolean requestJsonToServerVolleyWithoutProgressBar(final Context context, final String url, final int method,
                                                                           final JSONObject bodyJson,
                                                                           final Response.Listener<JSONObject> listenerResponse)
                throws InternetNoAvailableException {

            if(!isNetWorkAvailable(context)) throw new InternetNoAvailableException(context);
            createRequest(context, method, url,  bodyJson,  listenerResponse);
            return true;
        }

        private static boolean requestJsonToServerVolleyWithProgressBar(final Activity context, final String url, final int method,
                                                                        final JSONObject bodyJson,
                                                                        final Response.Listener<JSONObject> listenerResponse)
                throws InternetNoAvailableException {
            if(!isNetWorkAvailable(context)) throw new InternetNoAvailableException(context);

            createRequest(context, method, url,  bodyJson,  listenerResponse);

            RequestDialog progressDialog = RequestDialog.create(context);
            progressDialog.show();
            queue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            });

            return true;
        }


        private static void createRequest(Context context, int method, String url, JSONObject bodyJson,
                                                  Response.Listener<JSONObject>  responseJsonListener){
            queue = Volley.newRequestQueue(context);
            final StringRequest jsonRequest = new CustomRequest(context, method, url, bodyJson, responseJsonListener);
            jsonRequest.setTag(bodyJson);
            // TODO: 12/5/2019 DA RIGUARDARE
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(50000,3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonRequest);
        }

        public static void cancelAllRequestPending(Object tag){
            if(tag!=null){
                queue.cancelAll(tag);
                Log.d(TAG, "Richiesta ANNULLATA..");
            }
        }
    }

    static class HttpResponse {

        public static class Code {

            public static class Error{
                final static int TOKEN_NOT_VALID = 10;
            }

            private static class StateDBServer{
                private final static int CONSISTENT = 0;
                private final static int INCONSISTENT_RECEIVE_DATA = 1;
                private final static int INCONSISTENT_SEND_DATA = 2;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private static Response.Listener<JSONObject> onResponseSync(Context context, OnUpdateGuiListener onUpdateGuiListener){
            return response -> {
                try {
                    final JSONObject state = response.getJSONObject(NetworkHelper.Constant.STATE);

                    switch (state.getInt(NetworkHelper.Constant.CODE))
                    {
                        case Code.StateDBServer.CONSISTENT:
                           if(onUpdateGuiListener!=null) onUpdateGuiListener.onChangeStateDB();
                        break;
                        case Code.StateDBServer.INCONSISTENT_RECEIVE_DATA: // not consistent ( receive data to server )
                        {
                            final JSONObject receivedData = state.getJSONObject(NetworkHelper.Constant.DATA);
                            final JSONObject session = SessionUtilities.getDecodedSession(receivedData.getString(NetworkHelper.Constant.SESSION));

                            Preferences.Session.setSession(context, session);

                            final JSONObject user = receivedData.getJSONObject(NetworkHelper.Constant.USER)
                                                                .put(Constant.ID_USER, session.getInt(Constant.ID_USER));
                            Log.e(NetworkHelper.TAG, "User receive " + user);
                            if(!user.isNull(Constant.IMAGE)){
                                final JSONObject image = user.getJSONObject(Constant.IMAGE);

                                final String name = image.getString(Constant.NAME);
                                //if  file NON  esiste : download image with 'name'
                                if(!ImageFileHelper.create(context).getImage(name).exists()){
                                    Log.e(TAG, "Download Image");
                                    HttpRequest.downloadImageProfile(context, new JSONObject().put(Constant.IMAGE, name));
                                }
                            }

                            SqlLiteUserDao.create(context).insert(user);
                            SqlLiteSettingsDao.create(context).insert(receivedData.getJSONObject(NetworkHelper.Constant.SETTINGS));
                            SqlLiteWorkoutDao.create(context).replaceAll(receivedData.getJSONArray(NetworkHelper.Constant.WORKOUTS));
                            SqlLiteStatisticsDao.SqlLiteWeightDao.create(context).replaceAll(receivedData.getJSONArray(NetworkHelper.Constant.WEIGHTS));
                            // TODO: UPDATE GUI : se immagine presente (<> device -> storage image into internal memory )
                            if(onUpdateGuiListener!=null) onUpdateGuiListener.onChangeStateDB();
                        }
                        break;
                        case Code.StateDBServer.INCONSISTENT_SEND_DATA: // not consistent ( send data to server )
                        {
                            final JSONObject data = new JSONObject();
                            JSONObject userJson = SqlLiteUserDao.create(context).getUser();
                            JSONObject settingsJson = SqlLiteSettingsDao.create(context).getSettings();
                            JSONArray weights = SqlLiteStatisticsDao.create(context).getAll(Measure.Type.WEIGHT);
                            JSONArray workouts = SqlLiteWorkoutDao.create(context).getAll();
                            Log.e(NetworkHelper.TAG, "User send " +  userJson);
                            data.put(NetworkHelper.Constant.USER, userJson);
                            data.put(NetworkHelper.Constant.SETTINGS, settingsJson);
                            data.put(NetworkHelper.Constant.WEIGHTS, weights);
                            data.put(NetworkHelper.Constant.WORKOUTS, workouts);

                            final Bitmap img = userJson.isNull(Constant.IMAGE) ? null :
                                    BitmapFactory.decodeFile(
                                            ImageFileHelper.create(context).getPathImage(userJson.getJSONObject(Constant.IMAGE)
                                                                           .getString(ImageProfileDescriptor.NAME)));
                            if(img==null)
                                // TODO: UPDATE GUI
                                HttpRequest.requestUpdateAll(context, data, responseUpdate-> {
                                    if(onUpdateGuiListener!=null)
                                        onUpdateGuiListener.onChangeStateDB();
                                });

                            else
                            {
                                CompressionBitMapTask.create(context, image_encode -> {
                                    data.getJSONObject(NetworkHelper.Constant.USER).getJSONObject(Constant.IMAGE).put(Constant.IMG_ENCODE, image_encode);
                                    // TODO: UPDATE GUI
                                    HttpRequest.requestUpdateAll(context, data, responseUpdate-> {
                                        if(onUpdateGuiListener!=null)
                                            onUpdateGuiListener.onChangeStateDB();
                                    });
                                }).execute(img);
                            }
                        }
                        break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InternetNoAvailableException e) {
                    e.alert();
                }
            };
        }

        private static Response.Listener<JSONObject> onResponseDownloadImageProfile(Context context) {
            return response -> {
                try {
                    final JSONObject image = response.getJSONObject(Constant.IMAGE);
                    final String name = image.getString(Constant.NAME);
                    final String encode = image.getString(Constant.IMG_ENCODE);
                    DecompressionEncodeImageTask.create(context, name).execute(encode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        private static Response.Listener<JSONObject> onResponseContinueHere(AppCompatActivity activity) {
            return response -> {
                try {
                    JSONObject session = SessionUtilities.getDecodedSession(response.getString(Constant.SESSION));
                    Preferences.Session.setSession(activity, session);
                    Log.d(TAG, "Continue here");
                    HttpRequest.syncInForeground(activity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
        }

        private static Response.Listener<JSONObject> onResponseSignUp(Activity activity, JSONObject credential){
            return response ->{
                try {
                    if(response.has(NetworkHelper.Constant.SIGN_UP) && response.getBoolean(NetworkHelper.Constant.SIGN_UP)){
                        final Intent intent = new Intent(activity, ActivationAccountActivity.class);
                        intent.putExtra(KeysIntent.USERNAME, credential.getString(NetworkHelper.Constant.USERNAME));
                        intent.putExtra(KeysIntent.PASSWORD, credential.getString(NetworkHelper.Constant.PASSWORD));
                        activity.startActivity(intent);
                        activity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
        }

        private static Response.Listener<JSONObject> onResponseSignUp(Activity activity, JSONObject credential, RequestDialog progressDialog){
            return response ->{
                try {
                    Log.e(TAG, response.toString());
                    if(progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();

                    if(response.has(NetworkHelper.Constant.SIGN_UP) && response.getBoolean(NetworkHelper.Constant.SIGN_UP)){
                        final Intent intent = new Intent(activity, ActivationAccountActivity.class);
                        intent.putExtra(KeysIntent.USERNAME, credential.getString(NetworkHelper.Constant.USERNAME));
                        intent.putExtra(KeysIntent.PASSWORD, credential.getString(NetworkHelper.Constant.PASSWORD));
                        activity.startActivity(intent);
                        activity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private static Response.Listener<JSONObject> onResponseFirstLogin(Activity activity) {
            return response -> {
                if(response.has(NetworkHelper.Constant.SESSION) && response.has(NetworkHelper.Constant.DATA)){
                    try {
                        // SESSION TO SHAREDPREFERENCE
                        final JSONObject ss = SessionUtilities.getDecodedSession(response.getString(NetworkHelper.Constant.SESSION));
                        Preferences.Session.setSession(activity, ss);

                        // DATA TO DATABASE
                        JSONObject data = response.getJSONObject(NetworkHelper.Constant.DATA);
                        final JSONObject user = data.getJSONObject(NetworkHelper.Constant.USER)
                                                    .put(Constant.ID_USER, Preferences.Session.getIdUser(activity));

                        if(SqlLiteUserDao.create(activity).insert(user)){
                           // TODO: 1/2/2020 DECOMPRESSIONE IMMAGINE E SALVATAGGIO IN image/
                           if(!user.isNull(NetworkHelper.Constant.IMAGE)){
                               final JSONObject image = user.getJSONObject(NetworkHelper.Constant.IMAGE);
                               final String name = image.getString(ImageProfileDescriptor.NAME);
                               final String encode = image.getString(NetworkHelper.Constant.IMG_ENCODE);
                               DecompressionEncodeImageTask.create(activity, name).execute(encode);
                           }
                        }
                        SqlLiteSettingsDao.create(activity).insert(data.getJSONObject(NetworkHelper.Constant.SETTINGS));
                        SqlLiteStatisticsDao.SqlLiteWeightDao.create(activity)
                                                             .insert(data.getJSONArray(NetworkHelper.Constant.WEIGHTS)
                                                                         .getJSONObject(0));
                        //SyncServiceHandler.create(activity).start();
                        activity.startActivity(new Intent(activity, ApplicationActivity.class));
                        activity.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        private static Response.Listener<JSONObject> onResponseLogin(AppCompatActivity activity, JSONObject credential){
            return response ->{
                try {
                    if(response.has(NetworkHelper.Constant.FIRST_LOGIN) && response.getBoolean(NetworkHelper.Constant.FIRST_LOGIN)){
                        final Intent intent = new Intent(activity, ActivationAccountActivity.class);

                        intent.putExtra(KeysIntent.USERNAME, credential.optString(Constant.USERNAME));
                        intent.putExtra(KeysIntent.PASSWORD, credential.optString(Constant.PASSWORD));
                        activity.startActivity(intent);
                        activity.finish();
                    }else if(response.has(NetworkHelper.Constant.SESSION)){
                        // SESSION TO SHAREDPREFERENCE

                        final JSONObject session = SessionUtilities.getDecodedSession(response.getString(NetworkHelper.Constant.SESSION));
                        Preferences.Session.setSession(activity, session);

                        // REQUEST SYNC
                        NetworkHelper.HttpRequest.syncInForeground(activity);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
        }

        private static Response.Listener<JSONObject> onResponseForgotPassword(Activity activity){
            return response ->{
                try {

                    if(response.getBoolean(NetworkHelper.Constant.REQUEST_PASSWORD_FORGOT_SEND)){
                        TextView mexSuccess = activity.findViewById(R.id.request_success);
                        Button request = activity.findViewById(R.id.password_forgot_request);

                        mexSuccess.setVisibility(View.VISIBLE);
                        request.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
        }

        private static Response.Listener<JSONObject> onResponseChangePassword(Activity activity){
            return response -> activity.onBackPressed();
        }

        private static Response.Listener<JSONObject> onResponseDeleteAccount(Activity activity) {
            return response -> {
                //SyncServiceHandler.create(activity).stop();
                try {
                    final JSONObject image = SqlLiteImageDao.create(activity).getImage();
                    if(image!=null)
                        if(ImageFileHelper.create(activity).getImage(image.getJSONObject(Constant.IMAGE).getString(Constant.NAME)).delete())
                            Log.d(TAG, "Image delete from local storage");
                    if(SqlLiteUserDao.create(activity).delete())
                        Log.d(TAG, "Account delete from local DB");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Preferences.Session.logout(activity);
                activity.startActivity(new Intent(activity, BootAppActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                activity.finish();
            };
        }

        private static OnUpdateGuiListener onResponseSyncInForeground(AppCompatActivity activity){
            return () -> {
                // UPDATE GUI IF RECEIVED DATA ( INTO FRAGMENT WORKOUT OR STATISTICS )
                if(activity instanceof SplashScreenActivity || activity instanceof LoginActivity){
                    //SyncServiceHandler.create(activity).start();
                    activity.startActivity(new Intent(activity, ApplicationActivity.class));
                    activity.finish();
                }
                final Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(ApplicationActivity.TAG);
                if(fragment!=null){
                   if(fragment instanceof WorkoutsFragment){
                       ((WorkoutsFragment)fragment).onResume();
                   }
                   if(fragment instanceof StatisticsFragment){
                       ((StatisticsFragment)fragment).onResume();
                   }
                   if(fragment instanceof HomeFragment){
                       ((HomeFragment)fragment).onResume();
                   }
                }
            };
        }

    }

}
