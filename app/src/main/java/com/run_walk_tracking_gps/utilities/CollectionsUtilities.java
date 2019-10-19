package com.run_walk_tracking_gps.utilities;

import android.os.Build;
import android.support.annotation.RequiresApi;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionsUtilities <K, V>{

    public static CollectionsUtilities create(){
        return new CollectionsUtilities();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LinkedHashMap<K, V> listOfMapEntryToLinkedHashMap(List<Map.Entry<K, V>> list){
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey,
                Map.Entry::getValue,
                (v1,v2)->v1,
                LinkedHashMap::new));
    }

    public ArrayList<Map.Entry<K, V>> mapToListOfMapEntry(Map<K, V> map){
        return new ArrayList<>(map.entrySet());
    }
}
