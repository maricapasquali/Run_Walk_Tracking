package com.run_walk_tracking_gps.connectionserver;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.exception.BackgroundException;
import com.run_walk_tracking_gps.exception.SomeErrorHttpException;
import com.run_walk_tracking_gps.exception.TokenException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.run_walk_tracking_gps.connectionserver.NetworkHelper.Constant.ERROR;
import static com.run_walk_tracking_gps.connectionserver.NetworkHelper.TAG;

public class CustomRequest extends StringRequest {

    private static final String BODY_CONTENT_TYPE = "application/json; charset=utf-8";
    private final static int INITIAL_TIMEOUT_MS = 50000;
    private final static int MAX_NUM_RETRIES = 3;

    private JSONObject bodyJson;

    CustomRequest(Context context, int method, String url, JSONObject bodyJson, Response.Listener<JSONObject>  responseJsonListener) {
        super(method, url, response -> {
            Log.d(TAG, "onResponse");
            try {
                final JSONObject JSONResponse = new JSONObject(response);
                Log.d(TAG, JSONResponse.toString());
                if(JSONResponse.has(ERROR)) {
                    final JSONObject error = JSONResponse.getJSONObject(ERROR);
                    switch (error.getInt(NetworkHelper.Constant.CODE)){
                        case NetworkHelper.HttpResponse.Code.Error.TOKEN_NOT_VALID:
                            throw new TokenException(context);
                    }
                    throw new SomeErrorHttpException(context, error.getString(NetworkHelper.Constant.DESCRIPTION));
                }
                responseJsonListener.onResponse(JSONResponse);
            } catch (Exception e) {
                Log.e(TAG, "Error = " + e + ", (response) = " + response);

                BackgroundException ex;
                if(e instanceof TokenException)
                    ex = (TokenException) e;
                else if(e instanceof SomeErrorHttpException)
                    ex = (SomeErrorHttpException)e;
                else{
                    ex = SomeErrorHttpException.create(context, e instanceof JSONException? response: e.getMessage());
                }
                ex.alert();
            }
        }, error -> {
            if(error.networkResponse!=null){
                String errorHandler = String.valueOf(error.networkResponse.statusCode);

                if(error instanceof TimeoutError)
                    errorHandler = " : " + context.getString(R.string.connection_slow);
                else if(error instanceof ServerError)
                    errorHandler = " : " + context.getString(R.string.internal_server_error);

                SomeErrorHttpException ex = SomeErrorHttpException.create(context, errorHandler);
                ex.alert();
            }
        });
        this.bodyJson = bodyJson;
    }

    @Override
    public String getBodyContentType() {
        return BODY_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        return bodyJson.toString().getBytes();
    }

    @Override
    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        return super.setRetryPolicy(new DefaultRetryPolicy( INITIAL_TIMEOUT_MS, MAX_NUM_RETRIES, 
                                                           DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Request<?> setTag(Object tag) {
        return super.setTag(bodyJson);
    }
}
