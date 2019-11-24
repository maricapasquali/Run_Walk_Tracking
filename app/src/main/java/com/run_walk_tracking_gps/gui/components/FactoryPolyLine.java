package com.run_walk_tracking_gps.gui.components;

import android.graphics.Color;

import com.google.android.gms.maps.model.PolylineOptions;

public class FactoryPolyLine {

    private static int WIDTH_DEFAULT = 15;
    private static int COLOR_DEFAULT = Color.BLUE;

    public static PolylineOptions create(){
        return new PolylineOptions().color(COLOR_DEFAULT).width(WIDTH_DEFAULT);
    }
}
