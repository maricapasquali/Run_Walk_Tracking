package com.run_walk_tracking_gps.gui.components.dialog;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.material.appbar.AppBarLayout;
import com.run_walk_tracking_gps.R;

import androidx.appcompat.app.AlertDialog;

public class DelayedStartWorkoutDialog extends AlertDialog {

    private static final int INTERVAL = 1000;
    private static final int START_DELAY = 15000;
    private CountDownTimer countDownTimer;
    private TextView textView ;
    private Button startNow;

    private OnClickStartNowListener onClickStartNowListener;

    private DelayedStartWorkoutDialog(Context context, OnClickStartNowListener onClickStartNowListener) {
        super(context, R.style.full_screen_dialog);
        this.onClickStartNowListener = onClickStartNowListener;
    }

    public static DelayedStartWorkoutDialog create(Context context, OnClickStartNowListener onClickStartNowListener) {
        return new DelayedStartWorkoutDialog(context, onClickStartNowListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_delay_start);
        getWindow().setLayout(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.MATCH_PARENT);

        textView = findViewById(R.id.countdown);
        startNow = findViewById(R.id.start_now);

        countDownTimer =  new CountDownTimer(START_DELAY, INTERVAL) {

            public void onTick(long millisUntilFinished) {
                setSec(millisUntilFinished / INTERVAL);
            }

            public void onFinish() {
                dismiss();
                onClickStartNowListener.onClickStartNowListener();
            }
        };

        startNow.setOnClickListener(v ->{
            countDownTimer.cancel();
            dismiss();
            onClickStartNowListener.onClickStartNowListener();

        });
    }

    @Override
    public void show() {
        super.show();
        countDownTimer.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        countDownTimer.cancel();
    }

    private void setSec(long sec){
        textView.setText(String.valueOf(sec));
    }

    public interface OnClickStartNowListener{
        void onClickStartNowListener();
    }


}
