package com.run_walk_tracking_gps.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.location.LocationRequest;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.controller.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationUtilities {

    private static final String TAG = LocationUtilities.class.getName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static boolean requestPermissions = false;

    public static class Request {

        private static boolean isDefault(Context context, boolean now) {
            final String id_user = Preferences.getIdUserLogged(context);
            try {
                boolean isJustEnabled;
                isJustEnabled = Preferences.getSettingsJsonUserLogged(context,id_user).getBoolean(FieldDataBase.LOCATION.toName());
                return isJustEnabled==now;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        public static boolean setLocationOnOff(Context context, int id_user, Response.Listener<JSONObject> responseJsonListener) {
            boolean isEnable = isGpsEnable(context);
            boolean isChange = !isDefault(context, isEnable);
            if(isChange){
                try {
                    JSONObject bodyJson = new JSONObject();
                    bodyJson.put(FieldDataBase.ID_USER.toName(), Integer.valueOf(id_user))
                            .put(FieldDataBase.FILTER.toName(), FieldDataBase.LOCATION.toName())
                            .put(FieldDataBase.VALUE.toName(), isEnable);

                    if (!HttpRequest.requestUpdateSetting(context, bodyJson, responseJsonListener)) {
                        Toast.makeText(context, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            return isChange;
        }

    }

    public static boolean isGpsEnable(Context context) {
        return ((LocationManager) context.getSystemService(Service.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static LocationRequest createLocationRequest() {
        return LocationRequest.create()
                              .setInterval(10000)
                              .setFastestInterval(5000)
                              .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public static void setLocationPermission(final Activity activity) {
        Log.d(TAG, activity.getString(R.string.request_location_permissions));
        final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }

    public static void setRequestPermissions(final boolean requestPermissions){
        LocationUtilities.requestPermissions = requestPermissions;
    }

    public static boolean needPermission(){
        return requestPermissions;
    }
}


