package com.run_walk_tracking_gps.exception;

import android.content.Context;

import com.run_walk_tracking_gps.R;

public class PasswordNotCorrectException extends Exception{
    private Context context;

    public PasswordNotCorrectException(Context context){
        this.context = context;
    }

    @Override
    public String getMessage() {
        return context.getString(R.string.not_correct_password);
    }
}