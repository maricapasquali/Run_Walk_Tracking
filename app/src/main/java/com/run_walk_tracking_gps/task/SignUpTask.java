package com.run_walk_tracking_gps.task;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.gui.components.dialog.RequestDialog;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.RequiresApi;


public class SignUpTask extends AsyncTask<Bitmap, Void, String> {

    private static final String TAG = SignUpTask.class.getName();
    private Activity activity;
    private RequestDialog progressDialog;
    private JSONObject user;
    private SignUpTask(Activity activity, JSONObject user){
        this.activity = activity;
        this.user = user;
    }

    public static SignUpTask create(Activity activity, JSONObject user){
        return new SignUpTask(activity, user);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = RequestDialog.create(activity);
        progressDialog.show();
    }



    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        Bitmap imageProfile = bitmaps[0];
        if(imageProfile!=null){
            try {
                return BitmapUtilities.BitMapToString(imageProfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String image_encode) {
        super.onPostExecute(image_encode);
        try {
            if(image_encode==null) user.remove(NetworkHelper.Constant.IMAGE);
            else{
                user.getJSONObject(NetworkHelper.Constant.IMAGE).put(NetworkHelper.Constant.IMG_ENCODE, image_encode);
                Log.d(TAG, "After compress : " + user.toString());
            }
            NetworkHelper.HttpRequest.requestSignUp(activity, user, progressDialog);

        } catch (InternetNoAvailableException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
