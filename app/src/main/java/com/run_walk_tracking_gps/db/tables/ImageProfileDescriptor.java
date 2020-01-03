package com.run_walk_tracking_gps.db.tables;

import android.database.Cursor;

import com.run_walk_tracking_gps.connectionserver.NetworkHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageProfileDescriptor {

    public static final String TABLE_IMG = "PROFILE_IMAGE";
    public static final String NAME = "name";

    public static  final String CREATE_TABLE_PROFILE_IMAGE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_IMG + "( " +
                    UserDescriptor.ID_USER + " INTEGER PRIMARY KEY ," +
                    NAME + " TEXT NOT NULL, "+
                    "FOREIGN KEY ("+ UserDescriptor.ID_USER +") REFERENCES "+UserDescriptor.TABLE_USER +" ON DELETE CASCADE)";
    public static final String CREATE_INDEX_IMG =
            "CREATE UNIQUE INDEX IF NOT EXISTS FKhave on "+ TABLE_IMG +" ("+UserDescriptor.ID_USER+");";

    public static JSONObject from(Cursor cursor) throws JSONException {
        return new JSONObject().put(NetworkHelper.Constant.IMAGE,
                new JSONObject().put(NAME, cursor.getString(cursor.getColumnIndex(NAME)))
                        .put(NetworkHelper.Constant.IMG_ENCODE, "")
        );
    }
}
