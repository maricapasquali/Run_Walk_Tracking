package com.run_walk_tracking_gps.utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.model.enumerations.Language;

import org.json.JSONException;

import java.util.Locale;
import java.util.stream.Stream;

public class LanguageUtilities {

    public static Language of(final Context context){
        String current = null;//Language.current(context.getResources());
        try {
            current = Preferences.getLanguageDefault(context);
            Log.e("Language", "Language current : " + current);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalCurrent = current;
        return Stream.of(Language.values()).filter(l -> context.getString(l.getCode()).equals(finalCurrent)).findFirst().orElse(Language.ITALIAN);
    }

    public static Configuration change(Context context, Language language){
        final Resources res = context.getResources();
        final Configuration configuration = new Configuration(res.getConfiguration());

        switch (language){
            case ENGLISH:
                configuration.setLocale(Locale.ENGLISH);
                DateUtilities.setLocale(Locale.ENGLISH);
                break;
            default:
                configuration.setLocale(Locale.ITALIAN);
                DateUtilities.setLocale(Locale.ITALIAN);
                break;
        }
        res.updateConfiguration(configuration, res.getDisplayMetrics());
        return configuration;
    }


}
