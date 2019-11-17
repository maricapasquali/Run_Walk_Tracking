package com.run_walk_tracking_gps.gui.components.adapter.spinner;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

import com.run_walk_tracking_gps.R;


public abstract class CustomSpinnerAdapter<T> extends BaseAdapter {

    private Context context;
    private int layout;
    private boolean fistDisabled;
    private boolean center_item;

    private T[] genericArray;

    public CustomSpinnerAdapter(Context context, T[] objects, boolean isDisabledFirst) {
        this.context = context;
        this.genericArray = objects;
        this.fistDisabled = isDisabledFirst;
        this.layout = R.layout.custom_spinner;
    }

    protected void setLayout(int layout){
        this.layout = layout;
    }

    protected boolean isTextViewCenter(){
        return center_item;
    }

    protected void setTextViewInCenter(boolean isCenter){
        this.center_item = isCenter;
    }

    @Override
    public boolean isEnabled(int position) {
        return fistDisabled ? position!=0 : super.isEnabled(position);
    }

    @Override
    public int getCount() {
        return genericArray.length;
    }

    @Override
    public Object getItem(int position) {
        return genericArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final T item = (T)getItem(position);
        final ListHolder viewHolder;
        final View view;

        /* Se la view non ancora è stata caricata dai layout, uso l'inflater per caricarla,
         * in questo caso quella relativa al contatto */
        if(convertView==null){

            view = LayoutInflater.from(context).inflate(layout, null);

            final TextView text = view.findViewById(R.id.item_spinner);

            /* Creo un view holder per evitare ogni volta di cercare le view by id (operazione onerosa)*/
            viewHolder = new ListHolder(text);
            /* Imposto come tag della view il view holder creato, in questo modo quando viene "riciclata"
             * la view posso anche riusare il view holder.
             */
            view.setTag(viewHolder);
        } else {
            /* Se invece la convertView è disponibile, significa che abbiamo già caricato la view dai
             * layout quindi si può riutilizzare la stessa. Meccanismo di Recycler View.
             */
            view = convertView;
            /* Recupero il view holder dalla view */
            viewHolder = (ListHolder) convertView.getTag();
        }

        if(center_item) viewHolder.textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        setItemSpinner(viewHolder.textView, item);


        return view;
    }

    private static class ListHolder {
        private final TextView textView;

        private ListHolder(TextView textView) {
            this.textView = textView;
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    protected abstract void setItemSpinner(TextView textView, T item);

    protected Context getContext() {
        return context;
    }
}