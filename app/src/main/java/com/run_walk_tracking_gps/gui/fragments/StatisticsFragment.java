package com.run_walk_tracking_gps.gui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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

import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.gui.enumeration.FilterTime;
import com.run_walk_tracking_gps.gui.adapter.listview.StatisticsDataAdapter;
import com.run_walk_tracking_gps.gui.adapter.spinner.FilterWorkoutAdapterSpinner;
import com.run_walk_tracking_gps.gui.adapter.spinner.MeasureWorkoutAdapterSpinner;
import com.run_walk_tracking_gps.gui.enumeration.Measure;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private static final String TAG = StatisticsFragment.class.getName();

    private static final String LIST_WEIGHT_KEY = "All Weight";

    private Spinner spinner_measure;
    private Spinner spinner_time;
    private ListView list_data_filtered;
    private FloatingActionButton add_weight;

    private boolean isFirstQuery = true;

    private ArrayList<StatisticsData> statisticsData = new ArrayList<>();
    private OnAddWeightListener onAddWeightListener;
    private OnChangeFiltersListener onChangeFiltersListener;

    private StatisticsDataAdapter statisticsDataAdapter ;

    public StatisticsFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onAddWeightListener = (OnAddWeightListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnAddWeightListener");
        }

        try{
            onChangeFiltersListener = (OnChangeFiltersListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnChangeMeasureListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        // filter measure
        spinner_measure = view.findViewById(R.id.statistics_filter_data);
        spinner_measure.setAdapter(new MeasureWorkoutAdapterSpinner(getContext(), Measure.getMeasureStatistics(), false, true));
        spinner_time = view.findViewById(R.id.statistics_filter_time);
        spinner_time.setAdapter(new FilterWorkoutAdapterSpinner(getContext(), false, true));

        list_data_filtered = view.findViewById(R.id.list_data_filtered);
        add_weight = view.findViewById(R.id.add_weight);

        statisticsDataAdapter = new StatisticsDataAdapter(getContext(), statisticsData, (Measure)spinner_measure.getSelectedItem());
        list_data_filtered.setAdapter(statisticsDataAdapter);

        // filter measure
        spinner_measure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        Log.d(TAG, "measure Filter : onItemSelected");
                        final Measure measureSelected = (Measure) parent.getAdapter().getItem(position);
                        switch (measureSelected) {
                            case WEIGHT:
                                add_weight.setVisibility(View.VISIBLE);
                                break;
                            default:
                                add_weight.setVisibility(View.GONE);
                                break;
                        }

                        if(!isFirstQuery) updateGui(measureSelected,(FilterTime) spinner_time.getSelectedItem());
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
                        final Measure measureSelected = (Measure) spinner_measure.getSelectedItem();

                        if(!isFirstQuery){
                            updateGui(measureSelected,filterTimeSelected);
                        }
                        if(isFirstQuery) isFirstQuery = false;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        add_weight.setOnClickListener(v-> onAddWeightListener.onAddWeight());

        if(isFirstQuery){
            final Measure measureSelected = (Measure) spinner_measure.getSelectedItem();
            final FilterTime filterTimeSelected = (FilterTime) spinner_time.getSelectedItem();
            updateGui(measureSelected,filterTimeSelected);
        }

        return view;
    }

    // LISTENER CHE CHIEDERE ALL'ACTIVITY DI FARE QUERY COL LA MISURA SELEZIONATA
    private void updateGui(Measure measure, FilterTime filterTime){
        Log.d(TAG, "updateGui");
        statisticsData = onChangeFiltersListener.onChangeFilters(measure, filterTime);
        statisticsDataAdapter.updateStatisticsData(statisticsData, measure);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume");

        // TODO: 10/31/2019 RESET GUI SE SETTING SONO CAMBIATI

        if(spinner_measure!=null && spinner_measure.getSelectedItem()==Measure.WEIGHT && onAddWeightListener.newWeight()!=null){
            statisticsData.add(0,onAddWeightListener.newWeight());
            statisticsDataAdapter.updateStatisticsData(statisticsData, Measure.WEIGHT);
        }
    }

    public interface OnAddWeightListener{
        void onAddWeight();
        StatisticsData newWeight();
    }

    public interface OnChangeFiltersListener{
        ArrayList<StatisticsData> onChangeFilters(Measure measure, FilterTime filterTime);
    }
}
