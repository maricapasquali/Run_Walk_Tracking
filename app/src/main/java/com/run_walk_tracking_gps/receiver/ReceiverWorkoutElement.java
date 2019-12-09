package com.run_walk_tracking_gps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.service.NotificationWorkout;
import com.run_walk_tracking_gps.service.WorkoutService;

public class ReceiverWorkoutElement extends BroadcastReceiver {

    private NotificationWorkout notificationWorkout;
    private WorkoutService.OnReceiverListener broadcastReceiver;

    public ReceiverWorkoutElement(WorkoutService.OnReceiverListener broadcastReceiver, NotificationWorkout notificationWorkout){
        super();
        this.broadcastReceiver = broadcastReceiver;
        this.notificationWorkout = notificationWorkout;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent!=null && intent.getAction()!=null){
            switch (intent.getAction()){
                case ActionReceiver.TIMER_ACTION: {
                    final int sec = intent.getIntExtra(KeysIntent.SECONDS, 0);
                    Log.e("WorkoutService", "onReceive: duration = " + sec  );
                    broadcastReceiver.onReceiverDuration(sec);
                    notificationWorkout.updateDuration(sec);
                }
                break;
                case ActionReceiver.DISTANCE_ENERGY_ACTION: {

                    final double distanceInKm = intent.getDoubleExtra(KeysIntent.DISTANCE, 0);
                    final double energyInKcal  = intent.getDoubleExtra(KeysIntent.ENERGY, 0) ;
                    Log.e("WorkoutService", "onReceive: km = " + distanceInKm + ", kacl = "+energyInKcal );
                    broadcastReceiver.onReceiverEnergy(energyInKcal);
                    broadcastReceiver.onReceiverDistance(distanceInKm);
                    notificationWorkout.updateDistance(distanceInKm);
                    notificationWorkout.updateEnergy(energyInKcal);
                }break;
            }
        }
    }
}
