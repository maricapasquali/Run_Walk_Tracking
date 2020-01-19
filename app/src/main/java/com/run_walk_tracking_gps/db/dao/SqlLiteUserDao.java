package com.run_walk_tracking_gps.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.DataBaseUtilities;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.utilities.JSONUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import java.util.Map;


public class SqlLiteUserDao implements UserDao {

    private static final String TAG = SqlLiteUserDao.class.getName();
    private static UserDao userDao;
    private Context context;
    private DaoFactory daoFactory;

    private SqlLiteUserDao(Context context){
        this.context = context;
        daoFactory = DaoFactory.getInstance(context);
    }

    public static synchronized UserDao create(Context context) {
        if(userDao==null){
            userDao = new SqlLiteUserDao(context.getApplicationContext());
        }
        return userDao; //new SqlLiteUserDao(context.getApplicationContext());
    }

    @Override
    public JSONObject getUser() throws JSONException {
        JSONObject user = null;
        SQLiteDatabase db = daoFactory.getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT * FROM "+UserDescriptor.TABLE_USER+" WHERE "+UserDescriptor.ID_USER+"=?",
                new String[]{String.valueOf(Preferences.Session.getIdUser(context))})) {
            user =  (!c.moveToFirst()) ? null : UserDescriptor.from(c);
        }
        final JSONObject image = SqlLiteImageDao.create(context).getImage();

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
                && ( user.isNull(NetworkHelper.Constant.IMAGE) ? SqlLiteImageDao.create(context).delete() :
                SqlLiteImageDao.create(context).save(user.getJSONObject(NetworkHelper.Constant.IMAGE)));
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
            success = SqlLiteImageDao.create(context).save(user.getJSONObject(NetworkHelper.Constant.IMAGE));
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
}
