package com.run_walk_tracking_gps.gui.components.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.ActivationAccountActivity;
import com.run_walk_tracking_gps.gui.ForgotPassword;
import com.run_walk_tracking_gps.gui.LoginActivity;
import com.run_walk_tracking_gps.gui.SignUpActivity;
import com.run_walk_tracking_gps.gui.activity_of_settings.UserActivity;

import androidx.appcompat.app.AlertDialog;

public class RequestDialog extends AlertDialog {

    private static final String TAG = RequestDialog.class.getName();

    private Activity activity;

    private RequestDialog(Activity context) {
        super(context, R.style.full_screen_dialog);
        activity = context;
    }

    public static RequestDialog create(Activity context) {
        return new RequestDialog(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.custom_dialog_saving_fullscreen);
        getWindow().setLayout(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.MATCH_PARENT);

        TextView progressMsg = findViewById(R.id.progressmsg);
        progressMsg.setText(""); // TODO : NOME DEL PROGRESS DIALOG IN BASE ALL'ACTIVITY

        if(activity instanceof SignUpActivity){
            progressMsg.setText(new StringBuilder(getContext().getString(R.string.rec) + " ..."));
        }
        if(activity instanceof LoginActivity || activity instanceof ActivationAccountActivity){
            progressMsg.setText(new StringBuilder(getContext().getString(R.string.login) + " ..."));
        }
        if(activity instanceof UserActivity){
            progressMsg.setText(new StringBuilder(getContext().getString(R.string.delete_account) + " ..."));
        }
        if(activity instanceof ForgotPassword){
            progressMsg.setText(getContext().getString(R.string.request));
        }

        Log.d(TAG, progressMsg.getText().toString());
    }
}
