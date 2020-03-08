package com.run_walk_tracking_gps.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.model.enumerations.Language;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.service.WorkoutService;

import java.util.Map;
import java.util.stream.Collectors;

import java.util.stream.Stream;

public class VoiceCoach {

    private static final String TAG = VoiceCoach.class.getName();

    private static final int REQUEST_CODE_VOICE = 6;

    private static VoiceCoach handler;

    private AlarmManager alarmManager;
    private PendingIntent voicePendingIntent;

    private Context context;
    private TextToSpeech tts;

    private int lastSpeak = 0;

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

        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent voiceIntent = new Intent(context, WorkoutService.class).setAction(ActionReceiver.VOICE);
        this.voicePendingIntent = PendingIntent.getService(context, REQUEST_CODE_VOICE, voiceIntent,0);
    }

    public static synchronized VoiceCoach getInstance(Context context){
        if(handler == null)
            handler = new VoiceCoach(context.getApplicationContext());
        return handler;
    }

    public static void release(){
        if(handler!=null) handler = null;
    }

    public void start(){
        if(this.isActive){
            Log.d(WorkoutService.class.getName(), "START VOICE COACH");
            init();
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            this.interval*60000, voicePendingIntent);
        }
    }

    public void stop(){
        if(this.isActive){
            Log.d(WorkoutService.class.getName(), "STOP VOICE COACH");
            alarmManager.cancel(voicePendingIntent);
            releaseTTS();
            release();
        }
    }

    private void init(){
        if(this.isActive && this.tts==null){
            this.tts = new TextToSpeech(context, status -> {
                switch (status){
                    case TextToSpeech.SUCCESS:
                        tts.setLanguage(Language.getLocale(context));
                        tts.setOnUtteranceProgressListener(new UtteranceProgressListener(){
                            @Override
                            public void onStart(String utteranceId) {
                                Log.d(TAG, "onStart");
                                MusicCoach.getInstance(context).downVolume();
                            }

                            @Override
                            public void onDone(String utteranceId) {
                                Log.d(TAG, "onDone");
                                MusicCoach.getInstance(context).restoreVolume();
                            }
                            @Override
                            public void onError(String utteranceId) {

                            }
                        });
                        break;
                }
            });
        }
    }

    private void releaseTTS(){
        if(this.isActive && this.tts!=null){
            tts.stop();
            tts.shutdown();
            tts=null;
        }
    }

    private boolean isNowToSpeak(String time){
        int timeSec = Measure.Utilities.toSeconds(time);
        return  lastSpeak + interval*60 <= timeSec ;
    }

    public void speakIfIsActive(String time, String distance, String calories){
        if(isNowToSpeak(time)){

            StringBuilder speak = new StringBuilder()
                                      .append(speakString(Measure.Type.DURATION, time))
                                      .append(speakString(Measure.Type.DISTANCE, distance))
                                      .append(speakString(Measure.Type.ENERGY, calories));

            if (speak.length() > 0)
            {
                lastSpeak = Measure.Utilities.toSeconds(time);
                tts.speak(speak.toString(), TextToSpeech.QUEUE_FLUSH,
                          null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
            }
        }
    }

    private String speakString(Measure.Type type, String val){
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

    public void toggleActiveAndInActive(OnActiveOrInActiveListener onClickActiveListener){
        if(isActive){
            stop();
            setActive(false);
            if(onClickActiveListener!=null) onClickActiveListener.onInActive();
        }else{
            setActive(true);
            start();
            if(onClickActiveListener!=null) onClickActiveListener.onActive();
        }
    }

    public boolean isAllParameterUnable() {
        return activeFunctions.values().stream().allMatch(b -> b.equals(false));
    }

    public interface OnActiveOrInActiveListener{
        void onActive();
        void onInActive();
    }
}
