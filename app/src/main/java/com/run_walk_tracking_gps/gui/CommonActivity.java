package com.run_walk_tracking_gps.gui;

import android.os.Bundle;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public abstract class CommonActivity extends AppCompatActivity {

    private final static String TAG = CommonActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"OnCreate : savedInstanceState = " + savedInstanceState);

        init(savedInstanceState);
        listenerAction();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    protected abstract void init(Bundle savedInstanceState);

    protected abstract void listenerAction();

    protected void addFragment(final Fragment fragment, final int container, final boolean toStack, final String tag) {
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .replace(container, fragment, tag);

        if(toStack) fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();
    }

}
