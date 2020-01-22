package com.run_walk_tracking_gps.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.run_walk_tracking_gps.utilities.BitmapUtilities;
import com.run_walk_tracking_gps.utilities.ImageFileHelper;

import java.io.IOException;

import androidx.annotation.RequiresApi;


public class DecompressionEncodeImageTask extends AsyncTask<String, Void, Bitmap>{
    private static final String TAG = DecompressionEncodeImageTask.class.getName();

    private Context context;
    private String nameImage;

    private DecompressionEncodeImageTask(Context context, String nameImage){
        this.context = context;
        this.nameImage = nameImage;
    }

    public static DecompressionEncodeImageTask create(Context context, String nameImage){
        return new DecompressionEncodeImageTask(context.getApplicationContext(), nameImage);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Start Decompression");
        //Toast.makeText(context, R.string.start_compression_image, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected Bitmap doInBackground(String... encodes) {
        publishProgress();
        try {
            return BitmapUtilities.StringToBitMap(encodes[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "Decompression ...");
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.d(TAG, "End Decompression");
        ImageFileHelper.create(context).storageImage(bitmap, nameImage);
        // Toast.makeText(context.getApplicationContext(), R.string.end_compression_image, Toast.LENGTH_LONG).show();
    }

}
