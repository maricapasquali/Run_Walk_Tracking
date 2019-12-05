package com.run_walk_tracking_gps.utilities;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class CollectionsUtilities <K, V>{

    public static CollectionsUtilities create(){
        return new CollectionsUtilities();
    }

    public LinkedHashMap<K, V> listOfMapEntryToLinkedHashMap(List<Map.Entry<K, V>> list){
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey,
                Map.Entry::getValue,
                (v1,v2)->v1,
                LinkedHashMap::new));
    }

    public ArrayList<Map.Entry<K, V>> mapToListOfMapEntry(Map<K, V> map){
        return new ArrayList<>(map.entrySet());
    }

    // TODO: 12/5/2019 MIGLIORARE 
    public static List<LatLng> convertStringToListLatLng(String toString){
        List<LatLng> list = new ArrayList<>();

        Object[] o = toString.replace("lat/lng:", "")
                             .replace("(","")
                             .replace(")", "")
                             .replace("[", "")
                             .replace("]", "")
                             .split(",");

        for (int i=0; i<o.length; i+=2){
            list.add(new LatLng(Double.valueOf(o[i].toString()), Double.valueOf(o[i+1].toString())));
        }
        return list;
    }
}
