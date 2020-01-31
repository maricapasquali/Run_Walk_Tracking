package com.run_walk_tracking_gps.gui.components.dialog;

import android.content.Context;
import android.os.Build;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.enumerations.Sport;

import java.util.Arrays;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SportDialog extends ChooseDialog<Sport> { // FIXME : ME SE NE APRONO 2

    private static Sport[] sports = Sport.values();

    private SportDialog(Context context, CharSequence checkedItem, OnSelectedItemListener<Sport> onSelectedItemListener) {
        super(context, sports, checkedItem, onSelectedItemListener, () -> Arrays.stream(sports)
                                                                                .map(s -> context.getString(s.getStrId()))
                                                                                .toArray(String[]::new));
        setTitle(context.getResources().getString(R.string.sport));
    }


    public static SportDialog create(Context context, CharSequence checkedItem, OnSelectedItemListener<Sport> onSelectedItemListener){
        return new SportDialog(context, checkedItem, onSelectedItemListener);
    }

}