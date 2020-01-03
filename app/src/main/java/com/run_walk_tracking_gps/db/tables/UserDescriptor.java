package com.run_walk_tracking_gps.db.tables;

import android.database.Cursor;

import com.run_walk_tracking_gps.model.builder.UserBuilder;

import org.json.JSONException;
import org.json.JSONObject;


public class UserDescriptor {

    private UserDescriptor(){}

    public UserDescriptor getInstance(){
        return new UserDescriptor();
    }

    public static final String TABLE_USER = "USER";

    public static final String ID_USER = "id_user";
    public static final String NAME = "name";
    public static final String LAST_NAME = "last_name";
    public static final String BIRTH_DATE = "birth_date";
    public static final String EMAIL = "email";
    public static final String CITY = "city";
    public static final String PHONE = "phone";
    public static final String GENDER = "gender";
    public static final String HEIGHT = "height";

    public static  final String CREATE_TABLE_USER =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "( " +
                    ID_USER + " INTEGER PRIMARY KEY ," +
                    NAME  + " TEXT NOT NULL," +
                    LAST_NAME + " TEXT NOT NULL," +
                    BIRTH_DATE + " TEXT NOT NULL," +
                    EMAIL + " TEXT NOT NULL," +
                    CITY + " TEXT NOT NULL," +
                    PHONE + " TEXT NOT NULL," +
                    GENDER +  " TEXT NOT NULL," +
                    HEIGHT +  " FLOAT NOT NULL, " +
                    "CONSTRAINT SID_User UNIQUE ("+NAME+", "+LAST_NAME +"))";



    public static final String CREATE_INDEX_USER =
            "CREATE UNIQUE INDEX IF NOT EXISTS ID_User on "+ TABLE_USER+" ("+ID_USER+");";

    public static final String CREATE_INDEX_NAME =
            "CREATE UNIQUE INDEX IF NOT EXISTS SID_User on "+ TABLE_USER+" ("+NAME +", " + LAST_NAME +");";




    public static JSONObject from(Cursor cursor) throws JSONException {
        return UserBuilder.create()
                          .setName(cursor.getString(cursor.getColumnIndex(NAME)))
                          .setLastName(cursor.getString(cursor.getColumnIndex(LAST_NAME)))
                          .setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)))
                          .setCity(cursor.getString(cursor.getColumnIndex(CITY)))
                          .setPhone(cursor.getString(cursor.getColumnIndex(PHONE)))
                          .build().toJson()
                          .put(GENDER,cursor.getString(cursor.getColumnIndex(GENDER)))
                          //.put(IMG_NAME, cursor.getString(cursor.getColumnIndex(IMG_NAME))) // PATH IMAGE
                          .put(HEIGHT, cursor.getDouble(cursor.getColumnIndex(HEIGHT)))
                          .put(BIRTH_DATE, cursor.getString(cursor.getColumnIndex(BIRTH_DATE)));
    }

}
