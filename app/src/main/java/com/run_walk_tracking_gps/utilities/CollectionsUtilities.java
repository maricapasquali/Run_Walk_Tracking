package com.run_walk_tracking_gps.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class CollectionsUtilities <K, V>{

    public LinkedHashMap<K, V> listOfMapEntryToLinkedHashMap(List<Map.Entry<K, V>> list){
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey,
                Map.Entry::getValue,
                (v1,v2)->v1,
                LinkedHashMap::new));
    }

    public ArrayList<Map.Entry<K, V>> mapToListOfMapEntry(Map<K, V> map){
        return new ArrayList<>(map.entrySet());
    }

    public static class ListUtilities<T>{
        public ArrayList<T> difference(List<T> bigger, List<T>  small){
            final Set<T> biggerSet = new HashSet<>(bigger);
            final Set<T> smallerSet = new HashSet<>(small);
            biggerSet.removeAll(smallerSet);
            return new ArrayList<>(biggerSet);
        }

        public static class ArrayListAnySize<T> extends ArrayList<T>{
            @Override
            public void add(int index, T element){
                if(index >= 0 && index <= size()){
                    super.add(index, element);
                    return;
                }
                int insertNulls = index - size();
                for(int i = 0; i < insertNulls; i++){
                    super.add(null);
                }
                super.add(element);
            }
        }

    }

}
