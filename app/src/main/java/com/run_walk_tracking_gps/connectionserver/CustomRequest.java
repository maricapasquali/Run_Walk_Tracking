package com.run_walk_tracking_gps.connectionserver;

import android.content.Context;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.exception.BackgroundException;
import com.run_walk_tracking_gps.exception.SomeErrorHttpException;
import com.run_walk_tracking_gps.exception.TokenException;

import org.json.JSONException;
import org.json.JSONObject;

import static com.run_walk_tracking_gps.connectionserver.NetworkHelper.Constant.ERROR;
import static com.run_walk_tracking_gps.connectionserver.NetworkHelper.TAG;

public class CustomRequest extends StringRequest {

    private static final String BODY_CONTENT_TYPE = "application/json; charset=utf-8";

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
                Log.e(TAG, "Error = " + e);
                Log.e(TAG, "Error (response) = " + response);

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
            String errorHandler = null;
            if(error instanceof TimeoutError)
                errorHandler = context.getString(R.string.connection_slow);

            SomeErrorHttpException ex =
                    SomeErrorHttpException.create(context, errorHandler==null ? error.toString(): errorHandler);
            ex.alert();
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

}
