package com.run_walk_tracking_gps;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.google.android.gms.maps.model.LatLng;
import com.run_walk_tracking_gps.utilities.CollectionsUtilities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CollectionsUtilitiesTest {

    private Context context= InstrumentationRegistry.getTargetContext();
    private List<LatLng> list;
    private String string;

    @Before
    public void init(){

        list = Arrays.asList(
                new LatLng(44.3506747,11.7040691),
                new LatLng(44.3506995,11.7040577),
                new LatLng(44.3506976,11.7040631),
                new LatLng(44.3506906,11.7040652));


        string = "[lat/lng: (44.3506747,11.7040691), lat/lng: (44.3506995,11.7040577),"+
                "lat/lng: (44.3506976,11.7040631), lat/lng: (44.3506906,11.7040652)]";
        // = list.toString()

    }

    @Test
    public void convertStringToListObject(){
        Assert.assertEquals(list, CollectionsUtilities.convertStringToListLatLng(string));
    }
}
