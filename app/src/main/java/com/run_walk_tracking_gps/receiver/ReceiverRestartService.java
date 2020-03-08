package com.run_walk_tracking_gps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.service.WorkoutService;
import com.run_walk_tracking_gps.utilities.ServiceUtilities;

import org.json.JSONException;

import androidx.core.content.ContextCompat;

public class ReceiverRestartService  extends BroadcastReceiver{

    private static final String TAG = ReceiverRestartService.class.getName();

    public ReceiverRestartService(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if(!ServiceUtilities.isServiceRunning(context, WorkoutService.class)){
                double weight = DaoFactory.getInstance(context).getWeightDao().getLast();
                String sport = DaoFactory.getInstance(context).getSettingDao().getSportDefault();
                Intent intentS =  new Intent(context, WorkoutService.class)
                        .setAction(ActionReceiver.START_ACTION)
                        .putExtra(KeysIntent.WEIGHT_MORE_RECENT, weight)
                        .putExtra(KeysIntent.SPORT_DEFAULT, sport);
                Log.d(TAG, "ReceiverRestartService = "+ intentS);

                ContextCompat.startForegroundService(context, intentS);
            }else{
                Log.d(TAG, "JUST STARTED");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
