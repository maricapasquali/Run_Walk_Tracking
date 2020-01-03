package com.run_walk_tracking_gps.exception;

import android.content.Context;

import com.run_walk_tracking_gps.R;

public class PasswordNotCorrectException extends AbstractException{

    public PasswordNotCorrectException(Context context){
        super(context, R.string.not_correct_password);
    }

    @Override
    public void alert() {
    }
}