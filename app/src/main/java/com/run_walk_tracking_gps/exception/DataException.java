package com.run_walk_tracking_gps.exception;


import android.content.Context;
import android.util.Log;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.model.Workout;

import androidx.appcompat.app.AlertDialog;

public class DataException extends AbstractException {

    public DataException(Context context, Class classS){
        super(context);
        if(classS.equals(Workout.class)){
            setMessage(R.string.workout_not_set_correctly);
        }
        if(classS.equals(StatisticsData.class)){
            setMessage(R.string.weight_not_set_correctly);
        }
    }

    @Override
    public void alert() {
        Log.e(getContext().getPackageName(), getMessage());
        new AlertDialog.Builder(getContext())
                        .setTitle(R.string.info)
                        .setMessage(getMessage())
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, null)
                        .create()
                        .show();
    }
}
