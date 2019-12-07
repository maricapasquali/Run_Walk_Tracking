package com.run_walk_tracking_gps.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;

import java.io.IOException;


public class CompressionBitMap extends AsyncTask<Bitmap, Void, String>{
    private static final String TAG = CompressionBitMap.class.getName();

    private Context context;
    private CompressionBitMap(Context context){
        this.context = context;
    }

    public static CompressionBitMap create(Context context){
        return new CompressionBitMap(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Start Compression");
        Toast.makeText(context, R.string.start_compression_image, Toast.LENGTH_LONG).show();
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
        Toast.makeText(context, R.string.end_compression_image, Toast.LENGTH_LONG).show();
    }
}
