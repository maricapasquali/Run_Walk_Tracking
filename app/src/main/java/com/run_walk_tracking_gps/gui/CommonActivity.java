package com.run_walk_tracking_gps.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;


public abstract class CommonActivity extends AppCompatActivity {

    private final static String TAG = CommonActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"OnCreate");

        //hideNavigationBar();
        //fullScreenMode();
        init();
        listenerAction();
    }

    private void hideNavigationBar() {
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN );
    }

    private void fullScreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //show the activity in full screen
    }

    protected abstract void init();

    protected abstract void listenerAction();


    protected void addFragment(final Fragment fragment,final int container,final boolean toStack) {
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(container, fragment);
        if(toStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    protected void addFragment(final Fragment fragment, final int container,final boolean toStack, final String tag) {
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .replace(container, fragment, tag);

        if(toStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

}
