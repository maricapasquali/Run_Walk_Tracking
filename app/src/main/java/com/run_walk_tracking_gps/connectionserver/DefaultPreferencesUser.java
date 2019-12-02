package com.run_walk_tracking_gps.connectionserver;

import android.content.Context;
import android.content.SharedPreferences;

import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.model.enumerations.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class DefaultPreferencesUser {

    private static final String PREFERENCE_USER_LOGGED="User_Logged";
    private static final String PREFERENCE_SETTINGS_USERS ="Settings_App_For_User";
    private static final String PREFERENCE_USER_IMAGE ="Images_Profile";

    private static final String LAST_USER_LOGGED ="last_user_logged";
    private static final String IS_JUST_LOGGED ="is_just_logged";
    private static final String USER ="user";
    private static final String APP ="app";
    private static final String SETTINGS = "settings";
    private static final String UNIT_MEASURE ="unit_measure";


// GET SharedPreferences
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
        return (JSONObject) DefaultPreferencesUser.getAppJsonUserLogged(context, id_user).get(SETTINGS);
    }

    public static String getUnitDefault(Context context, String measure) throws JSONException {
        if(DefaultPreferencesUser.isJustUserLogged(context)){
            JSONObject settings = DefaultPreferencesUser.getSettingsJsonUserLogged(context, getIdUserLogged(context));
            return ((JSONObject)settings.get(UNIT_MEASURE)).getString(measure);
        }
        return null;
    }

    public static String getUnitHeightDefault(Context context) {
        final String default_measure = context.getString(Measure.Type.HEIGHT.getMeasureUnitDefault().getStrId());
        try {
            final String unitHeight = getUnitDefault(context, FieldDataBase.HEIGHT.toName());
            return unitHeight==null ? default_measure : unitHeight;
        }catch (JSONException e ){
            return default_measure;
        }
    }

    public static String getUnitWeightDefault(Context context) {
        final String default_measure = context.getString(Measure.Type.WEIGHT.getMeasureUnitDefault().getStrId());
        try {
            final String unitWeight = getUnitDefault(context, FieldDataBase.WEIGHT.toName());
            return unitWeight==null ? default_measure : unitWeight;
        }catch (JSONException e ){
            return default_measure;
        }
    }

    public static String getUnitDistanceDefault(Context context) {
        final String default_measure = context.getString(Measure.Type.DISTANCE.getMeasureUnitDefault().getStrId());
        try {
            final String unitDistance = getUnitDefault(context, FieldDataBase.DISTANCE.toName());
            return unitDistance==null ? default_measure : unitDistance;
        }catch (JSONException e ){
            return default_measure;
        }
    }

    public static String getUnitEnergyDefault(Context context) throws JSONException{

        final String default_measure = context.getString(Measure.Type.ENERGY.getMeasureUnitDefault().getStrId());
        try {
            final String unitEnergy = getUnitDefault(context, FieldDataBase.ENERGY.toName());
            return unitEnergy==null ? default_measure : unitEnergy;
        }catch (JSONException e ){
            return default_measure;
        }
    }

    public static String getUnitMiddleSpeedDefault(Context context) throws JSONException{
        return getUnitDefault(context, FieldDataBase.DISTANCE.toName())+"/h";
    }

    public static Target getTargetDefault(Context context) {
        try {
           return Target.valueOf(getSettingsJsonUserLogged(context,
                   getIdUserLogged(context)).getString(FieldDataBase.TARGET.toName()));
        }catch (JSONException e){
           e.printStackTrace();
        }
        return null;
    }

    public static Sport getSportDefault(Context context){
        try {
            return Sport.valueOf(getSettingsJsonUserLogged(context, getIdUserLogged(context)).getString(FieldDataBase.SPORT.toName()));
        }catch (JSONException e){
            e.printStackTrace();
        }
       return null;
    }

    public static String getLanguageDefault(Context context) throws JSONException {
        return ((JSONObject)getSettingsJsonUserLogged(context, getIdUserLogged(context))).getString(FieldDataBase.LANGUAGE.toName());
    }

 // SET SharedPreferences
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
        JSONObject user = ((JSONObject)response.get(USER));
        String image_encode = user.getString(FieldDataBase.IMG_ENCODE.toName());
        int id_user = (int)user.get(FieldDataBase.ID_USER.toName());

        if(!image_encode.equals("null") || !image_encode.equals(getSharedPreferencesImagesUser(context).getString(String.valueOf(id_user), "null")))
            getSharedPreferencesImagesUser(context).edit().putString(String.valueOf(id_user), image_encode).apply();
    }

    public static void setSettings(Context context, JSONObject json) throws JSONException {
        final String id_user = String.valueOf((int) ((JSONObject) json.get(USER)).get(FieldDataBase.ID_USER.toName()));

        // MEMORIZZO ID DELL'USER CHE HA EFFETTUATO L'ACCESSO
        final boolean isJustLogged = DefaultPreferencesUser.isJustUserLogged(context);
        if (!isJustLogged) DefaultPreferencesUser.setUserLogged(context, id_user);

        // MEMORIZZO LE IMPOSTRAZIONI DELLA APPLICAZIONE
        final JSONObject app = (JSONObject) json.get(APP);
        SharedPreferences sharedPreferences = DefaultPreferencesUser.getSharedPreferencesSettingUserLogged(context);
        String appSharedPreferencesJson = sharedPreferences.getString(id_user, "");
        if (appSharedPreferencesJson.isEmpty() || !(app.equals(new JSONObject(appSharedPreferencesJson)))) {
            sharedPreferences.edit()
                    .putString(id_user, app.toString())
                    .apply();
        }
    }

    public static void setLanguage(Context context, String language) throws JSONException {
        final String id_user = getIdUserLogged(context);
        final JSONObject appJson = getAppJsonUserLogged(context, id_user);
        final JSONObject settingsJson = (JSONObject)appJson.get(SETTINGS);
        settingsJson.put(FieldDataBase.LANGUAGE.toName(), language);

        appJson.put(SETTINGS, settingsJson);
        DefaultPreferencesUser.getSharedPreferencesSettingUserLogged(context).edit().putString(id_user, appJson.toString()).apply();
    }

    public static void setUnitMeasure(Context context, String measure, String unit) throws JSONException {
        final String id_user = getIdUserLogged(context);
        final JSONObject appJson = getAppJsonUserLogged(context, id_user);
        final JSONObject settingsJson = (JSONObject)appJson.get(SETTINGS);
        final JSONObject unit_measure = ((JSONObject)settingsJson.get(UNIT_MEASURE));

        unit_measure.put(measure, unit);
        settingsJson.put(UNIT_MEASURE, unit_measure);
        appJson.put(SETTINGS, settingsJson);

        DefaultPreferencesUser.getSharedPreferencesSettingUserLogged(context).edit().putString(id_user, appJson.toString()).apply();

    }
    // X TARGET O SPORT
    public static void updateSpinnerSettings(Context context, String type, String value) throws JSONException {

        final String id_user = getIdUserLogged(context);
        final JSONObject appJson = getAppJsonUserLogged(context, id_user);
        final JSONObject settingsJson = (JSONObject)appJson.get(SETTINGS);

        settingsJson.put(type, value);
        appJson.put(SETTINGS, settingsJson);
        DefaultPreferencesUser.getSharedPreferencesSettingUserLogged(context).edit().putString(id_user, appJson.toString()).apply();
    }

}
