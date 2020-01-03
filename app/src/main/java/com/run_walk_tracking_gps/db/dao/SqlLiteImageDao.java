package com.run_walk_tracking_gps.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.db.DataBaseUtilities;
import com.run_walk_tracking_gps.db.tables.ImageProfileDescriptor;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SqlLiteImageDao implements ImageDao {

    private static final String TAG = SqlLiteUserDao.class.getName();
    private static ImageDao imageDao;
    private Context context;
    private DaoFactory daoFactory;

    private SqlLiteImageDao(Context context){
        this.context = context;
        daoFactory = DaoFactory.getInstance(context);
    }

    public static synchronized ImageDao create(Context context) {
        if(imageDao==null){
            imageDao = new SqlLiteImageDao(context.getApplicationContext());
        }
        return imageDao; //new SqlLiteWorkoutDao(context.getApplicationContext());
    }

    @Override
    public JSONObject getImage() throws JSONException {
        SQLiteDatabase db = daoFactory.getReadableDatabase();
        try(Cursor c = db.rawQuery("SELECT "+ImageProfileDescriptor.NAME+" FROM "+
                        ImageProfileDescriptor.TABLE_IMG +" WHERE "+ UserDescriptor.ID_USER+"=?",
                new String[]{String.valueOf(DefaultPreferencesUser.getIdUser(context))})){
            return (!c.moveToFirst()) ? null :ImageProfileDescriptor.from(c);
        }
    }


    private boolean hasImage() {
        SQLiteDatabase db = daoFactory.getReadableDatabase();
        try(Cursor c = db.rawQuery("SELECT * FROM "+ ImageProfileDescriptor.TABLE_IMG +" WHERE "+ UserDescriptor.ID_USER+"=?",
                new String[]{String.valueOf(DefaultPreferencesUser.getIdUser(context))})){
            return (c.moveToFirst()) && c.getCount() > 0;
        }
    }

    @Override
    public boolean save(JSONObject image) throws JSONException {
        boolean success = false;
        final ContentValues imgContentValues = new ContentValues();
        imgContentValues.put(UserDescriptor.NAME, image.getString(UserDescriptor.NAME));

        Map<String, String> whereCondition = new HashMap<>();
        whereCondition.put(UserDescriptor.ID_USER, String.valueOf(DefaultPreferencesUser.getIdUser(context)));
        imgContentValues.put(UserDescriptor.ID_USER, DefaultPreferencesUser.getIdUser(context));

        success = DataBaseUtilities.replace(daoFactory.getWritableDatabase(), ImageProfileDescriptor.TABLE_IMG, imgContentValues);
        return success;
    }

    @Override
    public boolean delete() {
        Map<String, String> whereCondition = new HashMap<>();
        whereCondition.put(UserDescriptor.ID_USER, String.valueOf(DefaultPreferencesUser.getIdUser(context)));
        // TODO: 1/1/2020 ELIMINARE DA STOREAGE
        return DataBaseUtilities.delete(daoFactory.getWritableDatabase(), ImageProfileDescriptor.TABLE_IMG, whereCondition);
    }

}
