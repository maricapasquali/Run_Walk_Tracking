package com.run_walk_tracking_gps.gui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;

import com.run_walk_tracking_gps.R;

public class RequestDialog extends ProgressDialog {

    private RequestDialog(Context context) {
        super(context, R.style.full_screen_dialog);
    }

    public static RequestDialog create(Context context) {
        return new RequestDialog(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog_layout_fullscreen);
        getWindow().setLayout(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.MATCH_PARENT);
    }
}
