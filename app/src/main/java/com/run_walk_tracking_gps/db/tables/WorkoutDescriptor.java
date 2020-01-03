package com.run_walk_tracking_gps.db.tables;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkoutDescriptor {

    private WorkoutDescriptor(){}

    public static final String TABLE_WORKOUT = "WORKOUT";

    public static final String ID_WORKOUT = "id_workout";
    public static final String DATE = "date";
    public static final String MAP_ROUTE = "map_route";
    public static final String DURATION = "duration";
    public static final String DISTANCE = "distance";
    public static final String CALORIES = "calories";
    public static final String SPORT = "sport";


    public static  final String CREATE_TABLE_WORKOUT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_WORKOUT + "( " +
                    ID_WORKOUT  + " INTEGER NOT NULL," +
                    UserDescriptor.ID_USER + " INTEGER NOT NULL ," +
                    DATE + " INTEGER NOT NULL," +
                    MAP_ROUTE + " TEXT," +
                    DURATION + " INTEGER NOT NULL," +
                    DISTANCE + " FLOAT," +
                    CALORIES + " FLOAT," +
                    SPORT + " TEXT NOT NULL, " +
                    "PRIMARY KEY (" + ID_WORKOUT +","+ UserDescriptor.ID_USER +"), "+
                    "FOREIGN KEY ("+ UserDescriptor.ID_USER +") REFERENCES "+UserDescriptor.TABLE_USER +" ON DELETE CASCADE )";

    public static final String CREATE_INDEX =
            "CREATE UNIQUE INDEX IF NOT EXISTS ID_Workout on "+ TABLE_WORKOUT+" ("+UserDescriptor.ID_USER +", "+ID_WORKOUT+");";

    public static JSONObject from(Cursor cursor) {
        try {
            return new JSONObject()
                    .put(ID_WORKOUT, cursor.getInt(cursor.getColumnIndex(ID_WORKOUT)))
                    .put(DATE, cursor.getInt(cursor.getColumnIndex(DATE)))
                    .put(MAP_ROUTE, cursor.getString(cursor.getColumnIndex(MAP_ROUTE)))
                    .put(SPORT, cursor.getString(cursor.getColumnIndex(SPORT)))
                    .put(DURATION, cursor.getInt(cursor.getColumnIndex(DURATION)))
                    .put(DISTANCE, cursor.getFloat(cursor.getColumnIndex(DISTANCE)))
                    .put(CALORIES, cursor.getFloat(cursor.getColumnIndex(CALORIES)));

        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static class Statistic{
        public final static String VALUE = "value";
        public static JSONObject from(Cursor cursor) {
            try {
                return new JSONObject()
                        .put(DATE, cursor.getInt(cursor.getColumnIndex(DATE)))
                        .put(VALUE, cursor.getDouble(cursor.getColumnIndex(VALUE)))
                     ;

            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
