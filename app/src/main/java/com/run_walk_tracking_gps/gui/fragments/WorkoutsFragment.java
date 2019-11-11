package com.run_walk_tracking_gps.gui.fragments;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.adapter.listview.WorkoutsFilterAdapter;
import com.run_walk_tracking_gps.gui.adapter.spinner.FilterWorkoutAdapterSpinner;
import com.run_walk_tracking_gps.gui.enumeration.FilterTime;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.utilities.FilterUtilities;

import java.util.ArrayList;

public class WorkoutsFragment extends Fragment {

    private static final String TAG = WorkoutsFragment.class.getName();
    private static final String LIST_WORKOUT_KEY  = "Workouts History";

    private Spinner filter;
    private ExpandableListView workoutsViewExpandable;
    private ImageButton addManualWorkout;

    private WorkoutsFilterAdapter workoutsFilterAdapter;

    private OnWorkOutSelectedListener onWorkOutSelectedListener;
    private OnManualAddClickedListener onManualAddClickedListener;
    private OnDeleteWorkoutClickedListener onDeleteWorkoutClickedListener;
    private ArrayList<Workout> workouts = new ArrayList<>();

    public WorkoutsFragment() {
        super();
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

    public static WorkoutsFragment createWithArgument(ArrayList<Workout> workouts){
        final WorkoutsFragment workoutsFragment = new WorkoutsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_WORKOUT_KEY, workouts);
        workoutsFragment.setArguments(args);
        return workoutsFragment;
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
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setAdapter((FilterTime) parent.getAdapter().getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // List workouts
        workoutsViewExpandable = view.findViewById(R.id.list_workouts_expandable);
        setAdapter((FilterTime) filter.getSelectedItem());

        // Add Manual Workout
        addManualWorkout = view.findViewById(R.id.add_workout);
        addManualWorkout.setOnClickListener(v -> onManualAddClickedListener.onManualAddClickedListener());

        return view;
    }

    private Drawable getDefaultGroupIndicator(){
        //obtain expandableListViewStyle  from theme
        TypedArray expandableListViewStyle = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.expandableListViewStyle});
        //obtain attr from style
        TypedArray groupIndicator = getContext().getTheme().obtainStyledAttributes(expandableListViewStyle.getResourceId(0,0),new int[]{android.R.attr.groupIndicator});
        return groupIndicator.getDrawable(0);
    }

    private void setAdapter(FilterTime filterTime){
        workoutsFilterAdapter = new WorkoutsFilterAdapter(getContext(),filterTime, FilterUtilities.createMapWorkouts(workouts, filterTime));
        workoutsViewExpandable.setAdapter(workoutsFilterAdapter);

        switch (filterTime){
            case ALL:
                workoutsViewExpandable.setGroupIndicator(null);
                workoutsViewExpandable.setOnGroupClickListener((parent, v, groupPosition, id) -> {
                    onWorkOutSelectedListener.onWorkOutSelected(workouts.get(groupPosition));
                    return false;
                });
                break;
                default:
                    workoutsViewExpandable.setGroupIndicator(getDefaultGroupIndicator());
                    workoutsViewExpandable.setOnGroupClickListener(null);
                    workoutsViewExpandable.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                        onWorkOutSelectedListener.onWorkOutSelected(workouts.get(childPosition));
                        return false;
                    });
                    break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume");

        // TODO: 10/31/2019 RESET GUI SE SETTING SONO CAMBIATI

        FilterTime filterTime = (FilterTime) filter.getSelectedItem();
        if(onManualAddClickedListener.newWorkout()!=null){
            final Workout newW = onManualAddClickedListener.newWorkout();
            workouts.add(0, newW);
            workouts.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
            workoutsFilterAdapter.update(FilterUtilities.createMapWorkouts(workouts, filterTime));
            onManualAddClickedListener.resetNewWorkout();

            Log.d(TAG, "Gui Update : New Manual Workout = " +newW);
        }

        if(onWorkOutSelectedListener.workoutChanged()!=null){
            final Workout newW = onWorkOutSelectedListener.workoutChanged();
            workouts.stream().filter(w -> w.getIdWorkout() == newW.getIdWorkout())
                             .findFirst().ifPresent(w -> workouts.remove(w));
            workouts.add(newW);
            workouts.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

            workoutsFilterAdapter.update(FilterUtilities.createMapWorkouts(workouts, filterTime));
            onWorkOutSelectedListener.resetWorkoutChanged();

            Log.d(TAG, "Gui Update : Workout Changed = " +newW);
        }

        if(onDeleteWorkoutClickedListener.idWorkoutDeleted()>0){
            final int id_workout_deleted = onDeleteWorkoutClickedListener.idWorkoutDeleted();
            workouts.stream().filter(w -> w.getIdWorkout() == id_workout_deleted)
                    .findFirst().ifPresent(w -> workouts.remove(w));

            workoutsFilterAdapter.update(FilterUtilities.createMapWorkouts(workouts, filterTime));
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
        int idWorkoutDeleted();
        void resetWorkoutDelete();
    }
}
