package com.run_walk_tracking_gps.db.tables;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

public class WeightDescriptor {

    private WeightDescriptor(){}

    public static final String TABLE_WEIGHT = "WEIGHT";


    public static final String ID_WEIGHT = "id_weight";
    public static final String DATE = "date";
    public static final String VALUE = "value";


    public static  final String CREATE_TABLE_WEIGHT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_WEIGHT + "( " +
                    ID_WEIGHT  + " INTEGER NOT NULL," +
                    UserDescriptor.ID_USER + " INTEGER NOT NULL ," +
                    DATE + " TEXT NOT NULL," +
                    VALUE + " FLOAT NOT NULL, " +
                    "PRIMARY KEY (" + ID_WEIGHT +","+ UserDescriptor.ID_USER +"), "+
                    "CONSTRAINT SID_Weight unique (" +DATE+ "," +UserDescriptor.ID_USER+"), " +
                    "FOREIGN KEY ("+ UserDescriptor.ID_USER +") REFERENCES "+UserDescriptor.TABLE_USER +" ON DELETE CASCADE )";


    public static final String CREATE_INDEX_WEIGHT =
            "CREATE UNIQUE INDEX IF NOT EXISTS ID_Weight on "+ TABLE_WEIGHT+" ("+ID_WEIGHT+ "," +UserDescriptor.ID_USER+");";

    public static final String CREATE_INDEX_DATE =
            "CREATE UNIQUE INDEX IF NOT EXISTS SID_Weight on "+ TABLE_WEIGHT+" ("+UserDescriptor.ID_USER +", " + DATE +");";

    public static JSONObject from(Cursor cursor) {
        try {
            return new JSONObject().put(ID_WEIGHT, cursor.getInt(cursor.getColumnIndex(ID_WEIGHT)))
                    .put(DATE, cursor.getString(cursor.getColumnIndex(DATE)))
                    .put(VALUE, cursor.getFloat(cursor.getColumnIndex(VALUE)));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
