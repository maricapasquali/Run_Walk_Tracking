package com.run_walk_tracking_gps.db.tables;

import android.database.Cursor;

import com.run_walk_tracking_gps.model.Measure;

import org.json.JSONException;
import org.json.JSONObject;


public class SettingsDescriptor {

    public static class SportDefault {
        private SportDefault(){}

        public static final String TABLE_SPORT_DEFAULT = "SPORT_DEFAULT";

        public static final String SPORT = "sport";

        public static  final String CREATE_TABLE_SPORT_DEFAULT =
                "CREATE TABLE IF NOT EXISTS " + TABLE_SPORT_DEFAULT + "( " +
                        UserDescriptor.ID_USER + " INTEGER PRIMARY KEY NOT NULL," +
                        SPORT  + " TEXT NOT NULL , " +
                        "FOREIGN KEY ("+ UserDescriptor.ID_USER +") REFERENCES "+UserDescriptor.TABLE_USER +" ON DELETE CASCADE )";


        public static final String CREATE_INDEX =
                "CREATE UNIQUE INDEX IF NOT EXISTS FKchange on "+ TABLE_SPORT_DEFAULT+" ("+UserDescriptor.ID_USER +");";
    }

    public static class TargetDefault{
        private TargetDefault(){}

        public static final String TABLE_TARGET_DEFAULT = "TARGET_DEFAULT";


        public static final String TARGET = "target";


        public static  final String CREATE_TABLE_TARGET_DEFAULT =
                "CREATE TABLE IF NOT EXISTS " + TABLE_TARGET_DEFAULT + "( " +
                        UserDescriptor.ID_USER  + " INTEGER PRIMARY KEY NOT NULL," +
                        TARGET  + " TEXT NOT NULL , " +
                        "FOREIGN KEY ("+ UserDescriptor.ID_USER +") REFERENCES "+UserDescriptor.TABLE_USER + " ON DELETE CASCADE )";

        public static final String CREATE_INDEX =
                "CREATE UNIQUE INDEX IF NOT EXISTS FKpossess on "+ TABLE_TARGET_DEFAULT+" ("+UserDescriptor.ID_USER +");";
    }

    public static class UnitMeasureDefault {

        private UnitMeasureDefault(){}

        public static final String TABLE_UNIT_MEASURE_DEFAULT = "UNIT_MEASURE_DEFAULT";

        public static final String UNIT_MEASURE = "unit_measure";

        public static final String ENERGY = "energy";
        public static final String WEIGHT = "weight";
        public static final String DISTANCE = "distance";
        public static final String HEIGHT = "height";

        public static final String CREATE_TABLE_UNIT_MEASURE_DEFAULT =
                "CREATE TABLE IF NOT EXISTS " + TABLE_UNIT_MEASURE_DEFAULT + "( " +
                        UserDescriptor.ID_USER + " INTEGER PRIMARY KEY NOT NULL," +
                        ENERGY  + " TEXT NOT NULL DEFAULT " + Measure.Unit.KILO_CALORIES +", "+
                        WEIGHT  + " TEXT NOT NULL DEFAULT " + Measure.Unit.KILOGRAM +", "+
                        DISTANCE  + " TEXT NOT NULL DEFAULT " + Measure.Unit.KILOMETER +", "+
                        HEIGHT  + " TEXT NOT NULL DEFAULT " + Measure.Unit.METER +", "+
                        "FOREIGN KEY ("+ UserDescriptor.ID_USER +") REFERENCES "+UserDescriptor.TABLE_USER + " ON DELETE CASCADE )";

        public static final String CREATE_INDEX =
                "CREATE UNIQUE INDEX IF NOT EXISTS FKchoose on "+ TABLE_UNIT_MEASURE_DEFAULT+" ("+UserDescriptor.ID_USER +");";


        public static JSONObject from(Cursor c) throws JSONException {
            JSONObject unitForUser = new JSONObject();
            unitForUser.put(ENERGY, c.getString(c.getColumnIndex(UnitMeasureDefault.ENERGY)));
            unitForUser.put(DISTANCE, c.getString(c.getColumnIndex(SettingsDescriptor.UnitMeasureDefault.DISTANCE)));
            unitForUser.put(WEIGHT, c.getString(c.getColumnIndex(SettingsDescriptor.UnitMeasureDefault.WEIGHT)));
            unitForUser.put(HEIGHT, c.getString(c.getColumnIndex(SettingsDescriptor.UnitMeasureDefault.HEIGHT)));
            return unitForUser;
        }
    }

}
