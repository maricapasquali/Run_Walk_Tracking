package com.run_walk_tracking_gps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.service.WorkoutService;


public class ReceiverRestartService  {

    /*

    extends BroadcastReceiver
    public ReceiverRestartService(){}


    @Override
    public void onReceive(Context context, Intent intent) {

        new Thread(()->  {
            Bundle args = intent.getExtras();

            Intent intentS = new Intent(context, WorkoutService.class)
                                .setAction(ActionReceiver.START_ACTION)
                                .putExtra(KeysIntent.WEIGHT_MORE_RECENT, args.getDouble(KeysIntent.WEIGHT_MORE_RECENT))
                                .putExtra(KeysIntent.SPORT_DEFAULT, args.getString(KeysIntent.SPORT_DEFAULT))
                                .putExtra(KeysIntent.WORKOUT, (Workout)args.getParcelable(KeysIntent.WORKOUT));
            Log.d("ReceiverRestartService", "ReceiverRestartService = "+ intentS);
            context.startService(intent);
        });
    }*/
}
