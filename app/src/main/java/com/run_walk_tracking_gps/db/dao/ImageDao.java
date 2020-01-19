package com.run_walk_tracking_gps.db.dao;

import org.json.JSONException;
import org.json.JSONObject;

public interface ImageDao {

    JSONObject getImage() throws JSONException;

    boolean save(JSONObject image) throws JSONException;

    boolean delete();

}
