package com.run_walk_tracking_gps.model.enumerations;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.run_walk_tracking_gps.R;

import java.util.Locale;
import java.util.stream.Stream;

public enum Language {

    ENGLISH(R.string.en_code),
    ITALIAN(R.string.it_code),
    SPANISH(R.string.es_code);

    private int code;

    Language(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Language defaultForApplication(){
        return Language.ENGLISH;
    }

    public static Locale getLocale(final Context context){
        final String codeCurrentLanguage = Locale.getDefault().getLanguage();

        final Language language = Stream.of(values())
                                        .filter(l -> context.getString(l.getCode()).equals(codeCurrentLanguage))
                                        .findFirst()
                                        .orElse(defaultForApplication());

        Locale.setDefault(new Locale(context.getString(language.getCode())));

        return Locale.getDefault();
    }

}
