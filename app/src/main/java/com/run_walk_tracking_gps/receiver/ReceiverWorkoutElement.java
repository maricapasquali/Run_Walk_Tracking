package com.run_walk_tracking_gps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.gui.fragments.HomeFragment;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.service.NotificationWorkout;
import com.run_walk_tracking_gps.service.WorkoutService;

import java.util.List;

public class ReceiverWorkoutElement extends BroadcastReceiver {

    private static final String TAG = ReceiverWorkoutElement.class.getName();
    private NotificationWorkout notificationWorkout;
    private WorkoutService.OnReceiverListener broadcastReceiver;

    public ReceiverWorkoutElement(){}

    public ReceiverWorkoutElement(NotificationWorkout notificationWorkout){
        super();
        this.notificationWorkout = notificationWorkout;
    }

    public void setBroadcastReceiver(WorkoutService.OnReceiverListener broadcastReceiver) {
        this.broadcastReceiver = broadcastReceiver;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        new Handler().post(()->{

            if(intent!=null && intent.getAction()!=null){
                switch (intent.getAction()){

                    case ActionReceiver.TIMER_ACTION: {
                        final String sec = intent.getStringExtra(KeysIntent.SECONDS);
                        Log.e(TAG, "Duration (Sec) = " + sec);
                        broadcastReceiver.onReceiverDuration(sec);
                        notificationWorkout.updateDuration(sec);
                    }
                    break;
                    case ActionReceiver.DISTANCE_ENERGY_ACTION: {

                        final String distanceInKm = intent.getStringExtra(KeysIntent.DISTANCE);
                        final String energyInKcal  = intent.getStringExtra(KeysIntent.ENERGY) ;
                        Log.e(TAG, "Distance (km) = " + distanceInKm + ", Energy (kcal) = "+energyInKcal );
                        broadcastReceiver.onReceiverEnergy(energyInKcal);
                        broadcastReceiver.onReceiverDistance(distanceInKm);

                        notificationWorkout.updateDistance(distanceInKm);
                        notificationWorkout.updateEnergy(energyInKcal);
                    }
                    break;
                }
            }

        });
    }
}
