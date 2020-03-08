package com.run_walk_tracking_gps.gui.fragments;

import android.content.Context;
import android.content.res.TypedArray;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.ErrorQueue;
import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.gui.components.adapter.listview.WorkoutsFilterAdapter;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.FilterAdapterSpinner;
import com.run_walk_tracking_gps.model.enumerations.FilterTime;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.utilities.FilterUtilities;
import com.run_walk_tracking_gps.utilities.ColorUtilities;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class WorkoutsFragment extends Fragment {

    private static final String TAG = WorkoutsFragment.class.getName();
    private Spinner filter;
    private ExpandableListView workoutsViewExpandable;
    private ImageButton addManualWorkout;
    private NoValueFragment no_value_fragment;
    private WorkoutsFilterAdapter workoutsFilterAdapter;
    private OnWorkOutSelectedListener onWorkOutSelectedListener;
    private OnManualAddClickedListener onManualAddClickedListener;
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        workouts = Workout.createList(getContext(), DaoFactory.getInstance(getContext()).getWorkoutDao().getAll());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        final View view;
        view = inflater.inflate(R.layout.fragment_workouts, container, false);

        no_value_fragment = (NoValueFragment) getChildFragmentManager().findFragmentById(R.id.no_value_fragment);

        // Filter
        filter = view.findViewById(R.id.filter_workouts);
        filter.setAdapter(new FilterAdapterSpinner(getContext(),  true));
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
        final TypedArray expandableListViewStyle = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.expandableListViewStyle});
        //obtain attr from style
        final TypedArray groupIndicator = getContext().getTheme().obtainStyledAttributes(expandableListViewStyle.getResourceId(0,0),new int[]{android.R.attr.groupIndicator});
        return ColorUtilities.lightIcon(getContext(), groupIndicator.getDrawable(0));
    }

    private void setAdapter(FilterTime filterTime){
        workoutsFilterAdapter = new WorkoutsFilterAdapter(getContext(),filterTime, FilterUtilities.createMapWorkouts(workouts, filterTime));
        workoutsViewExpandable.setAdapter(workoutsFilterAdapter);

        switch (filterTime){
            case ALL:
                workoutsViewExpandable.setGroupIndicator(null);
                workoutsViewExpandable.setOnGroupClickListener((parent, v, groupPosition, id) -> {
                    Workout workout = (Workout) parent.getExpandableListAdapter().getGroup(groupPosition);
                    if(workout!=null) onWorkOutSelectedListener.onWorkOutSelected(workout);
                    return false;
                });
                break;
            default:
                workoutsViewExpandable.setGroupIndicator(getDefaultGroupIndicator());
                workoutsViewExpandable.setOnGroupClickListener(null);
                workoutsViewExpandable.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                    Workout workout = (Workout) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                    if(workout!=null) onWorkOutSelectedListener.onWorkOutSelected(workout);
                    return false;
                });
                break;
        }
    }

    private void checkSizeWorkouts(){
        final boolean isVisible = workouts.size()<=0;
        no_value_fragment.getView().setVisibility(isVisible? View.VISIBLE : View.GONE);
        filter.setVisibility(isVisible? View.GONE: View.VISIBLE );
        if(isVisible) Log.d(TAG, "No Workouts");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume");
        ErrorQueue.getErrors(getContext());
        // TODO: 1/3/2020 ADD SYNC SERVICE ONRESUME
        //SyncServiceHandler.create(getContext()).start();

        FilterTime filterTime = (FilterTime) filter.getSelectedItem();
        workouts = Workout.createList(getContext(), DaoFactory.getInstance(getContext()).getWorkoutDao().getAll());
        workoutsFilterAdapter.update(FilterUtilities.createMapWorkouts(workouts, filterTime));
        checkSizeWorkouts();
    }

    public interface OnWorkOutSelectedListener{
        void onWorkOutSelected(Workout workout);
    }

    public interface OnManualAddClickedListener{
        void onManualAddClickedListener();
    }

}
