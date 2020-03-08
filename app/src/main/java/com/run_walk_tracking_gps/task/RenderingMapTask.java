package com.run_walk_tracking_gps.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.PolylineOptions;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.fragments.HomeFragment;

import java.util.function.Consumer;

public class RenderingMapTask extends AsyncTask<Void, Void, PolylineOptions> {

    private Context context;
    private Consumer<PolylineOptions> consumer;
    private RenderingMapTask(Context context, Consumer<PolylineOptions> consumer){
        this.context = context;
        this.consumer = consumer;
    }

    public static RenderingMapTask create(Context context, Consumer<PolylineOptions> consumer){
        return new RenderingMapTask(context.getApplicationContext(), consumer);
    }

    @Override
    protected PolylineOptions doInBackground(Void... voids) {
        return Preferences.WorkoutInExecution.MapLocation.getPolylineOptions(context);
    }

    @Override
    protected void onPostExecute(PolylineOptions polylineOptions) {
        //Toast.makeText(context, "Render map", Toast.LENGTH_SHORT).show();
        Log.d(HomeFragment.class.getName(), "Render map");
        consumer.accept(polylineOptions);
    }
}