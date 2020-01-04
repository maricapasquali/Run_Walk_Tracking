package com.run_walk_tracking_gps.gui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.run_walk_tracking_gps.R;

import com.run_walk_tracking_gps.controller.ErrorQueue;
import com.run_walk_tracking_gps.db.dao.SqlLiteStatisticsDao;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.model.enumerations.FilterTime;
import com.run_walk_tracking_gps.gui.components.adapter.listview.StatisticsDataAdapter;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.FilterAdapterSpinner;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.MeasureWorkoutAdapterSpinner;
import com.run_walk_tracking_gps.utilities.FilterUtilities;

import org.json.JSONException;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private static final String TAG = StatisticsFragment.class.getName();

    private Spinner spinner_measure;
    private Spinner spinner_time;
    private FloatingActionButton add_weight;
    private StatisticsDataAdapter statisticsDataAdapter ;

    private OnWeightListener onWeightListener;


    public StatisticsFragment() {
        super();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onWeightListener = (OnWeightListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement onWeightListener");
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        // filter measure
        spinner_measure = view.findViewById(R.id.statistics_filter_data);
        spinner_measure.setAdapter(new MeasureWorkoutAdapterSpinner(getContext(), Measure.Type.getMeasureStatistics(), false, true));
        spinner_time = view.findViewById(R.id.statistics_filter_time);
        spinner_time.setAdapter(new FilterAdapterSpinner(getContext(), false));

        ListView list_data_filtered = view.findViewById(R.id.list_data_filtered);
        add_weight = view.findViewById(R.id.add_weight);

        list_data_filtered.setOnItemLongClickListener((parent, view1, position, id) -> {
            if(((Measure.Type)spinner_measure.getSelectedItem()).equals(Measure.Type.WEIGHT) && position > 0){
                Log.d(TAG,  "DEEP TOUCH :  " + (StatisticsData)parent.getAdapter().getItem(position));
                onWeightListener.modifyWeight((StatisticsData)parent.getAdapter().getItem(position));
            }
            return false;
        });

        statisticsDataAdapter = new StatisticsDataAdapter(getContext(), getStatistics(Measure.Type.MIDDLE_SPEED));
        list_data_filtered.setAdapter(statisticsDataAdapter);

        // filter measure
        spinner_measure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Log.d(TAG, "measure Filter : onItemSelected");
                    final Measure.Type measureSelected = (Measure.Type) parent.getAdapter().getItem(position);
                    final FilterTime filterTimeSelected = (FilterTime) spinner_time.getSelectedItem();

                    switch (measureSelected) {
                        case WEIGHT:
                            add_weight.setVisibility(View.VISIBLE);
                            break;
                        default:
                            add_weight.setVisibility(View.GONE);
                            break;
                    }

                    update(measureSelected, filterTimeSelected);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


        // filter time
        spinner_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "Time Filter : onItemSelected");
                        final FilterTime filterTimeSelected = (FilterTime) parent.getAdapter().getItem(position);
                        final Measure.Type measureSelected = (Measure.Type) spinner_measure.getSelectedItem();
                        update(measureSelected, filterTimeSelected);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        add_weight.setOnClickListener(v-> onWeightListener.onAddWeight());

        return view;
    }

    private void update(Measure.Type measure, FilterTime filterTime){
        Log.d(TAG, "updateGui :Measure = " +measure + ", Time = " +filterTime);
        checkSizeStatistics(measure);
        statisticsDataAdapter.updateStatisticsData(
                FilterUtilities.createListFilteredStatisticsData(getContext(), getStatistics(measure), filterTime));
    }

    private void checkSizeStatistics(Measure.Type measure){
        final boolean isVisible = getStatistics(measure).size()>0;
        spinner_time.setVisibility(isVisible?  View.VISIBLE : View.GONE );
        if(isVisible) Log.d(TAG, "No Statistics");
    }

    private ArrayList<StatisticsData> getStatistics(Measure.Type measure){
        try {
            return StatisticsData.createList(getContext(), measure, SqlLiteStatisticsDao.create(getContext()).getAll(measure));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume");
        ErrorQueue.getErrors(getContext());
        // TODO: 1/3/2020 ADD SYNC SERVICE ONRESUME 
        //SyncServiceHandler.create(getContext()).start();


        Measure.Type measureSelected = (Measure.Type) spinner_measure.getSelectedItem();
        statisticsDataAdapter.updateStatisticsData(getStatistics(measureSelected));

        // REQUEST TO DB LOCALE
        if (spinner_measure != null && measureSelected.equals(Measure.Type.WEIGHT)) {
            Log.e(TAG, "onResume : weights");
            statisticsDataAdapter.updateStatisticsData(getStatistics(Measure.Type.WEIGHT));
        }
    }

    public interface OnWeightListener{
        void onAddWeight();
        void modifyWeight(StatisticsData statisticsData);
    }
}
