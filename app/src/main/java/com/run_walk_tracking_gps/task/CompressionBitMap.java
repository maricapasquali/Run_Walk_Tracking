package com.run_walk_tracking_gps.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.run_walk_tracking_gps.utilities.BitmapUtilities;


public class CompressionBitMap extends AsyncTask<Bitmap, Void, String>{
    private CompressionBitMap(){}

    private static OnPostExecuteListener onPostExecuteListener ;
    public static CompressionBitMap create(OnPostExecuteListener listener){
        onPostExecuteListener = listener;
        return new CompressionBitMap();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        return BitmapUtilities.BitMapToString(bitmaps[0]);
    }

    @Override
    protected void onPostExecute(String image_encode) {
        super.onPostExecute(image_encode);
        onPostExecuteListener.onPostExecute(image_encode);
    }

    public interface OnPostExecuteListener{
        void onPostExecute(String image_encode);
    }
}
