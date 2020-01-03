package com.run_walk_tracking_gps.db.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface WorkoutDao {

    JSONArray getAll();

    long insert(JSONObject workout) throws JSONException;

    boolean insertAll(JSONArray workouts) throws JSONException;

    boolean replaceAll(JSONArray workouts) throws JSONException;

    boolean update(JSONObject workout) throws JSONException;

    boolean delete(int id);
}
