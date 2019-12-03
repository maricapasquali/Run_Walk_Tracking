package com.run_walk_tracking_gps;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

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

    final Context context = InstrumentationRegistry.getTargetContext();
    private static final String TAG = HttpRequestVolleyTest.class.getName();
    @Test
    public void requestSignUpVolley(){

        final JSONObject bodyJson = new JSONObject();
        try {
            //bodyJson.put(HttpRequest.Constant.ID_USER.toName(), 21);
            bodyJson.put( HttpRequest.Constant.IMG_ENCODE, "SFSFISDIGN");
            bodyJson.put( HttpRequest.Constant.NAME, "fabio");
            bodyJson.put( HttpRequest.Constant.LAST_NAME, "cani");
            bodyJson.put( HttpRequest.Constant.GENDER, Gender.MALE);
            bodyJson.put( HttpRequest.Constant.BIRTH_DATE, Calendar.getInstance().getTime());
            bodyJson.put( HttpRequest.Constant.EMAIL, "marica@gmail.com");
            bodyJson.put( HttpRequest.Constant.CITY, "Roma");
            bodyJson.put( HttpRequest.Constant.PHONE, "324242342342");
            bodyJson.put( HttpRequest.Constant.LANGUAGE,  Locale.getDefault().getDisplayLanguage());
            bodyJson.put( HttpRequest.Constant.WEIGHT, 50.6);
            bodyJson.put( HttpRequest.Constant.HEIGHT, 1.66);
            bodyJson.put( HttpRequest.Constant.TARGET, Target.LOSE_WEIGHT);
            bodyJson.put( HttpRequest.Constant.USERNAME, "cacjas");
            bodyJson.put( HttpRequest.Constant.PASSWORD, CryptographicHashFunctions.md5("fino"));

            if(!HttpRequest.requestSignUp(context, bodyJson, this::responseServer)){
                Log.e(TAG, context.getString(R.string.internet_not_available));
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestSignInVolley(){

        final JSONObject bodyJson = new JSONObject();
        try {
            bodyJson.put( HttpRequest.Constant.USERNAME, "marioRossi$1");
            //bodyJson.put( HttpRequest.Constant.NAME, "Mario");
            //bodyJson.put( HttpRequest.Constant.LAST_NAME, "Rossi");

            HttpRequest.requestSignIn(context, bodyJson,this::responseServer);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestUserInfoVolley(){


        final JSONObject bodyJson = new JSONObject();
        try {
            bodyJson.put( HttpRequest.Constant.ID_USER, 27);
            //bodyJson.put( HttpRequest.Constant.NAME, "Mario");

            HttpRequest.requestUserInformation(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestUpdateUserInfoVolley(){

        final JSONObject bodyJson = new JSONObject();
        try {

            bodyJson.put( HttpRequest.Constant.ID_USER, 27);
            //bodyJson.put( HttpRequest.Constant.WEIGHT, "Kira");
            HttpRequest.requestDelayedUpdateUserInformation(context, bodyJson,this::responseServer, null);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestDeleteUserInfoVolley(){

        final JSONObject bodyJson = new JSONObject();
        try {

           // bodyJson.put( HttpRequest.Constant.NAME, "Mario");
            bodyJson.put( HttpRequest.Constant.ID_USER, 27);

            HttpRequest.requestDeleteUser(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestUpdatePasswordVolley(){


        final JSONObject bodyJson = new JSONObject();
        try {

            //bodyJson.put( HttpRequest.Constant.NAME, "Mario");
            bodyJson.put( HttpRequest.Constant.ID_USER, 27);

            HttpRequest.requestUpdatePassword(context, bodyJson,this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestUpdateSettingVolley(){


        final JSONObject bodyJson = new JSONObject();
        try {

            //bodyJson.put( HttpRequest.Constant.NAME, "Mario");
            bodyJson.put( HttpRequest.Constant.ID_USER, 27);

            HttpRequest.requestUpdateSetting(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    @Test
    public void requestAllWorkoutsVolley(){


        final JSONObject bodyJson = new JSONObject();
        try {

            //bodyJson.put( HttpRequest.Constant.NAME, "Mario");
            bodyJson.put( HttpRequest.Constant.ID_USER, 27);

            HttpRequest.requestWorkouts(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

   @Test
    public void requestNewWorkoutVolley(){

        final JSONObject bodyJson = new JSONObject();
        try {

            //bodyJson.put( HttpRequest.Constant.NAME, "Mario");
            bodyJson.put( HttpRequest.Constant.ID_USER, 27);
            bodyJson.put( HttpRequest.Constant.DATE, DateHelper.create(context).getCalendar().getTime());
            bodyJson.put( HttpRequest.Constant.DURATION, 27);

            HttpRequest.requestNewWorkout(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    @Test
    public void requestUpdateWorkoutVolley(){


        final JSONObject bodyJson = new JSONObject();
        try {


            bodyJson.put( HttpRequest.Constant.ID_WORKOUT, 17);
            bodyJson.put( HttpRequest.Constant.DISTANCE, 7.0);

            HttpRequest.requestUpdateWorkout(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestDeleteWorkoutVolley(){

        final JSONObject bodyJson = new JSONObject();
        try {


            bodyJson.put( HttpRequest.Constant.ID_WORKOUT, 17);
            //bodyJson.put( HttpRequest.Constant.DISTANCE, 7.0);

            HttpRequest.requestDeleteWorkout(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestStatisticsVolley(){


        final JSONObject bodyJson = new JSONObject();
        try {

            bodyJson.put( HttpRequest.Constant.FILTER, "weight");
            bodyJson.put( HttpRequest.Constant.ID_USER, 27);
            //bodyJson.put( HttpRequest.Constant.DISTANCE, 7.0);

            HttpRequest.requestStatistics(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestNewWeightVolley(){

        final JSONObject bodyJson = new JSONObject();
        try {

            bodyJson.put( HttpRequest.Constant.VALUE, 70.2);
            bodyJson.put( HttpRequest.Constant.DATE, "2019-10-29");
            bodyJson.put( HttpRequest.Constant.ID_USER, 39);


            HttpRequest.requestNewWeight(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestUpdateWeightVolley(){


        final JSONObject bodyJson = new JSONObject();
        try {

            // bodyJson.put( HttpRequest.Constant.FILTER, "weight");
            bodyJson.put( HttpRequest.Constant.ID_WEIGHT,28);
            bodyJson.put( HttpRequest.Constant.VALUE,70);


            HttpRequest.requestUpdateWeight(context, bodyJson, this::responseServer);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Test
    public void requestDeleteWeightVolley(){

        final JSONObject bodyJson = new JSONObject();
        try {

           // bodyJson.put( HttpRequest.Constant.FILTER, "weight");
            bodyJson.put( HttpRequest.Constant.ID_WEIGHT, 81);


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
