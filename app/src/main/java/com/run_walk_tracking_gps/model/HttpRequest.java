package com.run_walk_tracking_gps.model;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HttpRequest {

    private static final String TAG = HttpRequest.class.getName();

    public static final String ERROR = "Error";
    private static final String ip = "10.10.10.3";//192.168.1.132

    public static final String SERVER = "http://"+ip+"/run_walk_tracking/";
    public static final String SIGN_UP = SERVER + "/account/signup.php";
    public static final String SIGN_IN = SERVER + "/account/signin.php";
    public static final String USER = SERVER + "/account/profile.php?"+ DatabaseNameFields.KEY_ID_USER+"=";


    public static boolean requestSignUp(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener) throws NullPointerException, IllegalArgumentException{
        if(bodyJson==null) throw new NullPointerException("bodyJson not null");
        final List<String> fieldRequired = DatabaseNameFields.fieldRequiredForSignUp();

        checkNotRequiredField(bodyJson::keys, s -> !s.equals(DatabaseNameFields.KEY_IMG_ENCODE) && !fieldRequired.contains(s));

        checkRequiredField(bodyJson::keys, (fieldSubmit) -> fieldRequired.stream().filter(f -> !fieldSubmit.contains(f)).toArray());

        return HttpRequest.requestJsonPostToServerVolley(context, SIGN_UP, bodyJson,responseJsonListener);
    }

    public static boolean requestSignIn(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener){
        if(bodyJson==null) throw new NullPointerException("bodyJson not null");
        final List<String> fieldRequired = DatabaseNameFields.fieldRequiredForSignIn();
        checkNotRequiredField(bodyJson::keys, s -> !fieldRequired.contains(s));
        checkRequiredField(bodyJson::keys, (fieldSubmit) -> fieldRequired.stream().filter(f -> !fieldSubmit.contains(f)).toArray());
        return HttpRequest.requestJsonPostToServerVolley(context, SIGN_IN, bodyJson,responseJsonListener);
    }

    public static boolean requestUserInformation(Context context, JSONObject bodyJson, Response.Listener<JSONObject> responseJsonListener) throws NullPointerException, JSONException {
        if(bodyJson==null) throw new NullPointerException("bodyJson not null");

        List<String> fieldRequired = DatabaseNameFields.fieldRequiredForUserInformation();

        checkNotRequiredField(bodyJson::keys, s -> !fieldRequired.contains(s));
        checkRequiredField(bodyJson::keys, (fieldSubmit) -> fieldRequired.stream().filter(f-> !fieldSubmit.contains(f)).toArray());

        int id_user = (Integer) bodyJson.get(DatabaseNameFields.KEY_ID_USER);
        return HttpRequest.requestJsonGetToServerVolley(context, USER + id_user, bodyJson,responseJsonListener);
    }


    private static void checkNotRequiredField(Iterable<String> iterable, Function<String, Boolean> fieldRequired){
        Object[] notRequired = StreamSupport.stream(iterable.spliterator(), false)
                .filter(k -> fieldRequired.apply(k)).toArray();
        if(notRequired.length>0) throw new IllegalArgumentException("NOT required : " +  Arrays.toString(notRequired));
    }

    private static void checkRequiredField(Iterable<String> iterable, Function<List<String>, Object[]> fieldRequired){
        Object[] missing = fieldRequired.apply(StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList()));
        if(missing.length>0) throw new IllegalArgumentException("Missing : " +  Arrays.toString(missing));
    }


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

    private static boolean requestJsonPutToServerVolley(final Context context, final String url, final JSONObject bodyJson,
                                                       Response.Listener<JSONObject> listenerResponse){

        return requestJsonToServerVolley(context, url, Request.Method.PUT, bodyJson, listenerResponse);
    }

    private static boolean requestJsonDeleteToServerVolley(final Context context, final String url, final JSONObject bodyJson,
                                                       Response.Listener<JSONObject> listenerResponse){

        return requestJsonToServerVolley(context, url, Request.Method.DELETE, bodyJson, listenerResponse);
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
        return !errRequest.get();
    }

    private static boolean requestStringToServerVolley(final Context context, final String url, final int method, final JSONObject jsonBody,
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
}
