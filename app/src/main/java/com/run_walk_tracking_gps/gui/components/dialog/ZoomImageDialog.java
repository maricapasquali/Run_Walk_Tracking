package com.run_walk_tracking_gps.gui.components.dialog;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.run_walk_tracking_gps.R;

import androidx.appcompat.app.AlertDialog;

public class ZoomImageDialog extends AlertDialog {

    private Bitmap bitmap;

    private ZoomImageDialog(Context context, Bitmap bitmap) {
        super(context, R.style.full_screen_dialog);
        this.bitmap = bitmap;
    }

    public static ZoomImageDialog create(Context context, Bitmap bitmap) {
        return new ZoomImageDialog(context, bitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_zoom);

        final ImageView imageView = findViewById(R.id.zoom_image_profile);

        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(null);
        ((RelativeLayout)imageView.getParent()).setOnClickListener(v -> dismiss());

        getWindow().setLayout(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.MATCH_PARENT);
    }
}
