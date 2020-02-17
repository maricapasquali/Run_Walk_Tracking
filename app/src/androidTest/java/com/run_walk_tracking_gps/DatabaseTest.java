package com.run_walk_tracking_gps;

import android.content.Context;
import android.util.Log;

import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.SqlLiteSettingsDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteStatisticsDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteUserDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteWorkoutDao;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.model.Workout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import androidx.test.InstrumentationRegistry;

public class DatabaseTest {
    private Context context= InstrumentationRegistry.getTargetContext();


    @Before
    public void insertUser1() throws JSONException {
        Assert.assertTrue(SqlLiteUserDao.create(context).delete());
       Preferences.Session.setSession(context, new JSONObject(" {\n" +
               "    \"id_user\": 27,\n" +
               "    \"token\": \"kKC72gfy8h0HOUHJ1sdCrDzWvKfTsf7xfqGLCX6BnL0IdqdETudYZhfWSEytxu3iXYoj22hMO7ugKoirRoFC4NqIfcULnWq6qHY0\",\n" +
               "    \"last_update\": 1577387391\n" +
               "  }"));
        boolean result = SqlLiteUserDao.create(context).insert(new JSONObject(" {\n" +
                "      \"id_user\": 27,\n" +
                "      \"name\": \"Mario\",\n" +
                "      \"last_name\": \"Rossi\",\n" +
                "      \"gender\": \"MALE\",\n" +
                "      \"birth_date\": \"1996-02-11\",\n" +
                "      \"email\": \"mariorossi@gmail.com\",\n" +
                "      \"phone\": \"3333333334\",\n" +
                "      \"city\": \"Milano\",\n" +
                "      \"height\": 1.7,\n" +
                "      \"username\": \"mariorossi$1\",\n" +
                "      \"img_encode\": \"http:://\"\n" +
                "    }"));
        Assert.assertTrue(result);

       result = SqlLiteSettingsDao.create(context).insert(new JSONObject("{\n" +
               "      \"sport\": \"WALK\",\n" +
               "      \"target\": \"LOSE_WEIGHT\",\n" +
               "      \"unit_measure\": {\n" +
               "        \"energy\": \"KILO_CALORIES\",\n" +
               "        \"distance\": \"KILOMETER\",\n" +
               "        \"weight\": \"KILOGRAM\",\n" +
               "        \"height\": \"METER\"\n" +
               "      }\n" +
               "    }"));
       Assert.assertTrue(result);

       result = SqlLiteStatisticsDao.SqlLiteWeightDao.create(context).replaceAll(new JSONArray(
               "[\n" +
                       "      {\n" +
                       "      \"id_weight\": 1,\n" +
                       "        \"date\": \"2019-12-26\",\n" +
                       "        \"value\": 75\n" +
                       "      },\n" +
                       "{\n" +
                       "      \"id_weight\": 2,\n" +
                       "        \"date\": \"2019-12-30\",\n" +
                       "        \"value\": 74.5\n" +
                       "      }\n" +
                       "]"));
       Assert.assertTrue(result);

        /*result = SqlLiteStatisticsDao.createWeightDao(context).insert(new JSONObject(
                "      {\n" +

                        "        \"date\": \"2019-12-20\",\n" +
                        "        \"value\": 74.5\n" +
                        "      }"));
        Assert.assertTrue(result);*/

       result = SqlLiteWorkoutDao.create(context).replaceAll(new JSONArray(
               "[\n" +
                       "{\n" +
                       "\t\t\"id_workout\":6,\n" +
                       "\t\t\"map_route\":\"[lat/lng: (44.3505994,11.7040864), lat/lng: (44.3506125,11.7041001), lat/lng: (44.350618,11.7041033), lat/lng: (44.3506169,11.7041029), lat/lng: (44.3506428,11.7040976), lat/lng: (44.3506022,11.7040873), lat/lng: (44.3506032,11.7040876), lat/lng: (44.3506183,11.704094), lat/lng: (44.3506081,11.7040895), lat/lng: (44.3506039,11.7040877)]\",\n" +
                       "\t\t\"date\":\"1577383925\",\n" +
                       "\t\t\"duration\": 1500,\n" +
                       "\t\t\"distance\": 12.2,\n" +
                       "\t\t\"calories\":33,\n" +
                       "\t\t\"sport\": \"RUN\"\n" +
                       "\t},"+
                       "{\n" +
                       "\t\t\"id_workout\":7,\n" +
                       "\t\t\"map_route\":\null,\n" +
                       "\t\t\"date\":\"1577383925\",\n" +
                       "\t\t\"duration\": 3500,\n" +
                       "\t\t\"distance\": 45.0,\n" +
                       "\t\t\"calories\":400,\n" +
                       "\t\t\"sport\": \"RUN\"\n" +
                       "\t}"+
                       "]"));
       Assert.assertTrue(result);


 /*    result = SqlLiteWorkoutDao.create(context).insert(new JSONObject(
       "{\n" +
               "\t\t\"id_workout\":6,\n" +
               "\t\t\"map_route\":\"[lat/lng: (44.3505994,11.7040864), lat/lng: (44.3506125,11.7041001), lat/lng: (44.350618,11.7041033), lat/lng: (44.3506169,11.7041029), lat/lng: (44.3506428,11.7040976), lat/lng: (44.3506022,11.7040873), lat/lng: (44.3506032,11.7040876), lat/lng: (44.3506183,11.704094), lat/lng: (44.3506081,11.7040895), lat/lng: (44.3506039,11.7040877)]\",\n" +
               "\t\t\"date\":\"1577383925\",\n" +
               "\t\t\"duration\": 1500,\n" +
               "\t\t\"distance\": 12.2,\n" +
               "\t\t\"calories\":33,\n" +
               "\t\t\"sport\": \"RUN\"\n" +
               "\t}"));
        Assert.assertTrue(result);*/

    }

    @Test
    public void select() throws JSONException {

        Log.e("USER", SqlLiteUserDao.create(context).getUser().toString());
        Log.e("SETTINGS", SqlLiteSettingsDao.create(context).getSettings().toString());
        Log.e("WEIGHTS", SqlLiteStatisticsDao.create(context).getAll(Measure.Type.WEIGHT).toString());
        Log.e("WORKOUTS - JSON", SqlLiteWorkoutDao.create(context).getAll().toString());
        Log.e("WORKOUTS - LIST", Workout.createList(context, SqlLiteWorkoutDao.create(context).getAll()).toString());


        Log.e("DISTACE - JSON", SqlLiteStatisticsDao.create(context).getAll(Measure.Type.DISTANCE).toString());
        Log.e("DISTACE - LIST", StatisticsData.createList(context, Measure.Type.DISTANCE, SqlLiteStatisticsDao.create(context).getAll(Measure.Type.DISTANCE)).toString());



        /*Log.e("DURATION", SqlLiteStatisticsDao.create(context).getAll(Measure.Type.DURATION).toString());
        Log.e("MIDDLE_SPEED", SqlLiteStatisticsDao.create(context).getAll(Measure.Type.MIDDLE_SPEED).toString());
        Log.e("ENERGY", SqlLiteStatisticsDao.create(context).getAll(Measure.Type.ENERGY).toString());*/
    }

    @After
    public void delete(){
        Assert.assertTrue(SqlLiteUserDao.create(context).delete());
    }
}
