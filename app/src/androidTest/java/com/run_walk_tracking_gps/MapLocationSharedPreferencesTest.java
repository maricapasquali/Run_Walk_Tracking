package com.run_walk_tracking_gps;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.run_walk_tracking_gps.controller.Preferences;

import androidx.test.InstrumentationRegistry;

import org.junit.Test;

import java.util.ArrayList;
// TODO: 2/17/2020 RIMUOVERE
public class MapLocationSharedPreferencesTest {
    private final String TAG = MapLocationSharedPreferencesTest.class.getName();
    private Context context = InstrumentationRegistry.getTargetContext();
    @Test
    public void test(){

        ArrayList<LatLng> set = new ArrayList<>();
        Preferences.MapLocation.create(context);

        //Assert.assertEquals(Preferences.MapLocation.get(context).getPoints(), set);


        LatLng l1 = new LatLng(44.3506747,11.7040691);
        LatLng l2 = new LatLng(44.3506995,11.7040577);
        LatLng l3 = new LatLng(44.3506976,11.7040631);
        LatLng l4 = new LatLng(44.3506906,11.7040652);


        Preferences.MapLocation.add(context, l1);
        Preferences.MapLocation.add(context, l2);
        Preferences.MapLocation.add(context, l3);
        Preferences.MapLocation.add(context, l4);

        set.add(l1);
        set.add(l2);
        set.add(l3);
        set.add(l4);

        Log.e(TAG, "Test = " + set.toString());
        Log.e(TAG, "Preference = " +  Preferences.MapLocation.getPolylineOptions(context));

       // Assert.assertEquals(Preferences.MapLocation.get(context).getPoints(), set);

    }
}
