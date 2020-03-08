package com.run_walk_tracking_gps;

import android.app.Application;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.run_walk_tracking_gps.utilities.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import androidx.annotation.RequiresApi;


public class RunWalkTracking extends Application {

    private static final String TAG = "RunWalkTracking";

    private static final String LOG_DIR = "log";
    private static final String LOG_FILE = "logcat_" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + ".txt";
    /**
     * Called when the application is starting, before any activity, service, or receiver objects (excluding content providers) have been created.
     */

    public void onCreate() {
        super.onCreate();

        if (isExternalStorageWritable()){
            Log.i(TAG,  "External Storage Writable");
            File logDirectory = FileUtilities.getDirectory(this, LOG_DIR);
            File logFile = new File(logDirectory, LOG_FILE);

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
                Log.i(TAG,  logFile.getPath());
                //process = Runtime.getRuntime().exec( "logcat -f " + logFile + " *:S MyActivity:D MyActivity2:D");
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } else if ( isExternalStorageReadable() ) {
            // only readable
            Log.i(TAG,  "External Storage Readable");
        } else {
            // not accessible
            Log.i(TAG,  "External Storage Not Accessible");
        }
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

}
