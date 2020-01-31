package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.SqlLiteStatisticsDao;
import com.run_walk_tracking_gps.db.tables.WeightDescriptor;
import com.run_walk_tracking_gps.exception.DataException;
import com.run_walk_tracking_gps.gui.components.adapter.listview.ModifyWeightAdapter;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewInformationAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.components.dialog.WeightDialog;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.service.NetworkServiceHandler;
import com.run_walk_tracking_gps.utilities.EnumUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;

public class ModifyWeightActivity extends NewInformationActivity {

    private static final String TAG = ModifyWeightActivity.class.getName();

    private StatisticsData statisticsData;
    private StatisticsData oldStatisticsData;

    private boolean isLastWeight;

    public ModifyWeightActivity() {
        super(R.string.modify);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_weight, menu);
        if(isLastWeight){
            menu.findItem(R.id.delete_weight).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            final JSONObject bodyJson = new JSONObject().put(WeightDescriptor.ID_WEIGHT, statisticsData.getId());

            switch (item.getItemId()) {
                case R.id.save_weight: {
                    try{
                        if(!statisticsData.isSet())
                            throw new DataException(this, StatisticsData.class);

                        if(!statisticsData.getValue().equals(oldStatisticsData.getValue())){
                            bodyJson.put(WeightDescriptor.VALUE, statisticsData.getValue());
                        }

                        if(!statisticsData.getDate().equals(oldStatisticsData.getDate())){
                            bodyJson.put(WeightDescriptor.DATE, statisticsData.getDateStrDB());
                        }
                        Log.e(TAG, bodyJson.toString());
                        if(SqlLiteStatisticsDao.SqlLiteWeightDao.create(this).update(bodyJson)) {
                            Preferences.Session.update(this);

                            NetworkServiceHandler.getInstance(this, NetworkHelper.Constant.UPDATE,
                                    NetworkHelper.Constant.WEIGHT, bodyJson.toString())
                                    .bindService();

                            setResult(RESULT_OK, new Intent());
                        }else {
                            setResult(RESULT_CANCELED, new Intent());
                        }

                        finish();
                    }catch (JSONException je){
                        je.printStackTrace();
                    }
                    catch (DataException e) {
                        e.alert();
                    }
                }
                break;
                case R.id.delete_weight: {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.delete_weight_mex)
                            .setPositiveButton(R.string.delete, (dialog, id) -> {
                                if(SqlLiteStatisticsDao.SqlLiteWeightDao.create(this).delete(statisticsData.getId())){
                                    Preferences.Session.update(this);


                                    try {
                                        NetworkServiceHandler.getInstance(this, NetworkHelper.Constant.DELETE,
                                                NetworkHelper.Constant.WEIGHT, new JSONObject()
                                                        .put(WeightDescriptor.ID_WEIGHT, statisticsData.getId()).toString())
                                                .bindService();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    setResult(RESULT_OK, new Intent());
                                }else {
                                    setResult(RESULT_CANCELED, new Intent());
                                }
                                finish();
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
    protected void setModel() {
    }

    @Override
    public NewInformationAdapter getAdapterListView() {
        ModifyWeightAdapter adapter = null;
        if(getIntent()!=null) {
            oldStatisticsData = getIntent().getParcelableExtra(KeysIntent.MODIFY_WEIGHT);
            if (oldStatisticsData != null) {
                oldStatisticsData.setContext(this);
                adapter = new ModifyWeightAdapter(this, oldStatisticsData.toArrayListString(), onSetInfo());
                statisticsData = oldStatisticsData.clone();
            }
            isLastWeight = SqlLiteStatisticsDao.SqlLiteWeightDao.create(this).isOne();
        }
        return adapter ;
    }

    @Override
    public View.OnFocusChangeListener onSetInfo() {
        return (view, hasFocus) ->{
            if(hasFocus){
                final TextInputEditText detail = (TextInputEditText)view;
                final TextInputLayout detailTitle = (TextInputLayout) detail.getParent().getParent();

                StatisticsData.InfoWeight title = null;
                try{
                    title = (StatisticsData.InfoWeight) EnumUtilities.getEnumFromString(StatisticsData.InfoWeight.class , this, detailTitle.getHint().toString());
                }catch (Exception ignored){
                }
                Log.d(TAG, detailTitle.getHint().toString());

                switch (title){
                    case DATE:
                        DateTimePickerDialog.createDatePicker(ModifyWeightActivity.this,
                                (date, calendar) -> {
                                    detail.setText(date);
                                    statisticsData.setDate(calendar.getTime());
                                    Log.e(TAG, statisticsData.getDateStr());
                                }).show();
                        break;
                    case WEIGHT:
                        WeightDialog.create(ModifyWeightActivity.this, (weightMeasure) -> {
                            detail.setText(weightMeasure.toString());
                            statisticsData.getMeasure().setValue(false, weightMeasure.getValue(false));
                        }).show();
                        break;
                }
            }

        };
    }

    @Override
    public void onClickAddInfo() {
    }
}
