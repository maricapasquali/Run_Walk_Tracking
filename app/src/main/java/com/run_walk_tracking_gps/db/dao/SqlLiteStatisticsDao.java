package com.run_walk_tracking_gps.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.DataBaseUtilities;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.db.tables.WeightDescriptor;
import com.run_walk_tracking_gps.db.tables.WorkoutDescriptor;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class SqlLiteStatisticsDao implements StatisticsDao{

    private static final String TAG = SqlLiteStatisticsDao.class.getName();

    private Context context;
    private DaoFactory daoFactory;

    private SqlLiteStatisticsDao(Context context, DaoFactory daoFactory){
        this.context=context;
        this.daoFactory = daoFactory;
    }

    public static StatisticsDao create(Context context, DaoFactory daoFactory) {
        return new SqlLiteStatisticsDao(context.getApplicationContext(), daoFactory);
    }

    @Override
    public JSONArray getAll(Measure.Type type) {
        switch (type){
            case WEIGHT:
            {
                return daoFactory.getWeightDao().getAll();
            }
            case DISTANCE:
            {
                return getStatisticsWorkout(WorkoutDescriptor.DISTANCE);
            }
            case ENERGY:
            {
                return getStatisticsWorkout(WorkoutDescriptor.CALORIES);

            }
            case DURATION:
            {
                return getStatisticsWorkout(WorkoutDescriptor.DURATION);
            }
            case MIDDLE_SPEED:
            {
                SQLiteDatabase db = daoFactory.getReadableDatabase();

                Cursor c = db.rawQuery("SELECT "+WorkoutDescriptor.DATE+"," +
                                            WorkoutDescriptor.DISTANCE + "/(cast ("+WorkoutDescriptor.DURATION+" as float)/3600) as "+
                                            WorkoutDescriptor.Statistic.VALUE+
                                            " FROM "+WorkoutDescriptor.TABLE_WORKOUT+" WHERE "+UserDescriptor.ID_USER +"=? AND "+
                                            WorkoutDescriptor.DISTANCE+" IS NOT 0 ORDER by date DESC",
                        new String[]{String.valueOf(Preferences.Session.getIdUser(context))});
                JSONArray middle_speed = DataBaseUtilities.getJSONArrayByCursor(c, WorkoutDescriptor.Statistic::from);
                db.close();
                return middle_speed;
            }
        }
        return null;
    }

    private JSONArray getStatisticsWorkout(String statistic){
        SQLiteDatabase db = daoFactory.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT "+WorkoutDescriptor.DATE +"," + statistic+" as "+WorkoutDescriptor.Statistic.VALUE+" FROM "+WorkoutDescriptor.TABLE_WORKOUT+
                                   " WHERE "+UserDescriptor.ID_USER +"=? AND "+statistic+" IS NOT 0 ORDER by date DESC",
                new String[]{String.valueOf(Preferences.Session.getIdUser(context))});
        JSONArray distances = DataBaseUtilities.getJSONArrayByCursor(c, WorkoutDescriptor.Statistic::from);
        db.close();
        return distances;
    }

    public static class SqlLiteWeightDao implements StatisticsDao.WeightDao {

        private Context context;
        private DaoFactory daoFactory;


        private SqlLiteWeightDao(Context context, DaoFactory daoFactory){
            this.context = context;
            this.daoFactory = daoFactory;
        }

        public static WeightDao create(Context context, DaoFactory daoFactory) {
            return new SqlLiteWeightDao(context.getApplicationContext(), daoFactory);
        }

        @Override
        public JSONArray getAll(){
            SQLiteDatabase db = daoFactory.getReadableDatabase();
            Cursor c = db.query(WeightDescriptor.TABLE_WEIGHT, null,
                    UserDescriptor.ID_USER+"=?", new String[]{String.valueOf(Preferences.Session.getIdUser(context))},
                    null , null , null);
            JSONArray weights = DataBaseUtilities.getJSONArrayByCursor(c, WeightDescriptor::from);
            db.close();
            return weights;
        }

        @Override
        public double getLast() {
            SQLiteDatabase db = daoFactory.getReadableDatabase();
            try (Cursor c = db.rawQuery("SELECT * FROM "+WeightDescriptor.TABLE_WEIGHT +
                            " WHERE "+UserDescriptor.ID_USER +"=? ORDER by date DESC LIMIT 1",
                    new String[]{String.valueOf(Preferences.Session.getIdUser(context))})) {
                return (!c.moveToFirst()) ? 0 : c.getDouble(c.getColumnIndex(WeightDescriptor.VALUE));
            }
        }

        private long insertOne(SQLiteDatabase db, JSONObject weight, int id_user) throws JSONException {

            final ContentValues weightContentValues = new ContentValues();

            int id_weight = -1;
            if(weight.has(WeightDescriptor.ID_WEIGHT))
                id_weight = weight.getInt(WeightDescriptor.ID_WEIGHT);
            else
                id_weight = DataBaseUtilities.getNextId(daoFactory.getReadableDatabase(), id_user,
                        WeightDescriptor.TABLE_WEIGHT, WeightDescriptor.ID_WEIGHT);

            if(id_weight!=-1) weightContentValues.put(WeightDescriptor.ID_WEIGHT, id_weight);

            weightContentValues.put(UserDescriptor.ID_USER, id_user);
            weightContentValues.put(WeightDescriptor.DATE, weight.getString(WeightDescriptor.DATE));
            weightContentValues.put(WeightDescriptor.VALUE, weight.getDouble(WeightDescriptor.VALUE));


            boolean success = db.insert(WeightDescriptor.TABLE_WEIGHT, null, weightContentValues) !=-1;
            return success ? id_weight : -1;
        }

        @Override
        public long insert(JSONObject weight) throws JSONException {
            final SQLiteDatabase db = daoFactory.getWritableDatabase();
            long id_weight = -1;
            try {
                db.beginTransaction();

                int id_user = Preferences.Session.getIdUser(context);
                id_weight = insertOne(db, weight, id_user);
                if(id_weight==-1) throw new SQLiteException();

                db.setTransactionSuccessful();
            } catch (SQLiteException e){
                //Error in between database transaction
                e.printStackTrace();
            }
            finally {
                db.endTransaction();
                db.close();
            }
            return id_weight;
        }

        // TODO: 19/02/2020  DA ELIMINARE
        private boolean insertAll(JSONArray weights) throws JSONException {
            final SQLiteDatabase db = daoFactory.getWritableDatabase();
            boolean success = false;
            try {
                db.beginTransaction();

                int id_user = Preferences.Session.getIdUser(context);

                for(int i=0; i <weights.length(); i++){
                    success = (insertOne(db, weights.getJSONObject(i), id_user)!=-1);
                    if(!success) throw new SQLiteException();
                }

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
        public boolean replaceAll(JSONArray weights) throws JSONException {
            final SQLiteDatabase db = daoFactory.getWritableDatabase();
            boolean success = false;
            try {
                db.beginTransaction();
                int id_user = Preferences.Session.getIdUser(context);

                db.delete(WeightDescriptor.TABLE_WEIGHT,UserDescriptor.ID_USER + "=?", new String[]{String.valueOf(id_user)});

                for(int i=0; i <weights.length(); i++){
                    success = (insertOne(db, weights.getJSONObject(i), id_user)!=-1);
                    if(!success) throw new SQLiteException();
                }

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
        public boolean update(JSONObject weight) throws JSONException {
            Log.e(TAG, weight.toString());
            if(JSONUtilities.countKey(weight)==1) return false;

            final ContentValues weightContentValues = new ContentValues();
            if(weight.has(WeightDescriptor.DATE))
                weightContentValues.put(WeightDescriptor.DATE, weight.getString(WeightDescriptor.DATE));
            if(weight.has(WeightDescriptor.VALUE))
                weightContentValues.put(WeightDescriptor.VALUE, weight.getDouble(WeightDescriptor.VALUE));

            Map<String, String> whereCondition = new HashMap<>();
            whereCondition.put(WeightDescriptor.ID_WEIGHT, String.valueOf(weight.getString(WeightDescriptor.ID_WEIGHT)));
            whereCondition.put(UserDescriptor.ID_USER, String.valueOf(Preferences.Session.getIdUser(context)));
            return DataBaseUtilities.update(daoFactory.getWritableDatabase(), WeightDescriptor.TABLE_WEIGHT, weightContentValues, whereCondition);
        }

        @Override
        public boolean delete(int id) {
            Map<String, String> whereCondition = new HashMap<>();
            whereCondition.put(WeightDescriptor.ID_WEIGHT, String.valueOf(id));
            whereCondition.put(UserDescriptor.ID_USER, String.valueOf(Preferences.Session.getIdUser(context)));
            return DataBaseUtilities.delete(daoFactory.getWritableDatabase(), WeightDescriptor.TABLE_WEIGHT, whereCondition);
        }

        @Override
        public boolean isOne() {
            SQLiteDatabase db = daoFactory.getReadableDatabase();
            try (Cursor c = db.rawQuery("SELECT * FROM "+WeightDescriptor.TABLE_WEIGHT+" WHERE "+UserDescriptor.ID_USER+"=?"
                    , new String[]{String.valueOf(Preferences.Session.getIdUser(context))})) {
                return (!c.moveToFirst()) ? null : c.getCount()==1;
            }
        }
    }
}
