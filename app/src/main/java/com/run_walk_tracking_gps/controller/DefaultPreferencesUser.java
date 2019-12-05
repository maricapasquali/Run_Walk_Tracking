package com.run_walk_tracking_gps.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.model.enumerations.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class DefaultPreferencesUser {

    private static final String PREFERENCE_USER_LOGGED="User_Logged";
    private static final String PREFERENCE_SETTINGS_USERS ="Settings_App_For_User";
    private static final String PREFERENCE_USER_IMAGE ="Images_Profile";

    private static final String LAST_USER_LOGGED ="last_user_logged";
    private static final String IS_JUST_LOGGED ="is_just_logged";


// GETTER SharedPreferences
    private static SharedPreferences getSharedPreferencesUserLogged(Context context){
        return context.getSharedPreferences(PREFERENCE_USER_LOGGED, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getSharedPreferencesSettingUserLogged(Context context){
        return context.getSharedPreferences(PREFERENCE_SETTINGS_USERS, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSharedPreferencesImagesUser(Context context){
        return context.getSharedPreferences(PREFERENCE_USER_IMAGE, Context.MODE_PRIVATE);
    }

    private static JSONObject getAppJsonUserLogged(Context context, String id_user) throws JSONException {
        return new JSONObject(getSharedPreferencesSettingUserLogged(context).getString(id_user, ""));
    }

    public static boolean isJustUserLogged(Context context){
        return getSharedPreferencesUserLogged(context).getBoolean(IS_JUST_LOGGED, false);
    }

    public static String getIdUserLogged(Context context){
        return getSharedPreferencesUserLogged(context).getString(LAST_USER_LOGGED, "");
    }

    public static JSONObject getSettingsJsonUserLogged(Context context, String id_user) throws JSONException {
        return (JSONObject) DefaultPreferencesUser.getAppJsonUserLogged(context, id_user).get(HttpRequest.Constant.SETTINGS);
    }

    public static String getUnitDefault(Context context, String measure) {
        try {
            if(DefaultPreferencesUser.isJustUserLogged(context)){
                JSONObject settings = DefaultPreferencesUser.getSettingsJsonUserLogged(context, getIdUserLogged(context));
                return ((JSONObject)settings.get(HttpRequest.Constant.UNIT_MEASURE)).getString(measure);
            }
        }catch (JSONException e ){
            e.printStackTrace();
        }
        return null;
    }

    public static Measure.Unit getUnitHeightDefault(Context context) {
        try {
            return Measure.Unit.valueOf(Objects.requireNonNull(getUnitDefault(context, HttpRequest.Constant.HEIGHT)));
        }catch (IllegalArgumentException | NullPointerException e){
            return Measure.Type.HEIGHT.getMeasureUnitDefault();
        }
    }

    public static Measure.Unit getUnitWeightDefault(Context context) {
        try {
            return Measure.Unit.valueOf(Objects.requireNonNull(getUnitDefault(context, HttpRequest.Constant.WEIGHT)));
        }catch (IllegalArgumentException | NullPointerException e){
            return Measure.Type.WEIGHT.getMeasureUnitDefault();
        }
    }

    public static Measure.Unit getUnitDistanceDefault(Context context) {
        try {
            return Measure.Unit.valueOf(Objects.requireNonNull(getUnitDefault(context, HttpRequest.Constant.DISTANCE)));
        }catch (IllegalArgumentException | NullPointerException e){
            return Measure.Type.DISTANCE.getMeasureUnitDefault();
        }
    }

    public static Measure.Unit getUnitMiddleSpeedDefault(Context context) {
        Measure.Unit unitDistance = getUnitDistanceDefault(context);
        switch (unitDistance){
            case KILOMETER:
                return Measure.Unit.KILOMETER_PER_HOUR;
            case MILE:
                return Measure.Unit.MILE_PER_HOUR;
            default:
                return null;
        }
    }

    public static Measure.Unit getUnitEnergyDefault(Context context) {
        try {
            return Measure.Unit.valueOf(Objects.requireNonNull(getUnitDefault(context, HttpRequest.Constant.ENERGY)));
        }catch (IllegalArgumentException | NullPointerException e){
            return Measure.Type.ENERGY.getMeasureUnitDefault();
        }
    }

    public static Target getTargetDefault(Context context) {
        try {
           return Target.valueOf(getSettingsJsonUserLogged(context,
                   getIdUserLogged(context)).getString(HttpRequest.Constant.TARGET));
        }catch (JSONException | IllegalArgumentException e){
           e.printStackTrace();
        }
        return null;
    }

    public static Sport getSportDefault(Context context){
        try {
            return Sport.valueOf(getSettingsJsonUserLogged(context, getIdUserLogged(context)).getString(HttpRequest.Constant.SPORT));
        }catch (JSONException | IllegalArgumentException e){
            e.printStackTrace();
        }
       return null;
    }

 // SETTER SharedPreferences
    private static void setUserLogged(Context context, String id_user){
        getSharedPreferencesUserLogged(context).edit()
                .putString(LAST_USER_LOGGED, id_user)
                .putBoolean(IS_JUST_LOGGED, true)
                .apply();
    }

    public static void deleteUser(Context context, String id_user){
        getSharedPreferencesSettingUserLogged(context).edit().remove(id_user).apply();
        getSharedPreferencesImagesUser(context).edit().remove(id_user).apply();
    }

    public static void unSetUserLogged(Context context){
        getSharedPreferencesUserLogged(context).edit()
                .putBoolean(IS_JUST_LOGGED, false)
                .apply();
    }

    public static void setImage(Context context, int id_user, String image_encode){
        if(image_encode!=null)
            getSharedPreferencesImagesUser(context).edit().putString(String.valueOf(id_user), image_encode).apply();
    }

    public static void setImage(Context context, JSONObject response) throws JSONException {
        JSONObject user = ((JSONObject)response.get(HttpRequest.Constant.USER));
        String image_encode = user.getString(HttpRequest.Constant.IMG_ENCODE);
        int id_user = (int)user.get(HttpRequest.Constant.ID_USER);

        if(!image_encode.equals("null") || !image_encode.equals(getSharedPreferencesImagesUser(context).getString(String.valueOf(id_user), "null")))
            getSharedPreferencesImagesUser(context).edit().putString(String.valueOf(id_user), image_encode).apply();
    }

    public static void setSettings(Context context, JSONObject json) throws JSONException {
        final String id_user = String.valueOf((int) ((JSONObject) json.get(HttpRequest.Constant.USER)).get(HttpRequest.Constant.ID_USER));

        // MEMORIZZO ID DELL'USER CHE HA EFFETTUATO L'ACCESSO
        final boolean isJustLogged = DefaultPreferencesUser.isJustUserLogged(context);
        if (!isJustLogged) DefaultPreferencesUser.setUserLogged(context, id_user);

        // MEMORIZZO LE IMPOSTRAZIONI DELLA APPLICAZIONE
        final JSONObject app = (JSONObject) json.get(HttpRequest.Constant.APP);
        SharedPreferences sharedPreferences = DefaultPreferencesUser.getSharedPreferencesSettingUserLogged(context);
        String appSharedPreferencesJson = sharedPreferences.getString(id_user, "");
        if (appSharedPreferencesJson.isEmpty() || !(app.equals(new JSONObject(appSharedPreferencesJson)))) {
            sharedPreferences.edit()
                    .putString(id_user, app.toString())
                    .apply();
        }
    }

    public static void setUnitMeasure(Context context, String measure, Measure.Unit unit) throws JSONException {
        final String id_user = getIdUserLogged(context);
        final JSONObject appJson = getAppJsonUserLogged(context, id_user);
        final JSONObject settingsJson = (JSONObject)appJson.get(HttpRequest.Constant.SETTINGS);
        final JSONObject unit_measure = ((JSONObject)settingsJson.get(HttpRequest.Constant.UNIT_MEASURE));

        unit_measure.put(measure, unit);
        settingsJson.put(HttpRequest.Constant.UNIT_MEASURE, unit_measure);
        appJson.put(HttpRequest.Constant.SETTINGS, settingsJson);

        DefaultPreferencesUser.getSharedPreferencesSettingUserLogged(context).edit().putString(id_user, appJson.toString()).apply();

    }
    // X TARGET O SPORT
    public static void updateSpinnerSettings(Context context, String type, String value) throws JSONException {

        final String id_user = getIdUserLogged(context);
        final JSONObject appJson = getAppJsonUserLogged(context, id_user);
        final JSONObject settingsJson = (JSONObject)appJson.get(HttpRequest.Constant.SETTINGS);

        settingsJson.put(type, value);
        appJson.put(HttpRequest.Constant.SETTINGS, settingsJson);
        DefaultPreferencesUser.getSharedPreferencesSettingUserLogged(context).edit().putString(id_user, appJson.toString()).apply();
    }

}
