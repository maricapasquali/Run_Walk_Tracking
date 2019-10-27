package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.TextView;
import android.widget.Toast;


import com.run_walk_tracking_gps.R;

import com.run_walk_tracking_gps.gui.adapter.listview.NewInformationAdapter;
import com.run_walk_tracking_gps.gui.adapter.listview.NewWeightAdapter;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;

import com.run_walk_tracking_gps.gui.dialog.WeightDialog;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.gui.enumeration.InfoWeight;

public class NewWeightActivity extends NewInformationActivity implements NewInformationActivity.OnAddInfoListener {

    private final static String TAG = NewWeightActivity.class.getName();

    private StatisticsData statisticsData = new StatisticsData();

    public NewWeightActivity() {
        super(R.string.weight);
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
                WeightDialog.create(NewWeightActivity.this, (weightString, weight)-> {
                    detail.setText(weightString);
                    statisticsData.setStatisticData(weight);
                }).show();
                break;
        }
    }

    @Override
    public void onClickAddInfo() {

        try{
            if(!statisticsData.isSet())
                throw new NullPointerException("Weight data doesn't correctly set !! " + statisticsData);

            // save and send
            final Intent newWeightIntent = new Intent();
            newWeightIntent.putExtra(getString(R.string.new_weight), statisticsData);
            setResult(RESULT_OK, newWeightIntent);
            finish();
        }catch (NullPointerException e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
