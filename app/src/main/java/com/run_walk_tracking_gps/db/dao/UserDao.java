package com.run_walk_tracking_gps.db.dao;

import org.json.JSONException;
import org.json.JSONObject;

public interface UserDao {

    JSONObject getUser() throws JSONException;

    boolean insert(JSONObject user) throws JSONException;

    boolean update(JSONObject user) throws JSONException;

    boolean delete();
}
