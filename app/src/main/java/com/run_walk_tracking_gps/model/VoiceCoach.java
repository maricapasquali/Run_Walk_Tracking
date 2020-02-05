package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.model.enumerations.Language;

import java.util.Map;
import java.util.stream.Collectors;

import java.util.stream.Stream;

public class VoiceCoach {

    private static final String TAG = VoiceCoach.class.getName();

    private static VoiceCoach handler;

    private Context context;
    private TextToSpeech tts;

    private boolean isActive;
    private int interval;
    private Map<Measure.Type, Boolean> activeFunctions;

    private VoiceCoach(Context context){
        this.context = context;
        this.isActive = Preferences.VoiceCoach.isActive(context);
        this.interval = Preferences.VoiceCoach.getInterval(context);
        this.activeFunctions = Stream.of(Measure.Type.DURATION, Measure.Type.DISTANCE,Measure.Type.ENERGY)
                                     .collect(Collectors.toMap(type -> type,
                                                               type -> Preferences.VoiceCoach.isParameterActive(context, type)));
    }

    public static synchronized VoiceCoach create(Context context){
        if(handler == null)
            handler = new VoiceCoach(context.getApplicationContext());
        return handler;
    }


    public void start(){
        if(this.isActive && tts==null){
            this.tts = new TextToSpeech(context, status -> {
                switch (status){
                    case TextToSpeech.SUCCESS:
                        tts.setLanguage(Language.getLocale(context));
                        break;
                }
            });
        }
    }

    public void stop(){
        if(this.isActive && tts!=null){
            tts.stop();
            tts.shutdown();
            tts=null;
        }
    }

    /**
     *
     * @param time string of time format ( 00:00:00 )
     * @return
     */
    private boolean isNowToSpeak(String time){
        int sec = Measure.Utilities.toSeconds(time);
        return sec> 0 && sec%(interval*60)== 0;
    }

    private void speak(CharSequence speak){
        if(this.isActive)
            tts.speak(speak, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    /**
     *
     * @param time
     * @param distance
     * @param calories
     */
    public void speakIfIsActive(String time, CharSequence distance, CharSequence calories){
        StringBuilder speak = new StringBuilder().append(speakString(Measure.Type.DURATION, time))
                .append(speakString(Measure.Type.DISTANCE, distance))
                .append(speakString(Measure.Type.ENERGY, calories));

        if (speak.length() > 0) speak(speak.toString());
    }

    private String speakString(Measure.Type type, CharSequence val){
        return activeFunctions.get(type) ? this.context.getString(type.getStrId()) + " " + val + ", " :"";
    }

    public int getInterval(){
        return interval;
    }

    public boolean getOnOff(Measure.Type type){
        return activeFunctions.get(type);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setInterval(int min){
        Preferences.VoiceCoach.setInterval(context, min);
        interval = min;
    }

    public void setParameterEnable(Measure.Type type, boolean onOff){
        Preferences.VoiceCoach.setParameter(context, type, onOff);
        activeFunctions.replace(type, onOff);
    }

    public void setAllParameterEnable(boolean onOff) {
        activeFunctions.entrySet().forEach(e -> setParameterEnable(e.getKey(),onOff));
    }

    public void setActive(boolean isChecked) {
        Preferences.VoiceCoach.setVoiceCoach(context, isChecked);
        isActive = isChecked;
    }

    public boolean isAllParameterUnable() {
        return activeFunctions.values().stream().allMatch(b -> b.equals(false));
    }
}
