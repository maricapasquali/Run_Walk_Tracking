package com.run_walk_tracking_gps.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.db.dao.SqlLiteUserDao;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.utilities.CollectionsUtilities;
import com.run_walk_tracking_gps.utilities.DateHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.stream.Stream;


public class Preferences {

    public static class Session{
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
                    .put(NetworkHelper.Constant.DB_EXIST, DaoFactory.existDatabase(context) &&
                            SqlLiteUserDao.create(context).getUser()!=null);
            Log.d(PREFERENCE_SESSION, "session = " + s);
            return s;
        }

        public static int getIdUser(Context context) {
            return getSharedPreferencesSession(context).getInt(UserDescriptor.ID_USER, 0);
        }

        // SETTER SharedPreferences
        public static void setSession(Context context, JSONObject s) {
            getSharedPreferencesSession(context).edit()
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

    public static class VoiceCoach{

        private static final boolean IS_ACTIVE_DEFAULT = true;
        private static final int INTERVAL_DEFAULT = 1;
        private static final boolean PARAMETER_DEFAULT = true;

        private static final String PREFERENCE_COACH = "coach";
        private static final String VOCAL_COACH = "vocal_coach";
        private static final String INTERVAL = "interval";

        private static SharedPreferences getSharedPreferencesCoach(Context context) {
            return context.getSharedPreferences(PREFERENCE_COACH+"_"+Session.getIdUser(context), Context.MODE_PRIVATE);
        }

        public static boolean isActive(Context context) {
            return getSharedPreferencesCoach(context).getBoolean(VOCAL_COACH, IS_ACTIVE_DEFAULT);
        }

        public static boolean isParameterActive(Context context, Measure.Type type) {
            return  getSharedPreferencesCoach(context).getBoolean(type.toString(), PARAMETER_DEFAULT);
        }

        public static int getInterval(Context context) {
            return getSharedPreferencesCoach(context).getInt(INTERVAL, INTERVAL_DEFAULT);
        }

        public static void setVoiceCoach(Context context, boolean onOff) {
            getSharedPreferencesCoach(context).edit().putBoolean(VOCAL_COACH, onOff).apply();
        }

        public static void setParameter(Context context, Measure.Type type, boolean onOff) {
            getSharedPreferencesCoach(context).edit().putBoolean(type.toString(), onOff).apply();
        }

        public static void setInterval(Context context, int min) {
            getSharedPreferencesCoach(context).edit().putInt(INTERVAL, min).apply();
        }
    }

    public static class Music{

        private static final boolean IS_ACTIVE_DEFAULT = false;
        private static final String PREFERENCE_MUSIC = "music";

        private static SharedPreferences getSharedPreferencesCoach(Context context) {
            return context.getSharedPreferences(PREFERENCE_MUSIC, Context.MODE_PRIVATE);
        }

        public static boolean isActive(Context context) {
            String id_user = String.valueOf(Session.getIdUser(context));
            return getSharedPreferencesCoach(context).getBoolean(id_user, IS_ACTIVE_DEFAULT);
        }

        public static void setActive(Context context, boolean isActive) {
            String id_user = String.valueOf(Session.getIdUser(context));
            getSharedPreferencesCoach(context).edit().putBoolean(id_user, isActive).apply();
        }
    }

    public static class MapLocation{
        private static final String PREFERENCE_LOCATION = "map_location";

      //  private static String id = String.valueOf(11);
        // GETTER SharedPreferences
        private static SharedPreferences getSharedPreferencesLocation(Context context) {
            return context.getSharedPreferences(PREFERENCE_LOCATION, Context.MODE_PRIVATE);
        }

        public static void create(Context context){
            String id = String.valueOf(Session.getIdUser(context));
            getSharedPreferencesLocation(context).edit().putString(id, "").apply();
        }

        public static void add(Context context, LatLng location){
            SharedPreferences ss = getSharedPreferencesLocation(context);
            String id = String.valueOf(Session.getIdUser(context));
            ArrayList<LatLng> hs =  CollectionsUtilities.convertStringToListLatLng(ss.getString(id,""));
            hs.add(location);
            ss.edit().clear().putString(id, hs.toString()).apply();
        }

        public static ArrayList<LatLng> get(Context context){
            String id = String.valueOf(Session.getIdUser(context));
            return CollectionsUtilities.convertStringToListLatLng(getSharedPreferencesLocation(context).getString(id,""));
        }

        public static void delete(Context context) {
            String id = String.valueOf(Session.getIdUser(context));
            getSharedPreferencesLocation(context).edit().remove(id).apply();
        }

        public static void addAll(Context context, List<Location> locations) {
            SharedPreferences ss = getSharedPreferencesLocation(context);
            String id = String.valueOf(Session.getIdUser(context));
            ArrayList<LatLng> route = new ArrayList<>();
            locations.forEach(l -> route.add(new LatLng(l.getLatitude(), l.getLongitude())));
            ArrayList<LatLng> hs = CollectionsUtilities.convertStringToListLatLng(ss.getString(id,""));
            hs.addAll(route);
            ss.edit().clear().putString(id, hs.toString()).apply();
            route.clear();
        }
    }

}

