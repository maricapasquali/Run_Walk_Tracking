package com.run_walk_tracking_gps.connectionserver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.run_walk_tracking_gps.exception.SomeErrorHttpException;
import com.run_walk_tracking_gps.gui.BootAppActivity;
import com.run_walk_tracking_gps.service.SyncServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import static com.run_walk_tracking_gps.connectionserver.NetworkHelper.Constant.ERROR;

public class CustomRequest extends StringRequest {

    private static final String BODY_CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String TAG = NetworkHelper.class.getName();

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
                        {
                            SyncServiceHandler.create(context).stop();
                            context.startActivity(new Intent(context, BootAppActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            return;
                        }
                    }
                    throw new SomeErrorHttpException(context, error.getString(NetworkHelper.Constant.DESCRIPTION));
                }

                responseJsonListener.onResponse(JSONResponse);

            } catch (JSONException | SomeErrorHttpException e) {
                Log.e(TAG, response);
                if(e instanceof SomeErrorHttpException) ((SomeErrorHttpException)e).alert();
                else SomeErrorHttpException.create(context, response).alert();
            }

        }, error -> {
            String errorHandler = null;
            if(error instanceof TimeoutError){
                errorHandler = "Ops ! Connessione lenta, riprova più tardi."; // TODO: 1/3/2020 MAKE A STRING FOR AL LANGUAGE
            }
            SomeErrorHttpException.create(context, errorHandler==null ? error.toString(): errorHandler).alert();
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
