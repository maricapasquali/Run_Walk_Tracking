package com.run_walk_tracking_gps.controller;

import android.content.Context;
import android.util.Log;

import com.run_walk_tracking_gps.exception.BackgroundException;

import java.util.Set;
import java.util.TreeSet;


public class ErrorQueue {

    private static final String TAG = ErrorQueue.class.getName();
    private static ErrorQueue errorQueue;

    private Context context;

    private static Set<BackgroundException> errors =  new TreeSet<>();

    private ErrorQueue(Context context) {
        this.context = context;
    }

    public static synchronized ErrorQueue getInstance(Context context){
        if(errorQueue == null){
            Log.e(TAG, "Create Error Handler");
            errorQueue = new ErrorQueue(context.getApplicationContext());
        }
        return errorQueue;
    }

    public void add(BackgroundException ex){
        errors.add(ex);
        Log.e(TAG, "Add Error : " + errors);
    }

    public void remove(BackgroundException ex){
        errors.remove(ex);
        Log.e(TAG, "Remove Error : " + errors);
    }

    public void removeAll(){
        errors.clear();
        Log.e(TAG, "Clear Error : " + errors);
    }

    public static void getErrors(Context context) {
        Log.e(TAG, "Get Errors ");
        if(errors.size()>0)
            errors.forEach(i-> {
                i.setContext(context);
                i.alert();
            });
    }

}
