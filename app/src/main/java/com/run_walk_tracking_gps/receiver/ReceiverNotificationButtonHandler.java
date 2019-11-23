package com.run_walk_tracking_gps.receiver;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import com.run_walk_tracking_gps.gui.fragments.HomeFragment;

import java.util.List;

public class ReceiverNotificationButtonHandler extends BroadcastReceiver {

    private static final String TAG = ReceiverNotificationButtonHandler.class.getName();

    public ReceiverNotificationButtonHandler(){
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null && intent.getAction()!=null){
            // TODO: 11/23/2019 RENDERE NON STATICO

            final List<FloatingActionButton> controllerButton = HomeFragment.getControllerButton();

            if(controllerButton!=null && !controllerButton.isEmpty()){
                switch (intent.getAction()){
                    case ActionReceiver.PAUSE_ACTION:
                        Log.e(TAG,  "Pause clicked");
                        controllerButton.get(0).callOnClick();
                        break;
                    case ActionReceiver.RESTART_ACTION:
                        Log.e(TAG,  "Restart clicked");
                        controllerButton.get(1).callOnClick();
                        break;
                    case ActionReceiver.STOP_ACTION:
                        Log.e(TAG, "Stop clicked");
                        controllerButton.get(2).callOnClick();
                        break;
                }
            }
        }
    }

}
