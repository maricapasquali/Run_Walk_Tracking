package com.run_walk_tracking_gps.db.dao;

import com.run_walk_tracking_gps.model.Measure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface StatisticsDao {

    JSONArray getAll(Measure.Type type) throws JSONException;

    interface  WeightDao{

        double getLast();

        long insert(JSONObject weight) throws JSONException;

        boolean insertAll(JSONArray weights) throws JSONException;

        boolean replaceAll(JSONArray weights) throws JSONException;

        boolean update(JSONObject weight) throws JSONException;

        boolean delete(int id);

        boolean isOne();
    }
}
