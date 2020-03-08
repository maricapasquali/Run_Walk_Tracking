package com.run_walk_tracking_gps.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.DataBaseUtilities;
import com.run_walk_tracking_gps.db.tables.ImageProfileDescriptor;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.utilities.JSONUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import java.util.Map;


class SqlLiteUserDao implements UserDao {

    private static final String TAG = SqlLiteUserDao.class.getName();

    private Context context;
    private DaoFactory daoFactory;

    private SqlLiteUserDao(Context context, DaoFactory daoFactory){
        this.context = context;
        this.daoFactory = daoFactory;
    }

    public static UserDao create(Context context, DaoFactory daoFactory) {
        return new SqlLiteUserDao(context.getApplicationContext(), daoFactory);
    }

    @Override
    public JSONObject getUser() throws JSONException {
        JSONObject user = null;
        SQLiteDatabase db = daoFactory.getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT * FROM "+UserDescriptor.TABLE_USER+" WHERE "+UserDescriptor.ID_USER+"=?",
                new String[]{String.valueOf(Preferences.Session.getIdUser(context))})) {
            user =  (!c.moveToFirst()) ? null : UserDescriptor.from(c);
        }
        final JSONObject image = daoFactory.getImageDao().getImage();

        return image==null ? user : JSONUtilities.merge(user, image);
    }

    @Override
    public boolean insert(JSONObject user) throws JSONException {
        final ContentValues userContentValues = new ContentValues();
        userContentValues.put(UserDescriptor.ID_USER, user.getInt(UserDescriptor.ID_USER));
        userContentValues.put(UserDescriptor.NAME, user.getString(UserDescriptor.NAME));
        userContentValues.put(UserDescriptor.LAST_NAME, user.getString(UserDescriptor.LAST_NAME));
        userContentValues.put(UserDescriptor.GENDER, user.getString(UserDescriptor.GENDER));
        userContentValues.put(UserDescriptor.BIRTH_DATE, user.getString(UserDescriptor.BIRTH_DATE));
        userContentValues.put(UserDescriptor.EMAIL, user.getString(UserDescriptor.EMAIL));
        userContentValues.put(UserDescriptor.CITY, user.getString(UserDescriptor.CITY));
        userContentValues.put(UserDescriptor.PHONE, user.getString(UserDescriptor.PHONE));
        userContentValues.put(UserDescriptor.HEIGHT, user.getDouble(UserDescriptor.HEIGHT));

        return DataBaseUtilities.replace(daoFactory.getWritableDatabase(), UserDescriptor.TABLE_USER, userContentValues)
                && ( user.isNull(NetworkHelper.Constant.IMAGE) ? daoFactory.getImageDao().delete() :
                daoFactory.getImageDao().save(user.getJSONObject(NetworkHelper.Constant.IMAGE)));
    }

    @Override
    public boolean update(JSONObject user) throws JSONException {
        Log.e(TAG, user.toString());
        if(JSONUtilities.countKey(user)==0) return false;

        final ContentValues userContentValues = new ContentValues();
        if(user.has(UserDescriptor.NAME))
            userContentValues.put(UserDescriptor.NAME, user.getString(UserDescriptor.NAME));
        if(user.has(UserDescriptor.LAST_NAME))
            userContentValues.put(UserDescriptor.LAST_NAME, user.getString(UserDescriptor.LAST_NAME));
        if(user.has(UserDescriptor.GENDER))
            userContentValues.put(UserDescriptor.GENDER, user.getString(UserDescriptor.GENDER));
        if(user.has(UserDescriptor.BIRTH_DATE))
            userContentValues.put(UserDescriptor.BIRTH_DATE, user.getString(UserDescriptor.BIRTH_DATE));
        if(user.has(UserDescriptor.EMAIL))
            userContentValues.put(UserDescriptor.EMAIL, user.getString(UserDescriptor.EMAIL));
        if(user.has(UserDescriptor.CITY))
            userContentValues.put(UserDescriptor.CITY, user.getString(UserDescriptor.CITY));
        if(user.has(UserDescriptor.PHONE))
            userContentValues.put(UserDescriptor.PHONE, user.getString(UserDescriptor.PHONE));
        if(user.has(UserDescriptor.HEIGHT))
            userContentValues.put(UserDescriptor.HEIGHT, user.getDouble(UserDescriptor.HEIGHT));

        Map<String, String> whereCondition = new HashMap<>();
        whereCondition.put(UserDescriptor.ID_USER, String.valueOf(Preferences.Session.getIdUser(context)));
        boolean success = false;
        if(userContentValues.size()>0){
            success = DataBaseUtilities.update(daoFactory.getWritableDatabase(), UserDescriptor.TABLE_USER,
                    userContentValues, whereCondition);
        }

        if(user.has(NetworkHelper.Constant.IMAGE)){
            success = daoFactory.getImageDao().save(user.getJSONObject(NetworkHelper.Constant.IMAGE));
        }

        return success;

    }

    @Override
    public boolean delete() {
        int id_user = Preferences.Session.getIdUser(context);
        Map<String, String> whereCondition = new HashMap<>();
        whereCondition.put(UserDescriptor.ID_USER, String.valueOf(id_user));
        return DataBaseUtilities.delete(daoFactory.getWritableDatabase(), UserDescriptor.TABLE_USER, whereCondition);
    }

    static class SqlLiteImageDao implements UserDao.ImageDao {

        private static final String TAG = SqlLiteImageDao.class.getName();
        private Context context;
        private DaoFactory daoFactory;

        private SqlLiteImageDao(Context context, DaoFactory daoFactory){
            this.context = context;
            this.daoFactory = daoFactory;
        }

        public static UserDao.ImageDao  create(Context context, DaoFactory daoFactory) {
            return new SqlLiteImageDao(context.getApplicationContext(), daoFactory);
        }

        @Override
        public JSONObject getImage() throws JSONException {
            SQLiteDatabase db = daoFactory.getReadableDatabase();
            try(Cursor c = db.rawQuery("SELECT "+ ImageProfileDescriptor.NAME+" FROM "+
                            ImageProfileDescriptor.TABLE_IMG +" WHERE "+ UserDescriptor.ID_USER+"=?",
                    new String[]{String.valueOf(Preferences.Session.getIdUser(context))})){
                return (!c.moveToFirst()) ? null :ImageProfileDescriptor.from(c);
            }
        }

        private boolean hasImage() {
            SQLiteDatabase db = daoFactory.getReadableDatabase();
            try(Cursor c = db.rawQuery("SELECT * FROM "+ ImageProfileDescriptor.TABLE_IMG +" WHERE "+ UserDescriptor.ID_USER+"=?",
                    new String[]{String.valueOf(Preferences.Session.getIdUser(context))})){
                return (c.moveToFirst()) && c.getCount() > 0;
            }
        }

        @Override
        public boolean save(JSONObject image) throws JSONException {
            boolean success = false;
            final ContentValues imgContentValues = new ContentValues();
            imgContentValues.put(UserDescriptor.NAME, image.getString(UserDescriptor.NAME));

            Map<String, String> whereCondition = new HashMap<>();
            whereCondition.put(UserDescriptor.ID_USER, String.valueOf(Preferences.Session.getIdUser(context)));
            imgContentValues.put(UserDescriptor.ID_USER, Preferences.Session.getIdUser(context));

            success = DataBaseUtilities.replace(daoFactory.getWritableDatabase(), ImageProfileDescriptor.TABLE_IMG, imgContentValues);
            return success;
        }

        @Override
        public boolean delete() {
            Map<String, String> whereCondition = new HashMap<>();
            whereCondition.put(UserDescriptor.ID_USER, String.valueOf(Preferences.Session.getIdUser(context)));
            // TODO: 1/1/2020 ELIMINARE DA STOREAGE
            return DataBaseUtilities.delete(daoFactory.getWritableDatabase(), ImageProfileDescriptor.TABLE_IMG, whereCondition);
        }

    }

}
