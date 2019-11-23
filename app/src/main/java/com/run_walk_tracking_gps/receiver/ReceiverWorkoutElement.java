package com.run_walk_tracking_gps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.service.NotificationWorkout;
import com.run_walk_tracking_gps.service.WorkoutServiceHandler;

public class ReceiverWorkoutElement extends BroadcastReceiver {

    private WorkoutServiceHandler.OnDistanceListener onDistanceListener;
    private WorkoutServiceHandler.OnEnergyListener onEnergyListener;
    private WorkoutServiceHandler.OnReceiverListener broadcastReceiver;

    private NotificationWorkout notificationWorkout;


    public ReceiverWorkoutElement(WorkoutServiceHandler.OnReceiverListener broadcastReceiver,
                                  WorkoutServiceHandler.OnDistanceListener onDistanceListener,
                                  WorkoutServiceHandler.OnEnergyListener onEnergyListener,
                                  NotificationWorkout notificationWorkout){
        super();
        this.onDistanceListener = onDistanceListener;
        this.onEnergyListener = onEnergyListener;
        this.broadcastReceiver = broadcastReceiver;
        this.notificationWorkout = notificationWorkout;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent!=null && intent.getAction()!=null){
            switch (intent.getAction()){
                case ActionReceiver.TIMER_ACTION: {

                    final int sec = intent.getIntExtra(context.getString(R.string.second), 0);

                    final double distanceInKm = onDistanceListener.getDistanceInKm();
                    final double energyInKcal  = onEnergyListener.getEnergyInKcal(distanceInKm) ;
                    broadcastReceiver.onReceiver(sec, distanceInKm, energyInKcal);

                    notificationWorkout.update(sec, distanceInKm, energyInKcal);
                }
                break;

            }

        }

    }
}
