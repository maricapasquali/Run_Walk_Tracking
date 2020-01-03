package com.run_walk_tracking_gps.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.db.DataBaseUtilities;
import com.run_walk_tracking_gps.db.tables.SettingsDescriptor;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.model.enumerations.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SqlLiteSettingsDao implements SettingsDao {

    private static SettingsDao settingsDao;
    private Context context;
    private DaoFactory daoFactory;

    private SqlLiteSettingsDao(Context context){
        this.context = context;
        daoFactory = DaoFactory.getInstance(context);
    }

    public static synchronized SettingsDao create(Context context) {
        if(settingsDao==null){
            settingsDao = new SqlLiteSettingsDao(context.getApplicationContext());
        }
        return settingsDao; //new SqlLiteSettingsDao(context.getApplicationContext());
    }


    @Override
    public JSONObject getSettings() throws JSONException {
        return  new JSONObject().put(SettingsDescriptor.SportDefault.SPORT, getSportDefault())
                                .put(SettingsDescriptor.TargetDefault.TARGET, getTargetDefault())
                                .put(SettingsDescriptor.UnitMeasureDefault.UNIT_MEASURE, getUnitMeasureDefault());
    }

    @Override
    public JSONObject getUnitMeasureDefault() throws JSONException {
        SQLiteDatabase db = daoFactory.getReadableDatabase();
        try (Cursor c = db.rawQuery(get(SettingsDescriptor.UnitMeasureDefault.TABLE_UNIT_MEASURE_DEFAULT),
                new String[]{String.valueOf(DefaultPreferencesUser.getIdUser(context))})) {
            return (!c.moveToFirst()) ? null : SettingsDescriptor.UnitMeasureDefault.from(c);
        }
    }

    @Override
    public String getSportDefault() {
        SQLiteDatabase db = daoFactory.getReadableDatabase();
        try (Cursor c = db.rawQuery(get(SettingsDescriptor.SportDefault.TABLE_SPORT_DEFAULT),
                new String[]{String.valueOf(DefaultPreferencesUser.getIdUser(context))})) {
            return (!c.moveToFirst()) ? null : c.getString(c.getColumnIndex(SettingsDescriptor.SportDefault.SPORT));
        }
    }

    @Override
    public String getTargetDefault() {
        SQLiteDatabase db = daoFactory.getReadableDatabase();
        try (Cursor c = db.rawQuery(get(SettingsDescriptor.TargetDefault.TABLE_TARGET_DEFAULT),
                new String[]{String.valueOf(DefaultPreferencesUser.getIdUser(context))})) {
            return (!c.moveToFirst()) ? null : c.getString(c.getColumnIndex(SettingsDescriptor.TargetDefault.TARGET));
        }
    }

    private String get(String table){
        return "SELECT * FROM "+table+" WHERE "+UserDescriptor.ID_USER+"=?";
    }

    @Override
    public boolean insert(JSONObject settings) throws JSONException {

        final SQLiteDatabase db = daoFactory.getWritableDatabase();
        boolean success = false;
        try {
            db.beginTransaction();

            int id_user = DefaultPreferencesUser.getIdUser(context);

            final ContentValues sportContentValues = new ContentValues();
            sportContentValues.put(UserDescriptor.ID_USER, id_user);
            sportContentValues.put(SettingsDescriptor.SportDefault.SPORT,
                    settings.getString(SettingsDescriptor.SportDefault.SPORT));
            success = db.replace(SettingsDescriptor.SportDefault.TABLE_SPORT_DEFAULT, null, sportContentValues)>0;
            if(!success) throw new SQLiteException();

            final ContentValues targetContentValues = new ContentValues();
            targetContentValues.put(UserDescriptor.ID_USER, id_user);
            targetContentValues.put(SettingsDescriptor.TargetDefault.TARGET,
                    settings.getString(SettingsDescriptor.TargetDefault.TARGET));
            success = db.replace(SettingsDescriptor.TargetDefault.TABLE_TARGET_DEFAULT, null, targetContentValues)>0;
            if(!success) throw new SQLiteException();

            final ContentValues unitContentValues = new ContentValues();
            unitContentValues.put(UserDescriptor.ID_USER, id_user);
            final JSONObject unit_measure =  settings.getJSONObject(SettingsDescriptor.UnitMeasureDefault.UNIT_MEASURE);
            unitContentValues.put(SettingsDescriptor.UnitMeasureDefault.ENERGY,
                    unit_measure.getString(SettingsDescriptor.UnitMeasureDefault.ENERGY) );
            unitContentValues.put(SettingsDescriptor.UnitMeasureDefault.WEIGHT,
                    unit_measure.getString(SettingsDescriptor.UnitMeasureDefault.WEIGHT) );
            unitContentValues.put(SettingsDescriptor.UnitMeasureDefault.HEIGHT,
                    unit_measure.getString(SettingsDescriptor.UnitMeasureDefault.HEIGHT));
            unitContentValues.put(SettingsDescriptor.UnitMeasureDefault.DISTANCE,
                    unit_measure.getString(SettingsDescriptor.UnitMeasureDefault.DISTANCE));
            success = db.replace(SettingsDescriptor.UnitMeasureDefault.TABLE_UNIT_MEASURE_DEFAULT, null, unitContentValues)>0;
            if(!success) throw new SQLiteException();

            db.setTransactionSuccessful();
        } catch (SQLiteException e){
            //Error in between database transaction
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return success;
    }


    @Override
    public boolean update(Enum type, String value) {
        final ContentValues contentValues = new ContentValues();
        final Map<String, String> whereCondition = new HashMap<>();
        String table = null;
        whereCondition.put(UserDescriptor.ID_USER, String.valueOf(DefaultPreferencesUser.getIdUser(context)));
        if(type instanceof Sport)
        {
            table = SettingsDescriptor.SportDefault.TABLE_SPORT_DEFAULT;
            contentValues.put(SettingsDescriptor.SportDefault.SPORT, value);
        }
        else if(type instanceof Target)
        {
            table = SettingsDescriptor.TargetDefault.TABLE_TARGET_DEFAULT;
            contentValues.put(SettingsDescriptor.TargetDefault.TARGET, value);
        }
        else if(type instanceof Measure.Type)
        {
            table = SettingsDescriptor.UnitMeasureDefault.TABLE_UNIT_MEASURE_DEFAULT;
            if(type.equals(Measure.Type.DISTANCE))
                contentValues.put(SettingsDescriptor.UnitMeasureDefault.DISTANCE, value);
            else if(type.equals(Measure.Type.WEIGHT))
                contentValues.put(SettingsDescriptor.UnitMeasureDefault.WEIGHT, value);
            else if(type.equals(Measure.Type.HEIGHT))
                contentValues.put(SettingsDescriptor.UnitMeasureDefault.HEIGHT, value);
        }
        return table != null &&
                DataBaseUtilities.update(daoFactory.getWritableDatabase(), table, contentValues, whereCondition);
    }

}
