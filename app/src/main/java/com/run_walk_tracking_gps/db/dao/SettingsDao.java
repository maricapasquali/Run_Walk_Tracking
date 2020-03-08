package com.run_walk_tracking_gps.db.dao;

import org.json.JSONException;
import org.json.JSONObject;

public interface SettingsDao {

    JSONObject getSettings() throws JSONException;

    JSONObject getUnitMeasureDefault() throws JSONException;

    String getSportDefault() throws JSONException;

    String getTargetDefault() throws JSONException;

    boolean insert(JSONObject settings) throws JSONException;

    boolean update(Enum type, String value);


}
