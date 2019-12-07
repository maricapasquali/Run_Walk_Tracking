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

import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.builder.StatisticsBuilder;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.model.enumerations.FilterTime;
import com.run_walk_tracking_gps.gui.components.adapter.listview.StatisticsDataAdapter;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.FilterAdapterSpinner;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.MeasureWorkoutAdapterSpinner;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.utilities.FilterUtilities;

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

    private OnWeightListener onWeightListener;


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
                workouts.clear();
                workouts = getArguments().getParcelableArrayList(LIST_WORKOUT_KEY);
            }

            if(args.getParcelableArrayList(LIST_WEIGHT_KEY)!=null) {
                weights.clear();
                weights = getArguments().getParcelableArrayList(LIST_WEIGHT_KEY);
            }
            //Log.d(TAG, "List Workouts = "+ workouts);
            Log.d(TAG, "List Weights = "+ weights);
        }
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        // filter measure
        spinner_measure = view.findViewById(R.id.statistics_filter_data);
        spinner_measure.setAdapter(new MeasureWorkoutAdapterSpinner(getContext(), Measure.Type.getMeasureStatistics(), false, true));
        spinner_time = view.findViewById(R.id.statistics_filter_time);
        spinner_time.setAdapter(new FilterAdapterSpinner(getContext(), false));

        list_data_filtered = view.findViewById(R.id.list_data_filtered);
        add_weight = view.findViewById(R.id.add_weight);

        list_data_filtered.setOnItemLongClickListener((parent, view1, position, id) -> {
            if(((Measure.Type)spinner_measure.getSelectedItem()).equals(Measure.Type.WEIGHT) && position > 0){
                Log.d(TAG,  "DEEP TOUCH :  " + (StatisticsData)parent.getAdapter().getItem(position));
                onWeightListener.modifyWeight((StatisticsData)parent.getAdapter().getItem(position));
            }

            return false;
        });


        statisticsDataAdapter = new StatisticsDataAdapter(getContext(), middleSpeedStatistics());
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

    private ArrayList<StatisticsData> middleSpeedStatistics(){
        return workouts.stream().filter(w -> !Measure.isNullOrEmpty(w.getMiddleSpeed())).collect(ArrayList::new,
                (s, w)-> s.add(StatisticsBuilder.createStatisticMiddleSpeed(getContext())
                        .setDate(w.getDate()).setValue(w.getMiddleSpeed().getValue(true)).build()), ArrayList::addAll);
    }

    private ArrayList<StatisticsData> weightsStatistics(){
        return weights;
    }

    private ArrayList<StatisticsData> energyStatistics(){
        return workouts.stream().filter(w -> !Measure.isNullOrEmpty(w.getCalories())).collect(ArrayList::new,
                (s, w)-> s.add(StatisticsBuilder.createStatisticEnergy(getContext())
                        .setDate(w.getDate()).setValue(w.getCalories().getValue(true)).build()), ArrayList::addAll);
    }

    private ArrayList<StatisticsData> distanceStatistics(){
        return workouts.stream().filter(w -> !Measure.isNullOrEmpty(w.getDistance())).collect(ArrayList::new,
                (s, w)-> s.add(StatisticsBuilder.createStatisticDistance(getContext())
                        .setDate(w.getDate()).setValue(w.getDistance().getValue(true)).build()), ArrayList::addAll);

    }

    private ArrayList<StatisticsData> getStatistics(Measure.Type measure){
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

        Measure.Type measureSelected = (Measure.Type)spinner_measure.getSelectedItem();
        statisticsDataAdapter.updateStatisticsData(getStatistics(measureSelected));


        if(spinner_measure!=null && measureSelected.equals(Measure.Type.WEIGHT)){
            if(onWeightListener.newWeight()!=null){
                weights.add(0, onWeightListener.newWeight());
                weights.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                Log.d(TAG, "Add weight: " +weights.toString());
                statisticsDataAdapter.updateStatisticsData(weights);
                onWeightListener.resetAddWeight();
            }

            if(onWeightListener.changedWeight()!=null){
                StatisticsData changedWeight = onWeightListener.changedWeight();

                weights.stream().filter(w -> w.getId() == changedWeight.getId())
                        .findFirst().ifPresent(w -> weights.remove(w));

                weights.add(changedWeight);
                weights.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                Log.d(TAG, "Changed weight : " +weights.toString());
                statisticsDataAdapter.updateStatisticsData(weights);
                onWeightListener.resetChangedWeight();
            }

            if(onWeightListener.deletedWeight()>0){
                int id_changed_weight = onWeightListener.deletedWeight();
                weights.stream().filter(w -> w.getId() == id_changed_weight)
                        .findFirst().ifPresent(w -> weights.remove(w));
                Log.d(TAG, "Delete weight : " +weights.toString());
                statisticsDataAdapter.updateStatisticsData(weights);
                onWeightListener.resetDeletedWeight();
            }
        }
    }

    public interface OnWeightListener{
        void onAddWeight();
        StatisticsData newWeight();
        void resetAddWeight();

        void modifyWeight(StatisticsData statisticsData);
        StatisticsData changedWeight();
        void resetChangedWeight();

        int deletedWeight();
        void resetDeletedWeight();
    }
}
