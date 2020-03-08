package com.run_walk_tracking_gps.exception;


import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.ErrorQueue;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.BootAppActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
                       .setCancelable(false)
                       .setNegativeButton(R.string.logout, (dialog, which) -> {
                            //SyncServiceHandler.create(getContext()).stop();
                            Preferences.Session.logout(getContext());
                            ErrorQueue.getInstance(getContext()).remove(this);
                            getContext().startActivity(new Intent(getContext(), BootAppActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                       })
                       .setPositiveButton(R.string.continue_here, ((dialog, which) ->
                                NetworkHelper.HttpRequest.request((AppCompatActivity) getContext(), NetworkHelper.Constant.CONTINUE_HERE, null)))
                       .create()
                       .show();
    }

}
