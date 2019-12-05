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

    public static Language defaultForUser(final Context context){
        final String current = Locale.getDefault().getLanguage();
        return  supported(context, current);
    }

    private static  Language supported(final Context context, final String code){
        return Stream.of(values()).filter(l -> context.getString(l.getCode()).equals(code)).findFirst().orElse(defaultForApplication());
    }


    public static class Utilities {

        private static Resources res;
        private static Configuration configuration;

        /**
         *
         * @param context
         * @return Locale for Language Default
         */
        public static Locale createLocale(Context context){
            return createLocale(context, Language.defaultForUser(context));
        }

        private static Locale createLocale(Context context, Language language){
            return new Locale(context.getString(language.getCode()));
        }

        private static void change(Context context, Language language) {
            res = context.getResources();
            configuration  = new Configuration(res.getConfiguration());
            final Locale locale = createLocale(context, language);
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
