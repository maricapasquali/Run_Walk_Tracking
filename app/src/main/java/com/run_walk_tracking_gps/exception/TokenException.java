package com.run_walk_tracking_gps.exception;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.controller.ErrorQueue;
import com.run_walk_tracking_gps.gui.BootAppActivity;
import com.run_walk_tracking_gps.service.SyncServiceHandler;

public class TokenException extends BackgroundException {

    public TokenException(Context context) {
        super(context, R.string.account_use_other_device, TokenException.class.getSimpleName());
    }

    public static TokenException create(Context context){
        return new TokenException(context);
    }

    @Override
    protected void createAlertDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.token_not_valid)
                .setMessage(getMessage())
                .setNegativeButton(R.string.logout, (dialog, which) -> {
                    SyncServiceHandler.create(getContext()).stop();
                    DefaultPreferencesUser.logout(getContext());
                    ErrorQueue.getInstance(getContext()).remove(this);
                    getContext().startActivity(new Intent(getContext(), BootAppActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                })
                .setPositiveButton(R.string.ok, super.close())
                .create()
                .show();
    }

}
