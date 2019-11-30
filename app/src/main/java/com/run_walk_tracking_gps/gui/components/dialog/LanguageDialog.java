package com.run_walk_tracking_gps.gui.components.dialog;

import android.content.Context;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.enumerations.Language;

public class LanguageDialog extends ChooseDialog<Language> {

    private static Language[] languages = Language.values();

    public LanguageDialog(Context context, Language checkedItem, OnSelectedItemListener<Language> onSelectedItemListener) {
        super(context, languages, checkedItem, R.array.language, onSelectedItemListener);
        setTitle(context.getString(R.string.language));
    }

    public static LanguageDialog create(Context context, Language checkedItem,
                                        OnSelectedItemListener<Language> onSelectedItemListener){
        return new LanguageDialog(context, checkedItem, onSelectedItemListener);
    }
}
