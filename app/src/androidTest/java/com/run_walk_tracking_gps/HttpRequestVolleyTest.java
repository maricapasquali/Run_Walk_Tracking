package com.run_walk_tracking_gps;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.model.enumerations.Target;
import com.run_walk_tracking_gps.utilities.CryptographicHashFunctions;
import com.run_walk_tracking_gps.utilities.DateHelper;

import org.json.JSONException;
import org.json.JSONObject;


import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

public class HttpRequestVolleyTest {

    private static final String TAG = HttpRequestVolleyTest.class.getName();
    @Test
    public void requestSignUpVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {
            //bodyJson.put(FieldDataBase.ID_USER.toName(), 21);
            bodyJson.put( FieldDataBase.IMG_ENCODE.toName(), "SFSFISDIGN");
            bodyJson.put( FieldDataBase.NAME.toName(), "fabio");
            bodyJson.put( FieldDataBase.LAST_NAME.toName(), "cani");
            bodyJson.put( FieldDataBase.GENDER.toName(), Gender.MALE);
            bodyJson.put( FieldDataBase.BIRTH_DATE.toName(), Calendar.getInstance().getTime());
            bodyJson.put( FieldDataBase.EMAIL.toName(), "marica@gmail.com");
            bodyJson.put( FieldDataBase.CITY.toName(), "Roma");
            bodyJson.put( FieldDataBase.PHONE.toName(), "324242342342");
            bodyJson.put( FieldDataBase.LANGUAGE.toName(),  Locale.getDefault().getDisplayLanguage());
            bodyJson.put( FieldDataBase.WEIGHT.toName(), 50.6);
            bodyJson.put( FieldDataBase.HEIGHT.toName(), 1.66);
            bodyJson.put( FieldDataBase.TARGET.toName(), Target.LOSE_WEIGHT);
            bodyJson.put( FieldDataBase.USERNAME.toName(), "cacjas");
            bodyJson.put( FieldDataBase.PASSWORD.toName(), CryptographicHashFunctions.md5("fino"));

            if(!HttpRequest.requestSignUp(context, bodyJson, this::responseServer)){
                Log.e(TAG, context.getString(R.string.internet_not_available));
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

   // @Test
    public void requestSignInVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {
            bodyJson.put( FieldDataBase.USERNAME.toName(), "marioRossi$1");
            //bodyJson.put( FieldDataBase.NAME.toName(), "Mario");
            //bodyJson.put( FieldDataBase.LAST_NAME.toName(), "Rossi");

            HttpRequest.requestSignIn(context, bodyJson,this::responseServer);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    //@Test
    public void requestUserInfoVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {
            bodyJson.put( FieldDataBase.ID_USER.toName(), 27);
            //bodyJson.put( FieldDataBase.NAME.toName(), "Mario");

            HttpRequest.requestUserInformation(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    //@Test
    public void requestUpdateUserInfoVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {

            bodyJson.put( FieldDataBase.ID_USER.toName(), 27);
            //bodyJson.put( FieldDataBase.WEIGHT.toName(), "Kira");
            HttpRequest.requestDelayedUpdateUserInformation(context, bodyJson,this::responseServer, null);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    //@Test
    public void requestDeleteUserInfoVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {

           // bodyJson.put( FieldDataBase.NAME.toName(), "Mario");
            bodyJson.put( FieldDataBase.ID_USER.toName(), 27);

            HttpRequest.requestDeleteUser(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    //@Test
    public void requestUpdatePasswordVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {

            //bodyJson.put( FieldDataBase.NAME.toName(), "Mario");
            bodyJson.put( FieldDataBase.ID_USER.toName(), 27);

            HttpRequest.requestUpdatePassword(context, bodyJson,this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    //@Test
    public void requestUpdateSettingVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {

            //bodyJson.put( FieldDataBase.NAME.toName(), "Mario");
            bodyJson.put( FieldDataBase.ID_USER.toName(), 27);

            HttpRequest.requestUpdateSetting(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    //@Test
    public void requestAllWorkoutsVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {

            //bodyJson.put( FieldDataBase.NAME.toName(), "Mario");
            bodyJson.put( FieldDataBase.ID_USER.toName(), 27);

            HttpRequest.requestWorkouts(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

   //@Test
    public void requestNewWorkoutVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {

            //bodyJson.put( FieldDataBase.NAME.toName(), "Mario");
            bodyJson.put( FieldDataBase.ID_USER.toName(), 27);
            bodyJson.put( FieldDataBase.DATE.toName(), DateHelper.getCalendar().getTime());
            bodyJson.put( FieldDataBase.ID_SPORT.toName(), 2);
            bodyJson.put( FieldDataBase.DURATION.toName(), 27);

            HttpRequest.requestNewWorkout(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    //@Test
    public void requestUpdateWorkoutVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {


            bodyJson.put( FieldDataBase.ID_WORKOUT.toName(), 17);
            bodyJson.put( FieldDataBase.DISTANCE.toName(), 7.0);

            HttpRequest.requestUpdateWorkout(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

   // @Test
    public void requestDeleteWorkoutVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {


            bodyJson.put( FieldDataBase.ID_WORKOUT.toName(), 17);
            //bodyJson.put( FieldDataBase.DISTANCE.toName(), 7.0);

            HttpRequest.requestDeleteWorkout(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    //@Test
    public void requestStatisticsVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {

            bodyJson.put( FieldDataBase.FILTER.toName(), "weight");
            bodyJson.put( FieldDataBase.ID_USER.toName(), 27);
            //bodyJson.put( FieldDataBase.DISTANCE.toName(), 7.0);

            HttpRequest.requestStatistics(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    //@Test
    public void requestNewWeightVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {

            bodyJson.put( FieldDataBase.VALUE.toName(), 70.2);
            bodyJson.put( FieldDataBase.DATE.toName(), "2019-10-29");
            bodyJson.put( FieldDataBase.ID_USER.toName(), 39);


            HttpRequest.requestNewWeight(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    //@Test
    public void requestUpdateWeightVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {

            // bodyJson.put( FieldDataBase.FILTER.toName(), "weight");
            bodyJson.put( FieldDataBase.ID_WEIGHT.toName(),28);
            bodyJson.put( FieldDataBase.VALUE.toName(),70);


            HttpRequest.requestUpdateWeight(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    //@Test
    public void requestDeleteWeightVolley(){
        final Context context = InstrumentationRegistry.getTargetContext();

        final JSONObject bodyJson = new JSONObject();
        try {

           // bodyJson.put( FieldDataBase.FILTER.toName(), "weight");
            bodyJson.put( FieldDataBase.ID_WEIGHT.toName(), 81);


            HttpRequest.requestDeleteWeight(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void responseServer(final JSONObject response){
        try {
            if(response.get(HttpRequest.ERROR)!=null) Log.e(TAG, response.toString());
        }catch (JSONException e){
            Log.d(TAG, response.toString());
        }
    }

}
