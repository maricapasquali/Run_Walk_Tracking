package com.run_walk_tracking_gps.gui.components.dialog;

import android.content.Context;
import android.content.DialogInterface;

import java.util.Arrays;

import androidx.appcompat.app.AlertDialog;

public class ChooseDialog<T> extends AlertDialog.Builder implements DialogInterface.OnClickListener{

    private static String ERROR;

    private T[] type;
    private String[] strings;
    private int indexCheckedItem;
    private OnSelectedItemListener<T> onSelectedItemListener;

    private ChooseDialog(Context context, T[] type, OnSelectedItemListener<T> onSelectedItemListener){
        super(context);
        this.onSelectedItemListener = onSelectedItemListener;
        this.type = type;

        /*final String tType = ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0].toString();*/
        ERROR = "Enum e Array-String (Resources) non hanno la stessa lunghezza";
    }

    public ChooseDialog(Context context, T[] type, T checkedItem, int arrayStrings, OnSelectedItemListener<T> onSelectedItemListener) {
        this(context, type, onSelectedItemListener);

        this.strings = getContext().getResources().getStringArray(arrayStrings);

        try {
            if(type.length!=strings.length) throw new ArrayIndexOutOfBoundsException(ERROR);

            indexCheckedItem = Arrays.asList(type).indexOf(checkedItem);
            setSingleChoiceItems(strings, indexCheckedItem, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChooseDialog(Context context, T[] type, CharSequence checkedItem, OnSelectedItemListener<T> onSelectedItemListener, OnSetStringArrayListener
            onSetStringArrayListener) {
        this(context, type, onSelectedItemListener);

        this.strings = onSetStringArrayListener.getStringArray();

        try {
            if(type.length!=strings.length) throw new ArrayIndexOutOfBoundsException(ERROR);

            indexCheckedItem = Arrays.asList(strings).indexOf(checkedItem.toString());
            setSingleChoiceItems(strings, indexCheckedItem, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
