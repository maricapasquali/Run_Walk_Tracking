package com.run_walk_tracking_gps.gui.fragments;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.ApplicationActivity;
import com.run_walk_tracking_gps.gui.adapter.spinner.FilterWorkoutAdapterSpinner;
import com.run_walk_tracking_gps.gui.adapter.listview.WorkoutsAdapter;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.enumerations.Sport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class WorkoutsFragment extends Fragment {

    private static final String TAG = WorkoutsFragment.class.getName();
    private static final String LIST_WORKOUT_KEY  = "Workouts History";

    private Spinner filter;
    private ListView workoutsView;
    private ImageButton addManualWorkout;

    private WorkoutsAdapter workoutsAdapter;

    private OnWorkOutSelectedListener onWorkOutSelectedListener;
    private OnManualAddClickedListener onManualAddClickedListener;
    private OnDeleteWorkoutClickedListener onDeleteWorkoutClickedListener;
    private ArrayList<Workout> workouts = new ArrayList<>();


    public WorkoutsFragment() {
        super();
    }


    public static WorkoutsFragment createWithArgument(ArrayList<Workout> workouts){
        final WorkoutsFragment workoutsFragment = new WorkoutsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_WORKOUT_KEY, workouts);
        workoutsFragment.setArguments(args);
        return workoutsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");

        try {
            onWorkOutSelectedListener = (OnWorkOutSelectedListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnWorkOutSelectedListener");
        }
        try {
            onManualAddClickedListener =(OnManualAddClickedListener)context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnManualAddClickedListener");
        }

        try {
            onDeleteWorkoutClickedListener =(OnDeleteWorkoutClickedListener)context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDeleteWorkoutClickedListener");
        }
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
            Log.d(TAG, "List Workouts = "+ workouts);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_workouts, container, false);

        // Filter
        filter = view.findViewById(R.id.filter_workouts);
        filter.setAdapter(new FilterWorkoutAdapterSpinner(getContext(), false, true));

        // list workouts
        workoutsView = view.findViewById(R.id.list_workouts);
        workoutsAdapter = new WorkoutsAdapter(getContext(), workouts);
        workoutsView.setAdapter(workoutsAdapter);

        /* Passaggio delle informazioni(visibili nella "Lista") del workout selezionato */
        workoutsView.setOnItemClickListener((parent, view1, position, id) ->
                onWorkOutSelectedListener.onWorkOutSelected(workouts.get(position)));


        // Add Manual Workout
        addManualWorkout = view.findViewById(R.id.add_workout);
        addManualWorkout.setOnClickListener(v -> onManualAddClickedListener.onManualAddClickedListener());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume");

        // TODO: 10/31/2019 RESET GUI SE SETTING SONO CAMBIATI

        if(onManualAddClickedListener.newWorkout()!=null){
            workouts.add(0, onManualAddClickedListener.newWorkout());
            workouts.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

            workoutsAdapter.notifyDataSetChanged();
            onManualAddClickedListener.resetNewWorkout();

            Log.d(TAG, "Gui Update : New Manual Workout = " +onManualAddClickedListener.newWorkout());
        }
        if(onWorkOutSelectedListener.workoutChanged()!=null){
            final Workout newW = onWorkOutSelectedListener.workoutChanged();
            workouts.stream().filter(w -> w.getIdWorkout() == newW.getIdWorkout())
                             .findFirst().ifPresent(w -> workouts.remove(w));
            workouts.add(newW);
            workouts.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

            workoutsAdapter.notifyDataSetChanged();
            onWorkOutSelectedListener.resetWorkoutChanged();

            Log.d(TAG, "Gui Update : Workout Changed = " +newW);
        }

        if(onDeleteWorkoutClickedListener.id_workout_deleted()>0){
            final int id_workout_deleted = onDeleteWorkoutClickedListener.id_workout_deleted();
            workouts.stream().filter(w -> w.getIdWorkout() == id_workout_deleted)
                    .findFirst().ifPresent(w -> workouts.remove(w));
            workoutsAdapter.notifyDataSetChanged();
            onDeleteWorkoutClickedListener.resetWorkoutDelete();
            Log.d(TAG, "Gui Update : Workout Deleted = " +id_workout_deleted);
        }
    }

    public interface OnWorkOutSelectedListener{
        void onWorkOutSelected(Workout workout);
        Workout workoutChanged();
        void resetWorkoutChanged();

    }

    public interface OnManualAddClickedListener{
        void onManualAddClickedListener();
        Workout newWorkout();
        void resetNewWorkout();
    }

    public interface OnDeleteWorkoutClickedListener{
        int id_workout_deleted();
        void resetWorkoutDelete();
    }
}
