package com.run_walk_tracking_gps.gui.adapter.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;

import java.util.Map;

public class MeasureAdapter extends ArrayAdapter<Measure.Type> {

    private static RadioGroup.OnCheckedChangeListener listener;

    private Map<Measure.Type, Integer> defaultMeasure;

    public MeasureAdapter(Context context, RadioGroup.OnCheckedChangeListener listener, Map<Measure.Type, Integer> defaultMeasure) {
        super(context, R.layout.custom_item_measure_unit, Measure.Type.getMeasureChangeable());
        MeasureAdapter.listener = listener;
        this.defaultMeasure = defaultMeasure;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Measure.Type measure = getItem(position);
        final ListHolder viewHolder;
        final View view;


        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.custom_item_measure_unit, null);

            final TextView textViewMeasure = view.findViewById(R.id.measure);
            final RadioButton unit_1 = view.findViewById(R.id.unit_1);
            final RadioButton unit_2 = view.findViewById(R.id.unit_2);
            final RadioGroup choose_unit = (RadioGroup) view.findViewById(R.id.choose_unit);;


            viewHolder = new ListHolder(textViewMeasure, unit_1, unit_2, choose_unit);

            view.setTag(viewHolder);
        } else {

            view = convertView;

            viewHolder = (ListHolder) convertView.getTag();
        }


        viewHolder.textViewMeasure.setText(measure.getStrId());
        viewHolder.textViewMeasure.setCompoundDrawablesWithIntrinsicBounds(
                getContext().getDrawable(measure.getIconId()), null, null, null);
        final Measure.Unit[] measureUnits = measure.getMeasureUnit();
        if(measureUnits.length==2){
            viewHolder.unit_1.setText(measureUnits[0].getStrId());
            viewHolder.unit_2.setText(measureUnits[1].getStrId());
        }
        switch (defaultMeasure.get(measure)){
            case 1:
                viewHolder.unit_1.setChecked(true);
                break;
            case 2 :
                viewHolder.unit_2.setChecked(true);
                break;
        }
        return view;
    }

    private static class ListHolder {

        private TextView textViewMeasure;
        private RadioButton unit_1;
        private RadioButton unit_2;

        private ListHolder(TextView textViewMeasure, RadioButton unit_1,RadioButton unit_2, RadioGroup choose_unit) {
            this.textViewMeasure = textViewMeasure;
            this.unit_1 = unit_1;
            this.unit_2 = unit_2;
            choose_unit.setOnCheckedChangeListener(listener);
        }
    }

}

