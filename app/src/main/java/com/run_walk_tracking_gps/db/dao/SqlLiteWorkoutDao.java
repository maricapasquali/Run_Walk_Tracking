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
import com.run_walk_tracking_gps.db.tables.WorkoutDescriptor;
import com.run_walk_tracking_gps.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SqlLiteWorkoutDao implements WorkoutDao {

    private static final String TAG = SqlLiteWorkoutDao.class.getName();
    private static WorkoutDao workoutDao;
    private Context context;
    private DaoFactory daoFactory;

    private SqlLiteWorkoutDao(Context context){
        this.context = context;
        daoFactory = DaoFactory.getInstance(context);
    }

    public static synchronized WorkoutDao create(Context context) {
        if(workoutDao==null){
            workoutDao = new SqlLiteWorkoutDao(context.getApplicationContext());
        }
        return workoutDao; //new SqlLiteWorkoutDao(context.getApplicationContext());
    }

    @Override
    public JSONArray getAll() {
        SQLiteDatabase db = daoFactory.getReadableDatabase();
        Cursor c = db.query(WorkoutDescriptor.TABLE_WORKOUT, null,
                UserDescriptor.ID_USER+"=?", new String[]{String.valueOf(Preferences.Session.getIdUser(context))},
                null , null , null);
        JSONArray workouts = DataBaseUtilities.getJSONArrayByCursor(c, WorkoutDescriptor::from);
        db.close();
        return workouts;
    }

    @Override
    public long insert(JSONObject workout) throws JSONException {
        final SQLiteDatabase db = daoFactory.getWritableDatabase();
        long id_workout = -1;
        try {

            db.beginTransaction();
            int id_user = Preferences.Session.getIdUser(context);

            id_workout = insertOne(db, workout, id_user);
            if(id_workout==-1)
                throw new SQLiteException();


            db.setTransactionSuccessful();
        } catch (SQLiteException e){
            //Error in between database transaction
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return id_workout;
    }

    private long insertOne(SQLiteDatabase db, JSONObject workout, int id_user) throws JSONException {

        final ContentValues workoutContentValues = new ContentValues();

        workoutContentValues.put(UserDescriptor.ID_USER, id_user);
        int id_workout = -1;
        if(workout.has(WorkoutDescriptor.ID_WORKOUT))
            id_workout = workout.getInt(WorkoutDescriptor.ID_WORKOUT);
        else
            id_workout = DataBaseUtilities.getNextId(daoFactory.getReadableDatabase(), id_user,
                    WorkoutDescriptor.TABLE_WORKOUT, WorkoutDescriptor.ID_WORKOUT);

        if(id_workout!=-1) workoutContentValues.put(WorkoutDescriptor.ID_WORKOUT, id_workout);


        workoutContentValues.put(WorkoutDescriptor.DATE, workout.getString(WorkoutDescriptor.DATE));
        workoutContentValues.put(WorkoutDescriptor.SPORT, workout.getString(WorkoutDescriptor.SPORT));
        workoutContentValues.put(WorkoutDescriptor.DURATION, workout.getInt(WorkoutDescriptor.DURATION));

        workoutContentValues.put(WorkoutDescriptor.MAP_ROUTE, workout.optString(WorkoutDescriptor.MAP_ROUTE));
        workoutContentValues.put(WorkoutDescriptor.DISTANCE, workout.optDouble(WorkoutDescriptor.DISTANCE, 0));
        workoutContentValues.put(WorkoutDescriptor.CALORIES, workout.optDouble(WorkoutDescriptor.CALORIES, 0));

        boolean success = db.insert(WorkoutDescriptor.TABLE_WORKOUT, null, workoutContentValues) !=-1;
        return success ? id_workout : -1;
    }

    // TODO: 19/02/2020  DA ELIMINARE
    private boolean insertAll(JSONArray workouts) throws JSONException {
        final SQLiteDatabase db = daoFactory.getWritableDatabase();
        boolean success = false;
        try {

            db.beginTransaction();
            int id_user = Preferences.Session.getIdUser(context);

            for (int i = 0; i <workouts.length(); i++){
                success = (insertOne(db, workouts.getJSONObject(i), id_user)!=-1);
                if(!success)
                    throw new SQLiteException();
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
    public boolean replaceAll(JSONArray workouts) throws JSONException {
        final SQLiteDatabase db = daoFactory.getWritableDatabase();
        boolean success = false;
        try {
            db.beginTransaction();
            int id_user = Preferences.Session.getIdUser(context);

            db.delete(WorkoutDescriptor.TABLE_WORKOUT, UserDescriptor.ID_USER + "=?",
                    new String[]{String.valueOf(id_user)});

            for(int i=0; i <workouts.length(); i++){
                success = (insertOne(db, workouts.getJSONObject(i), id_user)!=-1);
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
    public boolean update(JSONObject workout) throws JSONException {
        Log.e(TAG, workout.toString());
        if(JSONUtilities.countKey(workout)==1) return false;

        final ContentValues workoutContentValues = new ContentValues();
        if(workout.has(WorkoutDescriptor.DATE))
            workoutContentValues.put(WorkoutDescriptor.DATE, workout.getString(WorkoutDescriptor.DATE));
        if(workout.has(WorkoutDescriptor.SPORT))
            workoutContentValues.put(WorkoutDescriptor.SPORT, workout.getString(WorkoutDescriptor.SPORT));
        if(workout.has(WorkoutDescriptor.DURATION))
            workoutContentValues.put(WorkoutDescriptor.DURATION, workout.getInt(WorkoutDescriptor.DURATION));
        if(workout.has(WorkoutDescriptor.DISTANCE))
            workoutContentValues.put(WorkoutDescriptor.DISTANCE, workout.getDouble(WorkoutDescriptor.DISTANCE));
        if(workout.has(WorkoutDescriptor.CALORIES))
            workoutContentValues.put(WorkoutDescriptor.CALORIES, workout.getDouble(WorkoutDescriptor.CALORIES));

        Map<String, String> whereCondition = new HashMap<>();
        whereCondition.put(WorkoutDescriptor.ID_WORKOUT, String.valueOf(workout.getString(WorkoutDescriptor.ID_WORKOUT)));
        whereCondition.put(UserDescriptor.ID_USER, String.valueOf(Preferences.Session.getIdUser(context)));

        return DataBaseUtilities.update(daoFactory.getWritableDatabase(), WorkoutDescriptor.TABLE_WORKOUT, workoutContentValues, whereCondition);
    }

    @Override
    public boolean delete(int id) {
        Map<String, String> whereCondition = new HashMap<>();
        whereCondition.put(WorkoutDescriptor.ID_WORKOUT, String.valueOf(id));
        whereCondition.put(UserDescriptor.ID_USER, String.valueOf(Preferences.Session.getIdUser(context)));
        return DataBaseUtilities.delete(daoFactory.getWritableDatabase(), WorkoutDescriptor.TABLE_WORKOUT, whereCondition);
    }
}
