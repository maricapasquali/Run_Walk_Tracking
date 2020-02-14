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

    public static synchronized VoiceCoach create(Context context){
        if(handler == null)
            handler = new VoiceCoach(context.getApplicationContext());
        return handler;
    }


    public void setAlarmVoice(){
        if(isActive()){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    Preferences.VoiceCoach.getInterval(context)*60000, voicePendingIntent);
        }
    }

    public void cancelAlarmVoice(){
        if(isActive()){
            alarmManager.cancel(voicePendingIntent);
        }
    }


    public void start(){
        if(this.isActive && tts==null){
            this.tts = new TextToSpeech(context, status -> {
                switch (status){
                    case TextToSpeech.SUCCESS:
                        tts.setLanguage(Language.getLocale(context));
                        tts.setOnUtteranceProgressListener(new UtteranceProgressListener(){
                            @Override
                            public void onStart(String utteranceId) {
                                Log.d(TAG, "onStart");
                                MusicCoach.create(context).downVolume();
                            }

                            @Override
                            public void onDone(String utteranceId) {
                                Log.d(TAG, "onDone");
                                MusicCoach.create(context).restoreVolume();
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
        return  Measure.Utilities.toSeconds(time) >= 60;
    }

    private void speak(CharSequence speak){
        if(this.isActive)
            tts.speak(speak, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
    }

    /**
     *
     * @param time
     * @param distance
     * @param calories
     */
    public void speakIfIsActive(String time, CharSequence distance, CharSequence calories){
        if(isNowToSpeak(time)){
            StringBuilder speak = new StringBuilder().append(speakString(Measure.Type.DURATION, time))
                    .append(speakString(Measure.Type.DISTANCE, distance))
                    .append(speakString(Measure.Type.ENERGY, calories));

            if (speak.length() > 0)
                speak(speak.toString());
        }
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

    public void toggleActiveAndInActive(OnActiveOrInActiveListener onClickActiveListener){
        if(isActive){
            cancelAlarmVoice();
            stop();
            setActive(false);
            onClickActiveListener.onInActive();
        }else{
            setActive(true);
            setAlarmVoice();
            start();
            onClickActiveListener.onActive();
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
