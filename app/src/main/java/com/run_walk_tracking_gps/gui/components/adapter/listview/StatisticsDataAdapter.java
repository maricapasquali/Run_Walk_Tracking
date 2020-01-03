package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.builder.StatisticsBuilder;
import com.run_walk_tracking_gps.model.StatisticsData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class StatisticsDataAdapter extends BaseAdapter {

    private final static String TAG = StatisticsDataAdapter.class.getName();

    private Context context;
    private List<StatisticsData> statisticsFiltered;

    public StatisticsDataAdapter(Context context, List<StatisticsData> list){
        this.context = context;
        this.statisticsFiltered = list;
    }

    public void updateStatisticsData(List<StatisticsData> list){
        this.statisticsFiltered.clear();
        this.statisticsFiltered.addAll(list);
        this.notifyDataSetChanged();
    }

    // TODO: 12/2/2019 MIGLIORARE
    private List<StatisticsData> getStatisticsFilteredGraphic(){
        final List<StatisticsData> statisticsData = new ArrayList<>();
        final Measure.Type type = statisticsFiltered.get(0).getMeasure().getType();
        final Measure.Unit unit = statisticsFiltered.get(0).getMeasure().getUnit();
        for (StatisticsData sData : statisticsFiltered) {
            statisticsData.add(StatisticsBuilder.create(context, type)
                                                .setDate(sData.getDate())
                                                .setValue(unit.equals(type.getMeasureUnitDefault()) ? sData.getValue() :
                                                                                                      Measure.conversionTo(unit, sData.getValue()))
                                                .build());
        }

        Log.d(TAG, statisticsFiltered.toString());
        return statisticsData;
    }

    @Override
    public int getCount() {
        return statisticsFiltered.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return statisticsFiltered.get(position-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO: 11/17/2019 SE E' POSSIBILE USARE HOLDER
        if(getCount()-1>0){
            if (position == 0) {
                convertView = LayoutInflater.from(context).inflate(R.layout.custom_graph, null);
                final GraphView graphView = (GraphView) convertView.findViewById(R.id.statistics_graph);

                final LineGraphSeries<DataPoint> seriesLine = new LineGraphSeries<>();
                final PointsGraphSeries<DataPoint> seriesPoint = new PointsGraphSeries<>();

                seriesLine.setColor(R.color.colorPrimaryDark);

                seriesPoint.setSize(13);
                seriesPoint.setColor(R.color.colorPrimaryDark);
                final  List<StatisticsData> statisticsFilteredGraphic = getStatisticsFilteredGraphic();
                statisticsFilteredGraphic.stream().sorted((d1, d2) -> d1.getDate().compareTo(d2.getDate()))
                        .forEach(data -> seriesPoint.appendData(new DataPoint(data.getDate(),data.getValue()),true, getCount()-1));
                statisticsFilteredGraphic.stream().sorted((d1, d2) -> d1.getDate().compareTo(d2.getDate()))
                        .forEach(data -> seriesLine.appendData(new DataPoint(data.getDate(),data.getValue()),true, getCount()-1));

                graphView.addSeries(seriesPoint);graphView.addSeries(seriesLine);
                // set date label formatter
                graphView.getGridLabelRenderer().setPadding(100);
                graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(context));
                graphView.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

                // set manual x bounds to have nice steps
                final List<Date> x = statisticsFilteredGraphic.stream().map(StatisticsData::getDate).collect(toList());
                if(!x.isEmpty()){
                    graphView.getViewport().setMinX(x.get(statisticsFilteredGraphic.size()-1).getTime());
                    graphView.getViewport().setMaxX(x.get(0).getTime());
                    graphView.getViewport().setXAxisBoundsManual(true);

                    // as we use dates as labels, the human rounding to nice readable numbers
                    graphView.getGridLabelRenderer().setHumanRounding(false);
                }

            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.custom_item_statistics_filtered, null);

                final TextView text_statisticsFiltered = convertView.findViewById(R.id.statistics_data);
                final TextView date = convertView.findViewById(R.id.statistics_date);

                text_statisticsFiltered.setText(statisticsFiltered.get(position-1).getMeasure().toString());
                date.setText(statisticsFiltered.get(position-1).getDateStr());
            }

        }else{
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_item_no_value, null);
        }

        return convertView;
    }

}