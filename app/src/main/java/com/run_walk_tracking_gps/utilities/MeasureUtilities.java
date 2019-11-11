package com.run_walk_tracking_gps.utilities;

import android.content.Context;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;

import org.json.JSONException;

public class MeasureUtilities {

    public static String heightStr(Context context, double value) throws JSONException {
        return value + context.getString(R.string.space) + Preferences.getUnitHeightDefault(context);
    }

    public static String weightStr(Context context, double value) throws JSONException {
        return value + context.getString(R.string.space) + Preferences.getUnitWeightDefault(context);
    }

    public static String distanceStr(Context context, double value) throws JSONException {
        return value + context.getString(R.string.space) + Preferences.getUnitDistanceDefault(context);
    }

    public static String middleSpeedStr(Context context, double value) throws JSONException {
        return value + context.getString(R.string.space) + Preferences.getUnitMiddleSpeedDefault(context);
    }

    public static String energySpeedStr(Context context, double value) throws JSONException {
        return value + context.getString(R.string.space) + Preferences.getUnitMiddleSpeedDefault(context);
    }
}
