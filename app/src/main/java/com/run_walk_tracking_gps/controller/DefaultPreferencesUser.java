package com.run_walk_tracking_gps.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.utilities.DateHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class DefaultPreferencesUser {

    private static final String PREFERENCE_SESSION = "session";

    // GETTER SharedPreferences
    private static SharedPreferences getSharedPreferencesSession(Context context) {
        return context.getSharedPreferences(PREFERENCE_SESSION, Context.MODE_PRIVATE);
    }

    public static boolean isLogged(Context context){
        final SharedPreferences session = getSharedPreferencesSession(context);
        return  !session.getString(NetworkHelper.Constant.TOKEN, "").isEmpty() &&
                session.getLong(NetworkHelper.Constant.LAST_UPDATE, 0L)!=0L &&
                session.getInt(UserDescriptor.ID_USER, 0)!=0;
    }


    public static JSONObject getSession(Context context) throws JSONException {
        final SharedPreferences session = getSharedPreferencesSession(context);

        JSONObject s = new JSONObject()
                .put(NetworkHelper.Constant.TOKEN, session.getString(NetworkHelper.Constant.TOKEN, ""))
                .put(NetworkHelper.Constant.LAST_UPDATE, session.getLong(NetworkHelper.Constant.LAST_UPDATE, 0))
                .put(NetworkHelper.Constant.DB_EXIST, DaoFactory.existDatabase(context));
        Log.d(PREFERENCE_SESSION, "session = " + s);
        return s;
    }

    public static int getIdUser(Context context) {
        return getSharedPreferencesSession(context).getInt(UserDescriptor.ID_USER, 0);
    }

    // SETTER SharedPreferences
    public static void setSession(Context context, JSONObject s) {
        final SharedPreferences session = getSharedPreferencesSession(context);
        session.edit()
                //.putString(Constant.DEVICE, s.optString(Constant.DEVICE))
                .putString(NetworkHelper.Constant.TOKEN, s.optString(NetworkHelper.Constant.TOKEN))
                .putLong(NetworkHelper.Constant.LAST_UPDATE, s.optLong(NetworkHelper.Constant.LAST_UPDATE))
                .putInt(NetworkHelper.Constant.ID_USER, s.optInt(NetworkHelper.Constant.ID_USER))
                .apply();
    }

    public static void update(Context context) {
        final SharedPreferences session = getSharedPreferencesSession(context);
        session.edit()
                .putLong(NetworkHelper.Constant.LAST_UPDATE, DateHelper.create(context).getCurrentDate())
                .apply();
    }

    public static void logout(Context context) {
        final SharedPreferences session = getSharedPreferencesSession(context);
        session.edit()
                .remove(NetworkHelper.Constant.TOKEN)
                .remove(NetworkHelper.Constant.LAST_UPDATE)
                .remove(NetworkHelper.Constant.ID_USER)
                .apply();
    }
}

