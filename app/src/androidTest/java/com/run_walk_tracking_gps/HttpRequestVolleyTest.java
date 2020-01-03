package com.run_walk_tracking_gps;

public class HttpRequestVolleyTest {
/*
    private final static String ERROR ="Error";
    private final Context context = InstrumentationRegistry.getTargetContext();
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
            bodyJson.put( HttpRequest.Constant.WEIGHT, 50.6);
            bodyJson.put( HttpRequest.Constant.HEIGHT, 1.66);
            bodyJson.put( HttpRequest.Constant.TARGET, Target.LOSE_WEIGHT);
            bodyJson.put( HttpRequest.Constant.USERNAME, "cacjas");
            bodyJson.put( HttpRequest.Constant.PASSWORD, CryptographicHashFunctions.md5("fino"));

            HttpRequest.requestSignUp(context, bodyJson, this::responseServer);
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
    public void requestChangePasswordVolley(){


        final JSONObject bodyJson = new JSONObject();
        try {

            //bodyJson.put( HttpRequest.Constant.NAME, "Mario");
            bodyJson.put( HttpRequest.Constant.ID_USER, 27);

            HttpRequest.requestChangePassword(context, bodyJson,this::responseServer);

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
            if(response.get(ERROR)!=null) Log.e(TAG, response.toString());
        }catch (JSONException e){
            Log.d(TAG, response.toString());
        }
    }
*/
}
