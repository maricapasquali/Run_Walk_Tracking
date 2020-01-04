package com.run_walk_tracking_gps.connectionserver;

import android.content.Context;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;


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

    public CustomRequest(Context context, int method, String url, JSONObject bodyJson, Response.Listener<JSONObject>  responseJsonListener) {
        super(method, url, response -> {
            Log.d(TAG, "onResponse");
            try {
                JSONObject JSONResponse = new JSONObject(response);
                Log.d(TAG, JSONResponse.toString());

                if(JSONResponse.has(ERROR)){
                    JSONObject error = JSONResponse.getJSONObject(ERROR);
                    switch (error.getInt(NetworkHelper.Constant.CODE)){
                        case NetworkHelper.HttpResponse.Code.Error.TOKEN_NOT_VALID:
                            throw new TokenException(context);
                    }
                    throw new SomeErrorHttpException(context, error.getString(NetworkHelper.Constant.DESCRIPTION));
                }
                responseJsonListener.onResponse(JSONResponse);

            } catch (JSONException | SomeErrorHttpException | TokenException e) {
                Log.e(TAG, response);
                BackgroundException ex;
                if(e instanceof TokenException)
                    ex = (TokenException) e;
                else {
                     ex = e instanceof SomeErrorHttpException ? (SomeErrorHttpException)e :
                                            SomeErrorHttpException.create(context, response);
                }
                ex.alert();
                //if(AppUtilities.isInForeground(context)) ex.alert();else ErrorQueue.getInstance(context).add(ex);
            }

        }, error -> {
            String errorHandler = null;
            if(error instanceof TimeoutError){
                errorHandler = "Ops ! Connessione lenta, riprova più tardi."; // TODO: 1/3/2020 MAKE A STRING FOR AL LANGUAGE
            }
            SomeErrorHttpException ex = SomeErrorHttpException.create(context, errorHandler==null ? error.toString(): errorHandler);
            ex.alert();
            //if(AppUtilities.isInForeground(context))ex.alert();else ErrorQueue.getInstance(context).add(ex);
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
