package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Response;
import com.run_walk_tracking_gps.R;

import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.adapter.listview.NewInformationAdapter;
import com.run_walk_tracking_gps.gui.adapter.listview.NewWeightAdapter;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;

import com.run_walk_tracking_gps.gui.dialog.WeightDialog;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.gui.enumeration.InfoWeight;

import org.json.JSONException;
import org.json.JSONObject;

public class NewWeightActivity extends NewInformationActivity implements NewInformationActivity.OnAddInfoListener, Response.Listener<JSONObject> {

    private final static String TAG = NewWeightActivity.class.getName();

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
        final InfoWeight title =(InfoWeight) parent.getAdapter().getItem(position);
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
                    detail.setText(weightMeasure.toString(this));
                    statisticsData.setValue(weightMeasure.getValue());
                    // TODO: 11/3/2019 GESTIONE CONVERSIONE
                }).show();
                break;
        }
    }

    @Override
    public void onClickAddInfo() {

        try{
            if(!statisticsData.isSet())
                throw new NullPointerException("Weight data doesn't correctly set !! " + statisticsData);


            final JSONObject bodyJson = new JSONObject().put(FieldDataBase.ID_USER.toName(), Integer.valueOf(Preferences.getIdUserLogged(this)))
                    .put(FieldDataBase.VALUE.toName(), statisticsData.getValue())
                    .put(FieldDataBase.DATE.toName(), statisticsData.getDate());

            if(!HttpRequest.requestNewWeight(this, bodyJson, this)){
                Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
            }

        }catch (NullPointerException e){
            Log.e(TAG, e.getMessage());
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
                int id_statistics = response.getInt(FieldDataBase.ID_WEIGHT.toName());
                statisticsData.setId(id_statistics);
                // save and send
                final Intent newWeightIntent = new Intent();
                newWeightIntent.putExtra(getString(R.string.new_weight), statisticsData);
                setResult(RESULT_OK, newWeightIntent);
                finish();
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }


}
