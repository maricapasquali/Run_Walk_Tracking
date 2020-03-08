package com.run_walk_tracking_gps.gui;

import android.content.Intent;

import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.db.tables.WeightDescriptor;
import com.run_walk_tracking_gps.exception.DataException;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewInformationAdapter;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewWeightAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.components.dialog.WeightDialog;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.service.NetworkServiceHandler;
import com.run_walk_tracking_gps.utilities.EnumUtilities;

import org.json.JSONException;
import org.json.JSONObject;

public class NewWeightActivity extends NewInformationActivity {

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
        return new NewWeightAdapter(this, onSetInfo());
    }

    @Override
    public View.OnFocusChangeListener onSetInfo() {
        return (view, hasFocus) -> {
            if(hasFocus){
                final TextInputEditText detail = (TextInputEditText)view;
                final TextInputLayout detailTitle = (TextInputLayout) detail.getParent().getParent();

                StatisticsData.InfoWeight title = null;
                try{
                    title = (StatisticsData.InfoWeight) EnumUtilities.getEnumFromString(StatisticsData.InfoWeight.class ,
                            this, detailTitle.getHint().toString());
                }catch (Exception ignored){
                }
                Log.d(TAG, detailTitle.getHint().toString());

                switch (title){
                    case DATE:
                        DateTimePickerDialog.createDatePicker(NewWeightActivity.this,
                                (date, calendar) -> {
                                    detail.setText(date);
                                    statisticsData.setDate(calendar.getTime());
                                }).show();
                        break;
                    case WEIGHT:
                        WeightDialog.create(NewWeightActivity.this, (weightMeasure)-> {
                            if(weightMeasure!=null){
                                detail.setText(weightMeasure.toString());
                                statisticsData.getMeasure().setValue(false, weightMeasure.getValue(false));
                            }
                        }).show();
                        break;
                }
            }
        };
    }

    @Override
    public void onClickAddInfo() {
        try{
            if(!statisticsData.isSet())
                throw new DataException(this, StatisticsData.class);

            final JSONObject bodyJson = new JSONObject()
                    .put(UserDescriptor.ID_USER, Preferences.Session.getIdUser(this))
                    .put(WeightDescriptor.VALUE, statisticsData.getValue())
                    .put(WeightDescriptor.DATE, statisticsData.getDateStrDB());

            long id_weight = DaoFactory.getInstance(this).getWeightDao().insert(bodyJson);
            if(id_weight!=-1){
                Preferences.Session.update(this);
                NetworkServiceHandler.getInstance(this, NetworkHelper.Constant.INSERT,
                        NetworkHelper.Constant.WEIGHT, bodyJson.put(WeightDescriptor.ID_WEIGHT, id_weight).toString())
                        .startService();

                setResult(RESULT_OK, new Intent());
            }else {
                setResult(RESULT_CANCELED, new Intent());
            }
            finish();
        }
        catch (JSONException je){
            je.printStackTrace();
        }
        catch (DataException e) {
            e.alert();
        }
    }
}
