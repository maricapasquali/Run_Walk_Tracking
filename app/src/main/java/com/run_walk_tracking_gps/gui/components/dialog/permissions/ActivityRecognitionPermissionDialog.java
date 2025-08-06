package com.run_walk_tracking_gps.gui.components.dialog.permissions;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.utilities.PermissionUtilities;

public class ActivityRecognitionPermissionDialog extends AlertDialog.Builder {

    public ActivityRecognitionPermissionDialog(@NonNull Activity activity) {
        super(activity);

        this.setTitle(R.string.permission_activity_recognition_title);

        this.setMessage(R.string.permission_activity_recognition_message);

        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)) {
            this.setPositiveButton(R.string.forward, (dialog, which) -> PermissionUtilities.setActivityRecognitionPermission(activity));
        } else {
            this.setPositiveButton(R.string.goto_settings, (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            });
        }
    }

    public AlertDialog.Builder setOnNegatePermissionListener(DialogInterface.OnClickListener negativeListener) {
        this.setNegativeButton(R.string.cancel, negativeListener);
        return this;
    }
}
