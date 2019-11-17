package com.run_walk_tracking_gps.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;


public class CompressionBitMap extends AsyncTask<Bitmap, Void, String>{
    private static final String TAG = CompressionBitMap.class.getName();

    private CompressionBitMap(){}

    public static CompressionBitMap create(){
        return new CompressionBitMap();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Start Compression");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        publishProgress();
        return BitmapUtilities.BitMapToString(bitmaps[0]);
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
    }
}
