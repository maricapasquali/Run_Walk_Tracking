package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.gui.adapter.listview.ModifyWeightAdapter;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.dialog.WeightDialog;
import com.run_walk_tracking_gps.gui.enumeration.InfoWeight;
import com.run_walk_tracking_gps.model.StatisticsData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Stream;


public class ModifyWeightActivity extends CommonActivity implements Response.Listener<JSONObject>{

    private static final String TAG = ModifyWeightActivity.class.getName();

    private ListView listView;

    private ModifyWeightAdapter adapter;
    private StatisticsData statisticsData;
    private StatisticsData oldStatisticsData;

    @Override
    protected void init() {
        setContentView(R.layout.activity_add_info);

        getSupportActionBar().setTitle(R.string.modify);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.info_to_add);

        if(getIntent()!=null){
            oldStatisticsData = getIntent().getParcelableExtra(getString(R.string.modify_weight));
            if(oldStatisticsData!=null){
                adapter = new ModifyWeightAdapter(this, oldStatisticsData.toArrayListString(this)) ;
                listView.setAdapter(adapter);
                statisticsData = oldStatisticsData.clone();
            }
        }
    }

    @Override
    protected void listenerAction() {
        listView.setOnItemClickListener((parent, view, position, id) ->{
            final InfoWeight title =(InfoWeight) parent.getAdapter().getItem(position);
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
                        detail.setText(weightMeasure.toString(this));
                        statisticsData.setValue(weightMeasure.getValue());
                        // TODO: 11/3/2019 GESTIONE CONVERSIONE
                    }).show();
                    break;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_weight, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            final JSONObject bodyJson = new JSONObject().put(FieldDataBase.ID_WEIGHT.toName(), statisticsData.getId());

            switch (item.getItemId()) {
                case R.id.save_weight: {
                    try{
                        if(!statisticsData.isSet())
                            throw new NullPointerException("Weight data doesn't correctly set !! " + statisticsData);

                        if(!statisticsData.getValue().equals(oldStatisticsData.getValue())){
                            bodyJson.put(FieldDataBase.VALUE.toName(), statisticsData.getValue());
                        }

                        if(!statisticsData.getDate().equals(oldStatisticsData.getDate())){
                            bodyJson.put(FieldDataBase.DATE.toName(), statisticsData.getDate());
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

                if(Stream.of(response.keys()).anyMatch(i -> i.next().equals("update"))){
                    modifyWeightIntent.putExtra(getString(R.string.modify_weight), statisticsData);
                }

                if(Stream.of(response.keys()).anyMatch(i -> i.next().equals("delete"))){
                    modifyWeightIntent.putExtra(getString(R.string.delete_weight), statisticsData.getId());
                }

                setResult(RESULT_OK, modifyWeightIntent);
                finish();

            }

    }
}
