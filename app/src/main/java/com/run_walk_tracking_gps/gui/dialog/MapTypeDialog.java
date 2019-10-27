package com.run_walk_tracking_gps.gui.dialog;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.android.gms.maps.GoogleMap;
import com.run_walk_tracking_gps.R;

public class MapTypeDialog extends ChooseDialog<Integer> {

    private static final Integer[] googleMapType = {
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_HYBRID
    };

    private MapTypeDialog(Context context, Integer checkedItem, OnSelectedItemListener<Integer> onSelectedItemListener) {
        super(context, googleMapType, checkedItem, R.array.type_map, onSelectedItemListener);
        setTitle(context.getResources().getString(R.string.type_map));
    }

    public static MapTypeDialog create(Context context, int checkedItem, OnSelectedItemListener onSelectedItemListener)    {
        return new MapTypeDialog(context, checkedItem, onSelectedItemListener);
    }
}
