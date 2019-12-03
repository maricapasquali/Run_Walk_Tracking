package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.gui.components.adapter.listview.ModifyWeightAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.components.dialog.WeightDialog;
import com.run_walk_tracking_gps.intent.KeysIntent;
import com.run_walk_tracking_gps.model.StatisticsData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Stream;


public class ModifyWeightActivity extends CommonActivity implements Response.Listener<JSONObject>{

    private static final String TAG = ModifyWeightActivity.class.getName();
    private static final String UNSET = "Weight data doesn't correctly set !! ";

    private ListView listView;

    private ModifyWeightAdapter adapter;
    private StatisticsData statisticsData;
    private StatisticsData oldStatisticsData;

    private boolean isLastWeight;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_info);

        getSupportActionBar().setTitle(R.string.modify);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.info_to_add);

        if(getIntent()!=null){
            oldStatisticsData = getIntent().getParcelableExtra(KeysIntent.MODIFY_WEIGHT);
            if(oldStatisticsData!=null){
                oldStatisticsData.setContext(this);
                adapter = new ModifyWeightAdapter(this, oldStatisticsData.toArrayListString()) ;
                listView.setAdapter(adapter);
                statisticsData = oldStatisticsData.clone();
            }
            isLastWeight = getIntent().getBooleanExtra(KeysIntent.IS_LAST_WEIGHT, false);
            Log.d(TAG, "Weights size = 1 : "+ isLastWeight);
        }
    }

    @Override
    protected void listenerAction() {
        listView.setOnItemClickListener((parent, view, position, id) ->{
            final StatisticsData.InfoWeight title =(StatisticsData.InfoWeight) parent.getAdapter().getItem(position);
            final TextView detail = view.findViewById(R.id.detail_description);

            switch (title){
                case DATE:
                    DateTimePickerDialog.create(ModifyWeightActivity.this, detail.getText().toString(),
                            (date, calendar) -> {
                                detail.setText(date);
                                statisticsData.setDate(calendar.getTime());
                                Log.e(TAG, statisticsData.getDateStr());
                            }, false).show();
                    break;
                case WEIGHT:
                    WeightDialog.create(ModifyWeightActivity.this, (weightMeasure) -> {
                        detail.setText(weightMeasure.toString());
                        statisticsData.getMeasure().setValueFromGui(weightMeasure.getValueToGui());
                    }).show();
                    break;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_weight, menu);
        if(isLastWeight){
            menu.findItem(R.id.delete_weight).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            final JSONObject bodyJson = new JSONObject().put(HttpRequest.Constant.ID_WEIGHT, statisticsData.getId());

            switch (item.getItemId()) {
                case R.id.save_weight: {
                    try{
                        if(!statisticsData.isSet())
                            throw new NullPointerException(UNSET);

                        if(!statisticsData.getValue().equals(oldStatisticsData.getValue())){
                            bodyJson.put(HttpRequest.Constant.VALUE, statisticsData.getValue());
                        }

                        if(!statisticsData.getDate().equals(oldStatisticsData.getDate())){
                            bodyJson.put(HttpRequest.Constant.DATE, statisticsData.getDate());
                        }
                        Log.e(TAG, bodyJson.toString());

                        if(!HttpRequest.requestUpdateWeight(this, bodyJson, this))
                        {
                            Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                        }

                    }catch (NullPointerException e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (JSONException je){
                        je.printStackTrace();
                    }
                }
                break;
                case R.id.delete_weight: {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.delete_weight_mex)
                            .setPositiveButton(R.string.delete, (dialog, id) -> {
                                if(!HttpRequest.requestDeleteWeight(this, bodyJson, this))
                                {
                                    Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                                }
                            }).setNegativeButton(R.string.cancel, null).create().show();
                }
                break;
                case android.R.id.home: super.onBackPressed();
                break;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(JSONObject response) {

            if(HttpRequest.someError(response)){
                Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
            }else {
                final Intent modifyWeightIntent = new Intent();
                Log.d(TAG, response.toString());

                if(Stream.of(response.keys()).anyMatch(i -> i.next().equals(HttpRequest.Constant.UPDATE))){
                    modifyWeightIntent.putExtra(KeysIntent.CHANGED_WEIGHT, statisticsData);
                    Log.d(TAG, statisticsData.toString());
                }

                if(Stream.of(response.keys()).anyMatch(i -> i.next().equals(HttpRequest.Constant.DELETE))){
                    modifyWeightIntent.putExtra(KeysIntent.DELETE_WEIGHT, statisticsData.getId());
                    Log.d(TAG, "Id to delete : " +statisticsData.getId());
                }

                setResult(RESULT_OK, modifyWeightIntent);
                finish();

            }

    }
}
