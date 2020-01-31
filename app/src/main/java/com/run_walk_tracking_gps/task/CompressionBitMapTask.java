package com.run_walk_tracking_gps.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;

import org.json.JSONException;

import java.io.IOException;

import androidx.annotation.RequiresApi;


public class CompressionBitMapTask extends AsyncTask<Bitmap, Void, String>{
    private static final String TAG = CompressionBitMapTask.class.getName();

    private OnPostExecuteListener onPostExecuteListener;
    private Context context;

    private CompressionBitMapTask(Context context){
        this.context = context;
    }

    public static CompressionBitMapTask create(Context context){
        return new CompressionBitMapTask(context.getApplicationContext());
    }


    private CompressionBitMapTask(Context context, OnPostExecuteListener postExecute){
        this.context = context;
        onPostExecuteListener = postExecute;
    }

    public static CompressionBitMapTask create(Context context, OnPostExecuteListener postExecute){
        return new CompressionBitMapTask(context.getApplicationContext(), postExecute);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Start Compression");
 //     Toast.makeText(context.getApplicationContext(), R.string.start_compression_image, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        publishProgress();
        try {
            return BitmapUtilities.BitMapToString(bitmaps[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "Compression ...");
    }

    @Override
    protected void onPostExecute(String image_encode) {
        super.onPostExecute(image_encode);
        Log.d(TAG, "End Compression");
        if(image_encode!=null){
            try {
                onPostExecuteListener.onPostExecute(image_encode);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InternetNoAvailableException e) {
                e.printStackTrace();
            }
        }
        // Toast.makeText(context.getApplicationContext(), R.string.end_compression_image, Toast.LENGTH_LONG).show();
    }

    public  interface  OnPostExecuteListener{
        void onPostExecute(String image_encode) throws JSONException, InternetNoAvailableException;
    }
}
