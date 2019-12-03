package com.run_walk_tracking_gps.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;

public abstract class CommonActivity extends AppCompatActivity {

    private final static String TAG = CommonActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"OnCreate : savedInstanceState = " + savedInstanceState);

        init(savedInstanceState);
        listenerAction();
    }

    protected abstract void init(Bundle savedInstanceState);

    protected abstract void listenerAction();


    protected void addFragment(final Fragment fragment, final int container,final boolean toStack, final String tag) {
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .replace(container, fragment, tag);

        if(toStack) fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    /*@Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e(TAG, "onConfigurationChanged ");
        init(null);
        listenerAction();
    }*/

}
