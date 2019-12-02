package com.run_walk_tracking_gps.model.enumerations;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.utilities.DateUtilities;

import org.json.JSONException;

import java.util.Locale;
import java.util.stream.Stream;

public enum Language {

    ENGLISH(R.string.en_code),
    ITALIAN(R.string.it_code),
    SPANISH(R.string.es_code);

    /*
    *  Se si aggiunge una lingua, si deve aggiungere in:
    *  -  getLocale(...)
    *  -  Resourses : array-string (di tutti i file 'string.xml' )
    */

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

    public Locale getLocale(Context context){
        switch (this) {
            case ITALIAN:
                return Locale.ITALIAN;
            case ENGLISH:
                return Locale.ENGLISH;
            case SPANISH:
                return Utilities.createLocale(context, this);
            default: // =  defaultForApplication
                return Language.defaultForApplication().getLocale(context);
        }
    }

    public static Language defaultForUser(final Context context){
        String current = null;
        try {
            current = Preferences.isJustUserLogged(context) ? Preferences.getLanguageDefault(context) : Locale.getDefault().getLanguage();
            Log.e("Language", "Language current : " + current);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  supported(context, current);
    }

    private static  Language supported(final Context context, final String code){
        return Stream.of(values()).filter(l -> context.getString(l.getCode()).equals(code)).findFirst().orElse(defaultForApplication());
    }


    public static class Utilities {

        private static Resources res;
        private static Configuration configuration;

        private static Locale createLocale(Context context, Language language){
            return new Locale(context.getString(language.getCode()));
        }

        private static void change(Context context, Language language) {
            res = context.getResources();
            configuration  = new Configuration(res.getConfiguration());
            final Locale locale = language.getLocale(context);
            configuration.setLocale(locale);
        }

        public static Configuration changeConfiguration(Context context, Language language){
            change(context, language);
            res.updateConfiguration(configuration, res.getDisplayMetrics());
            return configuration;
        }

        public static Context changeContext(Context context, Language language){
            change(context, language);
            return context.createConfigurationContext(configuration);
        }
    }
}
