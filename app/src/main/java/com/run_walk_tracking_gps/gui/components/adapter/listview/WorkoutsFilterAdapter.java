package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.enumerations.FilterTime;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.utilities.DateUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkoutsFilterAdapter extends BaseExpandableListAdapter  {

    private static final String TAG = WorkoutsFilterAdapter.class.getName();

    private Context context;
    private FilterTime filterTime;

    /* Tutte le etichette dei gruppi */
    private List<Integer> labels;

    /* Una mappa dove ad ogni etichetta associo una lista di film. */
    private Map<Integer, List<Workout>> workouts;

    public WorkoutsFilterAdapter(Context context, FilterTime filterTime, Map<Integer, List<Workout>> objects){
        this.workouts = objects;
        this.labels = new ArrayList<>(workouts.keySet());
        this.context = context;
        this.filterTime = filterTime;
    }

    public void update(Map<Integer, List<Workout>>  map){
        this.workouts.clear();
        this.workouts.putAll(map);
        this.labels = new ArrayList<>(workouts.keySet());
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return labels.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(filterTime.equals(FilterTime.ALL))
            return 0;
        // recupero la l'etichetta del gruppo, in base all'indice
        final Integer groupLabel = labels.get(groupPosition);
        // restituisco la dimensione dei figli del gruppo, ovvero il numero di film sotto quella etichetta.
        return workouts.get(groupLabel).size();
    }

    /* Questo metodo deve restituire l'oggetto che rappresenta il gruppo in una certa
     * posizione "groupPosition", in questo caso i gruppi sono delle etichette (nome del gruppo) basta quindi
     * recuperare l'etichetta all'indice corretto della lista. */
    @Override
    public Object getGroup(int groupPosition) {
        switch (filterTime){
            case ALL: return workouts.get(groupPosition).get(0);
            default:return labels.get(groupPosition);
        }
    }

    /* Questo metodo deve restituire l'oggetto che rappresenta un figlio di un gruppo, infatti
     * vengono fortini l'indice del gruppo e l'indice del figlio (il film). In questo
     * caso è sufficente recuperare l'etichetta corrispondete all'indice del gruppo, e utilizzare
     * la mappa per accedere al film con indice "childPosition" */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        final Integer groupLabel = labels.get(groupPosition);
        return workouts.get(groupLabel).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /* Metodo utilizzato per la costruzione della view del gruppo (dell'etichetta) */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final View view;

                switch (filterTime){
                    case ALL:
                        view = getAll(convertView, groupPosition, 0);
                        break;
                    default:
                        final Integer groupLabel = labels.get(groupPosition);
                        final ListHolderGroup viewHolder;

                        /* Se la view non è mai stata creata, uso layoutInflater per "instanziare" la view con il layout del gruppo */
                        if(convertView==null) {
                            view = LayoutInflater.from(context).inflate(R.layout.custom_item_workout_filtered, null);
                            final TextView txtGroupLabel = view.findViewById(R.id.data_filtered);
                            viewHolder = new ListHolderGroup(txtGroupLabel);
                            view.setTag(viewHolder);
                        }else {
                            /* Ramo else, view già creata, Android permette di riutilizzarla senza crearne una nuova */
                            view = convertView;
                            viewHolder = (ListHolderGroup) convertView.getTag();
                        }
                        /* La view è stata creata in precedenza, quindi posso utilizzarla. */
                        viewHolder.textView.setText(filterTime.equals(FilterTime.MONTH) ?
                                DateUtilities.getMonth(groupLabel):String.valueOf(groupLabel));

                        break;
                }

            return view;
    }

    /* Metodo utilizzato per la costruzione della view di un figlio (del workout) */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return getAll(convertView, groupPosition, childPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private View getAll(View convertView, int groupPosition, int childPosition){
        final View view;
        final ListHolderChild vH;
        final Workout workout = (Workout)getChild(groupPosition, childPosition);

        Log.e(TAG, workout.toString());

        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.custom_item_workouts, null);
            final TextView date = view.findViewById(R.id.workout_date);
            final TextView time = view.findViewById(R.id.workout_time);
            final TextView distance = view.findViewById(R.id.workout_distance);
            final TextView calories = view.findViewById(R.id.workout_calories);
            vH = new ListHolderChild(date, time, distance, calories);
            view.setTag(vH);
        } else {
            view = convertView;
            vH = (ListHolderChild) convertView.getTag();
        }

        vH.date.setText(DateUtilities.parseFullToString(workout.getDate()));
        vH.time.setText(workout.getDuration().toString());

        vH.distance.setText(workout.getDistance().toString());
        vH.calories.setText(workout.getCalories().toString());
        return view;
    }

    private static class ListHolderGroup {

        private TextView textView;

        private ListHolderGroup(TextView textView){
            this.textView = textView;
        }
    }

    private static class ListHolderChild {

        private TextView date;
        private TextView time;
        private TextView distance;
        private TextView calories;

        private ListHolderChild(TextView date, TextView time, TextView distance, TextView calories) {
            this.date = date;
            this.time = time;
            this.distance = distance;
            this.calories = calories;
        }
    }
}
