package com.run_walk_tracking_gps.connectionserver;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.exception.SomeErrorHttpException;
import com.run_walk_tracking_gps.exception.TokenException;

import org.json.JSONException;
import org.json.JSONObject;

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
                responseJsonListener.onResponse(JSONResponse);
            } catch (Exception e) {
                Log.e(TAG, "Error = " + e + ", (response) = " + response);

                SomeErrorHttpException.create(context, e instanceof JSONException? response: e.getMessage())
                                      .alert();
            }
        }, error -> {
            Log.e(TAG, error.toString() + ": status = " + error.networkResponse);
            String errorHandlerMessage = "";
            if(error instanceof TimeoutError)
                errorHandlerMessage = context.getString(R.string.connection_slow);
            else if(error instanceof NoConnectionError) {
                errorHandlerMessage = context.getString(R.string.no_connection);
            } else if(error.networkResponse != null) {
                errorHandlerMessage = error.networkResponse.statusCode + " : ";
                try {
                    JSONObject body = new JSONObject(new String(error.networkResponse.data));
                    Log.e(TAG, "Error: " + errorHandlerMessage + " - " + body);
                    if(body.has(ERROR)) {
                        final JSONObject errorJson = body.getJSONObject(ERROR);
                        if(
                                errorJson.has(NetworkHelper.Constant.CODE) &&
                                errorJson.getInt(NetworkHelper.Constant.CODE) == NetworkHelper.HttpResponse.Code.Error.TOKEN_NOT_VALID
                        ){
                            TokenException.create(context).alert();
                            return;
                        }
                        errorHandlerMessage += errorJson.getString(NetworkHelper.Constant.DESCRIPTION);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    errorHandlerMessage += context.getString(R.string.internal_server_error);
                }
            }
            SomeErrorHttpException.create(context, errorHandlerMessage).alert();
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
