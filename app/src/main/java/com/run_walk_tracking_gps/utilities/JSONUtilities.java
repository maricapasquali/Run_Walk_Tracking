package com.run_walk_tracking_gps.utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JSONUtilities {

    public static JSONObject merge(JSONObject json1, JSONObject json2) throws JSONException {
        if(json1==null) json1 = new JSONObject();
        final JSONObject mergedJSON = new JSONObject(json1, getNames(json1::keys).toArray(String[]::new));
        getNames(json2::keys).forEach(n -> {
            try {
                mergedJSON.put(n, json2.get(n));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        return mergedJSON;
    }

    private static Stream<String> getNames(Iterable<String> itr) {
        return StreamSupport.stream(itr.spliterator(), false);
    }
}
