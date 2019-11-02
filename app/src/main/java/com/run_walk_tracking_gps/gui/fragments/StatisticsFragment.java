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
import com.run_walk_tracking_gps.model.Workout;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private static final String TAG = StatisticsFragment.class.getName();

    private static final String LIST_WEIGHT_KEY = "All Weight";
    private static final String LIST_WORKOUT_KEY = "All Workouts";

    private Spinner spinner_measure;
    private Spinner spinner_time;
    private ListView list_data_filtered;
    private FloatingActionButton add_weight;

    private StatisticsDataAdapter statisticsDataAdapter ;

    private ArrayList<StatisticsData> weights = new ArrayList<>();
    private ArrayList<Workout> workouts = new ArrayList<>();

    private OnAddWeightListener onAddWeightListener;


    public StatisticsFragment() {
        super();
    }

    public static Fragment createWithArgument(ArrayList<Workout> workouts, ArrayList<StatisticsData> statisticsWeight) {
        final StatisticsFragment statisticsFragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_WORKOUT_KEY, workouts);
        args.putParcelableArrayList(LIST_WEIGHT_KEY, statisticsWeight);
        statisticsFragment.setArguments(args);
        return statisticsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        Bundle args = getArguments();
        if(args!=null){
            if(args.getParcelableArrayList(LIST_WORKOUT_KEY)!=null) {
                workouts = getArguments().getParcelableArrayList(LIST_WORKOUT_KEY);
            }

            if(args.getParcelableArrayList(LIST_WEIGHT_KEY)!=null) {
                weights = getArguments().getParcelableArrayList(LIST_WEIGHT_KEY);
            }
            Log.d(TAG, "List Workouts = "+ workouts);
            Log.d(TAG, "List Weights = "+ weights);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onAddWeightListener = (OnAddWeightListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnAddWeightListener");
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


        statisticsDataAdapter = new StatisticsDataAdapter(getContext(), middleSpeedStatistics(), (Measure)spinner_measure.getSelectedItem());
        list_data_filtered.setAdapter(statisticsDataAdapter);

        // filter measure
        spinner_measure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Log.d(TAG, "measure Filter : onItemSelected");
                    final Measure measureSelected = (Measure) parent.getAdapter().getItem(position);
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
                        final Measure measureSelected = (Measure) spinner_measure.getSelectedItem();
                        update(measureSelected, filterTimeSelected);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        add_weight.setOnClickListener(v-> onAddWeightListener.onAddWeight());


        return view;
    }

    private void update(Measure measure, FilterTime filterTime){
        Log.e(TAG, "updateGui :Measure = " +measure + ", Time = " +filterTime);

        switch (filterTime){
            case ALL: statisticsDataAdapter.updateStatisticsData(getStatistics(measure), measure);
                break;
            default: statisticsDataAdapter.updateStatisticsData(new ArrayList<>(), measure);
                break;
        }

    }

    private ArrayList<StatisticsData> middleSpeedStatistics(){
        return workouts.stream().filter(w -> w.getMiddleSpeed()>0).collect(ArrayList::new,
                (s, w)-> s.add(new StatisticsData(w.getDate(), w.getMiddleSpeed())), ArrayList::addAll);
    }

    private ArrayList<StatisticsData> weightsStatistics(){
        return weights;
    }

    private ArrayList<StatisticsData> energyStatistics(){
        return  workouts.stream().filter(w -> w.getCalories()>0).collect(ArrayList::new,
                (s, w)-> s.add(new StatisticsData(w.getDate(), w.getCalories())), ArrayList::addAll);
    }

    private ArrayList<StatisticsData> distanceStatistics(){
        return   workouts.stream().filter(w -> w.getDistance()>0).collect(ArrayList::new,
                (s, w)-> s.add(new StatisticsData(w.getDate(), w.getDistance())), ArrayList::addAll);
    }

    private ArrayList<StatisticsData> getStatistics(Measure measure){
        switch (measure){
            case WEIGHT:
                return weightsStatistics();
            case MIDDLE_SPEED:
                return middleSpeedStatistics();
            case ENERGY:
               return energyStatistics();
            case DISTANCE:
              return distanceStatistics();
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume");

        // TODO: 10/31/2019 RESET GUI SE SETTING SONO CAMBIATI

        if(spinner_measure!=null && spinner_measure.getSelectedItem()==Measure.WEIGHT && onAddWeightListener.newWeight()!=null){
            weights.add(0, onAddWeightListener.newWeight());
            statisticsDataAdapter.updateStatisticsData(weights, Measure.WEIGHT);
        }


    }

    public interface OnAddWeightListener{
        void onAddWeight();
        StatisticsData newWeight();
    }

}
