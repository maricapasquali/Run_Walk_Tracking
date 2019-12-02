package com.run_walk_tracking_gps.gui;

import android.content.Context;

import com.run_walk_tracking_gps.model.enumerations.Language;

public abstract class AttachBaseContextActivity extends CommonActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Language.Utilities.changeContext(newBase, Language.defaultForUser(newBase)));
    }
}
