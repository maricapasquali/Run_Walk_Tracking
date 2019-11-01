package com.run_walk_tracking_gps;

import com.run_walk_tracking_gps.model.enumerations.Gender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonObjectTest {

    private final static String KEY_NAME = "name";
    private final static String KEY_LAST_NAME ="lastname";
    private final static String KEY_GENDER ="gender";
    private final static String VALUE_NAME = "Marica";
    private final static String VALUE_LAST_NAME = "Pasquali";
    private final static String VALUE_GENDER = "Femmmina";

    private final static String KEY_CONVERT = "key";
    private final static int VALUE_CONVERT = 1339188905;

    private JSONObject object;
    private String stringJson;


    @Before
    public void init(){
        try {
            System.out.print("Before");
            object = new JSONObject();
            object.put(KEY_NAME, VALUE_NAME);
            object.put(KEY_LAST_NAME, VALUE_LAST_NAME);
            object.put(KEY_GENDER, VALUE_GENDER);

            JSONArray jsonArray= new JSONArray();
            for (int i=0; i<2; i++){
                JSONObject item = new JSONObject();
                item.put("information", "test"+i);
                item.put("id", i);
                item.put("name", "course"+i);
                jsonArray.put(item);
            }
            object.put("course", jsonArray);


            stringJson ="{\n" +
                    "  \"email\": \"marica@gmail.com\",\n" +
                    "  \"key\": 1339188905,\n" +
                    "  \"expiry_date\": \"2019-10-25 13:42:44\",\n" +
                    "  \"request_sended\": true\n" +
                    "}";


        } catch (JSONException e) {
            System.err.print(e.getMessage());
        }
    }

    @Test
    public void getJsonValues(){

        try {
            System.out.print("\n\n"+object.toString()+"\n\n");
            Assert.assertEquals(VALUE_NAME, object.get(KEY_NAME));
            Assert.assertEquals(VALUE_LAST_NAME, object.get(KEY_LAST_NAME));
            Assert.assertEquals(VALUE_GENDER, object.get(KEY_GENDER));
        } catch (JSONException e) {
            System.err.print(e.getMessage());
        }

    }


    @Test
    public void convertStringToJson(){

        try {
            System.err.print(
                    Gender.FEMALE.toString()
            );
            object = new JSONObject(stringJson);
            System.out.print("\n\n" +object.toString() + "\n\n");
            Assert.assertEquals(VALUE_CONVERT, object.get(KEY_CONVERT));
        }catch (JSONException e){
            System.err.print(e.getMessage());
        }

    }
}
