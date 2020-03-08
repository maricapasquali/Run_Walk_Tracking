package com.run_walk_tracking_gps.task;

import android.content.Context;
import android.os.AsyncTask;

import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.gui.DetailsWorkoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AutoWorkoutInsert extends AsyncTask<JSONObject, Void, Long> {

    private static final String TAG = AutoWorkoutInsert.class.getName();

    private Context context;
    private DetailsWorkoutActivity.OnInsertAutoWorkoutListener onInsertAutoWorkoutListener ;

    private AutoWorkoutInsert(Context context, DetailsWorkoutActivity.OnInsertAutoWorkoutListener onInsertAutoWorkoutListener){
        this.context = context;
        this.onInsertAutoWorkoutListener = onInsertAutoWorkoutListener;
    }

    public static AutoWorkoutInsert create(Context context, DetailsWorkoutActivity.OnInsertAutoWorkoutListener onInsertAutoWorkoutListener){
        return new AutoWorkoutInsert(context.getApplicationContext(), onInsertAutoWorkoutListener);
    }

    @Override
    protected Long doInBackground(JSONObject... jsonObjects) {
        try {
            return DaoFactory.getInstance(context).getWorkoutDao().insert(jsonObjects[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
        if(aLong!=-1L){
            onInsertAutoWorkoutListener.onSuccess(aLong);
        }else{
            onInsertAutoWorkoutListener.onFail();
        }
    }
}
