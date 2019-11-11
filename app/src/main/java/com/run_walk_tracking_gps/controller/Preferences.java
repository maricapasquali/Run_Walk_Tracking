package com.run_walk_tracking_gps.controller;

import android.content.Context;
import android.content.SharedPreferences;


import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;

import org.json.JSONException;
import org.json.JSONObject;


public class Preferences {

    private static final String PREFERENCE_USER_LOGGED="User_Logged";
    private static final String PREFERENCE_SETTINGS_USERS ="Settings_App_For_User";
    private static final String PREFERENCE_USER_IMAGE ="Images_Profile";

    private static final String LAST_USER_LOGGED ="last_user_logged";
    private static final String IS_JUST_LOGGED ="is_just_logged";

    // LOGIN
    private static SharedPreferences getSharedPreferencesUserLogged(Context context){
        return context.getSharedPreferences(PREFERENCE_USER_LOGGED, Context.MODE_PRIVATE);
    }

    public static boolean isJustUserLogged(Context context){
        return getSharedPreferencesUserLogged(context).getBoolean(IS_JUST_LOGGED, false);
    }

    public static void setUserLogged(Context context, String id_user){
        getSharedPreferencesUserLogged(context).edit()
                                    .putString(LAST_USER_LOGGED, id_user)
                                    .putBoolean(IS_JUST_LOGGED, true)
                                    .apply();
    }

    public static void unSetUserLogged(Context context){
        getSharedPreferencesUserLogged(context).edit()
                .putBoolean(IS_JUST_LOGGED, false)
                .apply();
    }

    //IMAGE
    public static SharedPreferences getSharedPreferencesImagesUser(Context context){
        return context.getSharedPreferences(PREFERENCE_USER_IMAGE, Context.MODE_PRIVATE);
    }

    public static void setImageProfile(Context context, int id_user, String image_encode){
        getSharedPreferencesImagesUser(context).edit().putString(String.valueOf(id_user), image_encode).apply();
    }

    // SETTING
    public static SharedPreferences getSharedPreferencesSettingUserLogged(Context context){
        return context.getSharedPreferences(PREFERENCE_SETTINGS_USERS, Context.MODE_PRIVATE);
    }

    public static String getIdUserLogged(Context context){
        return getSharedPreferencesUserLogged(context).getString(LAST_USER_LOGGED, "");
    }

    public static JSONObject getAppJsonUserLogged(Context context, String id_user) throws JSONException {
        return new JSONObject(getSharedPreferencesSettingUserLogged(context).getString(id_user, ""));
    }

    public static JSONObject getSettingsJsonUserLogged(Context context, String id_user) throws JSONException {
        return (JSONObject) Preferences.getAppJsonUserLogged(context, id_user).get(FieldDataBase.SETTINGS.toName());
    }

    private static String getUnitDefault(Context context, String measure) throws JSONException{
        JSONObject settings = Preferences.getSettingsJsonUserLogged(context, Preferences.getIdUserLogged(context));
        return (String) ((JSONObject)((JSONObject)settings.get(FieldDataBase.UNIT_MEASURE.toName()))
                .get(measure)).get(FieldDataBase.UNIT.toName());
    }

    public static String getUnitHeightDefault(Context context) throws JSONException{
        return getUnitDefault(context, FieldDataBase.HEIGHT.toName());
    }

    public static String getUnitWeightDefault(Context context) throws JSONException{
        return getUnitDefault(context, FieldDataBase.WEIGHT.toName());
    }

    public static String getUnitDistanceDefault(Context context) throws JSONException{
        return getUnitDefault(context, FieldDataBase.DISTANCE.toName());
    }

    public static String getUnitEnergyDefault(Context context) throws JSONException{
        return getUnitDefault(context, FieldDataBase.ENERGY.toName());
    }

    public static String getUnitMiddleSpeedDefault(Context context) throws JSONException{
        return getUnitDefault(context, FieldDataBase.DISTANCE.toName())+"/h";
    }

    public static void writeSettingsIntoSharedPreferences(Context context, JSONObject json) throws JSONException {
        final String id_user = String.valueOf((int) ((JSONObject) json.get("user")).get("id_user"));

        // MEMORIZZO ID DELL'USER CHE HA EFFETTUATO L'ACCESSO
        final boolean isJustLogged = Preferences.isJustUserLogged(context);
        if (!isJustLogged) Preferences.setUserLogged(context, id_user);

        // MEMORIZZO LE IMPOSTRAZIONI DELLA APPLICAZIONE
        final JSONObject app = (JSONObject) json.get("app");
        SharedPreferences sharedPreferences = Preferences.getSharedPreferencesSettingUserLogged(context);
        String appSharedPreferencesJson = sharedPreferences.getString(id_user, "");
        if (appSharedPreferencesJson.isEmpty() || !(app.equals(new JSONObject(appSharedPreferencesJson)))) {
            sharedPreferences.edit()
                    .putString(id_user, app.toString())
                    .apply();
        }
    }

}
