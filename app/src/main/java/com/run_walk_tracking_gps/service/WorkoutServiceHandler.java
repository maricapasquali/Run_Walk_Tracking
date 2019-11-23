package com.run_walk_tracking_gps.service;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.run_walk_tracking_gps.receiver.ReceiverNotificationButtonHandler;

import java.util.List;

public class WorkoutServiceHandler {

    private static final String TAG = WorkoutServiceHandler.class.getName();
    private static boolean isWorkoutServiceRunning = false;

    private TimeService timeService;
    private DistanceService distanceService;
    private EnergyService energyService;


    @RequiresApi(api = Build.VERSION_CODES.O)
    private WorkoutServiceHandler(final Context context, final double weight, final OnReceiverListener broadcastReceiver){
        this.distanceService = new DistanceService(context);
        this.energyService = new EnergyService(context, weight);
        this.timeService = new TimeService(context, broadcastReceiver,(OnDistanceListener) distanceService, (OnEnergyListener) energyService);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static WorkoutServiceHandler createService(final Context context, final double weight, final OnReceiverListener broadcastReceiver){
        return new WorkoutServiceHandler(context, weight, broadcastReceiver);
    }

    public void start(){
        Log.d(TAG, "start");
        isWorkoutServiceRunning = true;
        distanceService.start();
        timeService.start();
    }

    public void pause(){
        Log.d(TAG, "pause");
        distanceService.pause();
        timeService.pause();
    }

    public void restart(){
        Log.d(TAG, "restart");
        distanceService.restart();
        timeService.restart();
    }

    public void stop(){
        Log.d(TAG, "stop");
        distanceService.stop();
        timeService.stop();
        isWorkoutServiceRunning = false;
    }

    public boolean isRunning(){
        return isWorkoutServiceRunning;
    }

    public void block() {
        Log.d(TAG, "block_screen");
        timeService.getNotification().setInvisibleButton();
    }

    public void unblock() {
        Log.d(TAG, "unblock_screen");
        timeService.getNotification().restartClicked();
    }

    // INTERFACE
    public interface OnReceiverListener{
        void onReceiver(int sec, double distance, double energy);
    }

    public interface OnDistanceListener{
        double getDistanceInKm();
    }

    public interface OnEnergyListener{
        double getEnergyInKcal(double km);
    }

}
