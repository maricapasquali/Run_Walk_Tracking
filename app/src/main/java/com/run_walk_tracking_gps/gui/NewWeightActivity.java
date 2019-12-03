package com.run_walk_tracking_gps.gui;

import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Response;
import com.run_walk_tracking_gps.R;

import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.connectionserver.DefaultPreferencesUser;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewInformationAdapter;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewWeightAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;

import com.run_walk_tracking_gps.gui.components.dialog.WeightDialog;
import com.run_walk_tracking_gps.intent.KeysIntent;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.StatisticsData;


import org.json.JSONException;
import org.json.JSONObject;

public class NewWeightActivity extends NewInformationActivity implements NewInformationActivity.OnAddInfoListener, Response.Listener<JSONObject> {

    private final static String TAG = NewWeightActivity.class.getName();

    private final String UNSET = "Weight data doesn't correctly set !!";
    private StatisticsData statisticsData;

    public NewWeightActivity() {
        super(R.string.weight);
    }

    @Override
    protected void setModel() {
        statisticsData = StatisticsData.create(Measure.create(this, Measure.Type.WEIGHT));
    }

    @Override
    public NewInformationAdapter getAdapterListView() {
        return new NewWeightAdapter(this);
    }


    @Override
    public void onSetInfo(AdapterView<?> parent, View view, int position, long id) {
        final StatisticsData.InfoWeight title =(StatisticsData.InfoWeight) parent.getAdapter().getItem(position);
        final TextView detail = view.findViewById(R.id.detail_description);

        switch (title){
            case DATE:
                DateTimePickerDialog.create(NewWeightActivity.this, detail.getText().toString(),
                        (date, calendar) -> {
                            detail.setText(date);
                            statisticsData.setDate(calendar.getTime());
                        }, false).show();
                break;
            case WEIGHT:
                WeightDialog.create(NewWeightActivity.this, (weightMeasure)-> {
                   if(weightMeasure!=null){
                       detail.setText(weightMeasure.toString());
                       statisticsData.getMeasure().setValueFromGui(weightMeasure.getValueToGui());
                   }
                }).show();
                break;
        }
    }

    @Override
    public void onClickAddInfo() {

        try{
            if(!statisticsData.isSet())
                throw new NullPointerException(UNSET);


            final JSONObject bodyJson = new JSONObject().put(HttpRequest.Constant.ID_USER, Integer.valueOf(DefaultPreferencesUser.getIdUserLogged(this)))
                    .put(HttpRequest.Constant.VALUE, statisticsData.getValue())
                    .put(HttpRequest.Constant.DATE, statisticsData.getDate());

            if(!HttpRequest.requestNewWeight(this, bodyJson, this)){
                Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
            }

        }catch (NullPointerException e){
            Log.d(TAG, e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if(HttpRequest.someError(response)){
                Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
            }else {
                int id_statistics = response.getInt(HttpRequest.Constant.ID_WEIGHT);
                statisticsData.setId(id_statistics);
                // save and send
                final Intent newWeightIntent = new Intent();
                newWeightIntent.putExtra(KeysIntent.NEW_WEIGHT, statisticsData);
                setResult(RESULT_OK, newWeightIntent);
                finish();
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
