package com.run_walk_tracking_gps;

import android.content.Context;
import android.util.Log;

import com.run_walk_tracking_gps.utilities.AppUtilities;

import org.junit.Test;

import androidx.test.InstrumentationRegistry;

public class AppTest {

    private Context context = InstrumentationRegistry.getTargetContext();

    @Test
    public void init(){
        Log.d(AppTest.class.getSimpleName(), AppUtilities.id(context));
    }
}
