package com.run_walk_tracking_gps.gui.components.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.ForgotPassword;
import com.run_walk_tracking_gps.gui.LoginActivity;
import com.run_walk_tracking_gps.gui.SignUpActivity;
import com.run_walk_tracking_gps.gui.TokenActivity;

public class RequestDialog extends ProgressDialog {

    private static final String TAG = RequestDialog.class.getName();
    private TextView progressMsg;

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

        progressMsg = findViewById(R.id.progressmsg);

        if(activity instanceof SignUpActivity){
            progressMsg.setText(new StringBuilder(getContext().getString(R.string.rec) + " ..."));
        }
        if(activity instanceof LoginActivity || activity instanceof TokenActivity){
            progressMsg.setText(new StringBuilder(getContext().getString(R.string.login) + " ..."));
        }

        if(activity instanceof ForgotPassword){
            progressMsg.setText(getContext().getString(R.string.request));
        }


        Log.e(TAG, progressMsg.getText().toString());
    }
}
