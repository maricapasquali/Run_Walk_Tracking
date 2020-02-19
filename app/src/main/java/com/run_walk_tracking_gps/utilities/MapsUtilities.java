package com.run_walk_tracking_gps.utilities;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.run_walk_tracking_gps.gui.components.Factory;

import java.util.List;

public class MapsUtilities {

    public static PolylineOptions getPolylineOptions(String polylineEncode){
        return Factory.CustomPolylineOptions.create().addAll(decodePolyLine(polylineEncode));
    }

    public static String encodePolyLine(List<LatLng> locations){
        return PolyUtil.encode(locations);
    }

    public static List<LatLng> decodePolyLine(String polylineString){
        return PolyUtil.decode(polylineString);
    }

}
