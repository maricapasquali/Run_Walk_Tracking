package com.run_walk_tracking_gps.gui.components.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;

import java.util.Arrays;


public class ChooseDialog<T> extends AlertDialog.Builder implements DialogInterface.OnClickListener{

    private T[] type;
    private String[] strings;
    private int indexCheckedItem;
    private OnSelectedItemListener<T> onSelectedItemListener;

    public ChooseDialog(Context context, T[] type, T checkedItem, int arrayStrings, OnSelectedItemListener<T> onSelectedItemListener) {
        super(context);
        this.onSelectedItemListener = onSelectedItemListener;
        this.type = type;
        this.strings = getContext().getResources().getStringArray(arrayStrings);

        indexCheckedItem = Arrays.asList(type).indexOf(checkedItem);
        setSingleChoiceItems(strings, indexCheckedItem, this);
    }


    public ChooseDialog(Context context, T[] type, CharSequence checkedItem, OnSelectedItemListener<T> onSelectedItemListener, OnSetStringArrayListener
            onSetStringArrayListener) {
        super(context);
        this.onSelectedItemListener = onSelectedItemListener;
        this.type = type;

        strings = onSetStringArrayListener.getStringArray();

        indexCheckedItem = Arrays.asList(strings).indexOf(checkedItem);
        setSingleChoiceItems(strings, indexCheckedItem, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        if(indexCheckedItem!=which) onSelectedItemListener.onSelectedItem(type[which], strings[which]);
    }

    public interface OnSelectedItemListener<T> {
        void onSelectedItem(T val, String description);
    }

    public interface OnSetStringArrayListener {
        String[] getStringArray();
    }
}
