package com.run_walk_tracking_gps.gui.adapter.listview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.utilities.DateUtilities;
import com.run_walk_tracking_gps.gui.enumeration.Measure;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsDataAdapter extends BaseAdapter {

    private final static String TAG = StatisticsDataAdapter.class.getName();

    private Context context;
    private List<StatisticsData> data_filtered;

    private String unitMeasure;


    public StatisticsDataAdapter(Context context, List<StatisticsData> map, Measure measure){
        this.context = context;
        this.data_filtered = map;
        this.setUnitMeasure(measure);
    }

    public void updateStatisticsData(List<StatisticsData> list, Measure measure){
        this.data_filtered.clear();
        this.data_filtered.addAll(list);
        this.setUnitMeasure(measure);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data_filtered.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position == 0) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_graph, null);

            final GraphView graphView = (GraphView) convertView.findViewById(R.id.statistics_graph);

            final LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

            data_filtered.stream().sorted((d1, d2) -> d1.getDate().compareTo(d2.getDate())).forEach(data -> series.appendData(new DataPoint(data.getDate(),data.getStatisticData()),true, getCount()-1));
            graphView.addSeries(series);


            // set date label formatter
            graphView.getGridLabelRenderer().setPadding(100);
            graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(context));
            graphView.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

            // set manual x bounds to have nice steps
            final List<Date> x = data_filtered.stream().map(StatisticsData::getDate).collect(Collectors.toList());
            if(!x.isEmpty()){
                graphView.getViewport().setMinX(x.get(data_filtered.size()-1).getTime());
                graphView.getViewport().setMaxX(x.get(0).getTime());
                graphView.getViewport().setXAxisBoundsManual(true);

                // as we use dates as labels, the human rounding to nice readable numbers
                graphView.getGridLabelRenderer().setHumanRounding(false);
            }

        } else {

            convertView = LayoutInflater.from(context).inflate(R.layout.custom_item_statistics_filtered, null);

            final TextView text_data_filtered = convertView.findViewById(R.id.statistics_data);
            final TextView date = convertView.findViewById(R.id.statistics_date);

            String val_y = data_filtered.get(position-1).getStatisticData()+
                    context.getString(R.string.space)+
                    unitMeasure;
            text_data_filtered.setText(val_y);
            date.setText(DateUtilities.parseFullToString(data_filtered.get(position-1).getDate()));
        }

        return convertView;
    }


    private int unitMeasure(Measure measure){
        return measure.getMeasureUnit()[0].getStrId();
    }

    private void setUnitMeasure(Measure measure) {
        this.unitMeasure = context.getString(unitMeasure(measure));
    }

}
