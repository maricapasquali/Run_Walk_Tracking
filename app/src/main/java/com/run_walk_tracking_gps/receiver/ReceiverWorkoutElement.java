package com.run_walk_tracking_gps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.gui.NotificationWorkout;
import com.run_walk_tracking_gps.gui.fragments.HomeFragment;
import com.run_walk_tracking_gps.service.WorkoutService;

import java.util.ArrayList;


public class ReceiverWorkoutElement  { // TODO: NON IN USO
/*extends BroadcastReceiver
    private static final String TAG = ReceiverWorkoutElement.class.getName();
    private NotificationWorkout notificationWorkout;
    private WorkoutService.OnReceiverListener broadcastReceiver;


    private ReceiverWorkoutElement(NotificationWorkout notificationWorkout){
        super();
        this.notificationWorkout = notificationWorkout;
    }

    private ReceiverWorkoutElement(NotificationWorkout notificationWorkout, WorkoutService.OnReceiverListener broadcastReceiver){
        this(notificationWorkout);
        this.broadcastReceiver = broadcastReceiver;
    }

    public static ReceiverWorkoutElement create(NotificationWorkout notificationWorkout){
        return new ReceiverWorkoutElement(notificationWorkout);
    }

    private ReceiverWorkoutElement(WorkoutService.OnReceiverListener broadcastReceiver){
        super();
        this.broadcastReceiver = broadcastReceiver;
    }

    public static ReceiverWorkoutElement create(WorkoutService.OnReceiverListener broadcastReceiver){
        return new ReceiverWorkoutElement(broadcastReceiver);
    }

    public static ReceiverWorkoutElement create(NotificationWorkout notificationWorkout, WorkoutService.OnReceiverListener broadcastReceiver){
        return new ReceiverWorkoutElement(notificationWorkout, broadcastReceiver);
    }

    public void setBroadcastReceiver(WorkoutService.OnReceiverListener broadcastReceiver) {
        this.broadcastReceiver = broadcastReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      new Handler().post(()->{
          if(intent!=null && intent.getAction()!=null){
              switch (intent.getAction()){

                  case ActionReceiver.TIMER_ACTION: {
                      final String sec = intent.getStringExtra(KeysIntent.SECONDS);
                      Log.d(TAG, "Duration (Sec) = " + sec);
                      //broadcastReceiver.onReceiverDuration(sec);
                      notificationWorkout.updateDuration(sec);
                  }
                  break;
                  case ActionReceiver.DISTANCE_ENERGY_ACTION: {

                      final String distanceInKm = intent.getStringExtra(KeysIntent.DISTANCE);
                      final String energyInKcal  = intent.getStringExtra(KeysIntent.ENERGY) ;
                      Log.d(TAG, "Distance (km) = " + distanceInKm + ", Energy (kcal) = "+energyInKcal );

                      //broadcastReceiver.onReceiverEnergy(energyInKcal);
                      //broadcastReceiver.onReceiverDistance(distanceInKm);

                      notificationWorkout.updateDistance(distanceInKm);
                      notificationWorkout.updateEnergy(energyInKcal);
                  }
                  break;

                  case ActionReceiver.DRAWING_MAP_TIMER_ACTION:
                      final PolylineOptions route = intent.getParcelableExtra(KeysIntent.ROUTE);
                      Log.e(TAG, "Route polyline receive");
                      //broadcastReceiver.onReceiverMapRoute().addPolyLineOnMap(route);
                      break;
              }
          }
      });
    }*/
}
