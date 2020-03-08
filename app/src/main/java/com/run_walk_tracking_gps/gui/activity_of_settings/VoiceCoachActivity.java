package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.model.Measure;

import com.run_walk_tracking_gps.model.VoiceCoach;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class VoiceCoachActivity extends CommonActivity {

    private VoiceCoach voiceCoach;

    // GUI
    private Switch useVoiceCoach;
    private Switch duration;
    private Switch distance;
    private Switch calories;
    private IndicatorSeekBar intervalSet;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_vocal_coach);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.vocal_coach);

        voiceCoach = VoiceCoach.getInstance(this);

        useVoiceCoach = findViewById(R.id.vocal_coach_on_off);
        duration = findViewById(R.id.duration_on_off);
        distance = findViewById(R.id.distance_on_off);
        calories = findViewById(R.id.calories_on_off);
        intervalSet = findViewById(R.id.interval_set);

        useVoiceCoach.setChecked(voiceCoach.isActive());
        duration.setChecked(voiceCoach.getOnOff(Measure.Type.DURATION ));
        distance.setChecked(voiceCoach.getOnOff(Measure.Type.DISTANCE ));
        calories.setChecked(voiceCoach.getOnOff(Measure.Type.ENERGY   ));
        intervalSet.setProgress(voiceCoach.getInterval());

        onActiveCoach(voiceCoach.isActive());
    }


    @Override
    protected void listenerAction() {

        duration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            voiceCoach.setParameterEnable(Measure.Type.DURATION, isChecked);
            checkParameter();
        });

        distance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            voiceCoach.setParameterEnable(Measure.Type.DISTANCE, isChecked);
            checkParameter();
        });

        calories.setOnCheckedChangeListener((buttonView, isChecked) -> {
            voiceCoach.setParameterEnable(Measure.Type.ENERGY, isChecked);
            checkParameter();
        });

        useVoiceCoach.setOnCheckedChangeListener((buttonView, isChecked) -> {
            VoiceCoachActivity.this.onActiveCoach(isChecked);
            voiceCoach.setActive(isChecked);
            checkVoiceCoach();
        });

        intervalSet.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                voiceCoach.setInterval(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            }
        });
    }

    private void checkParameter(){
        if(voiceCoach.isAllParameterUnable()) {
            voiceCoach.setActive(false);
            useVoiceCoach.setChecked(false);
            onActiveCoach(false);
        }
    }

    private void checkVoiceCoach(){
        if(voiceCoach.isActive()) {
            onActiveCoach(true);
            if(voiceCoach.isAllParameterUnable()){
                voiceCoach.setAllParameterEnable(true);
                onSetOnOffAll(true);
            }
        }
    }

    private void onActiveCoach(boolean isActive){
        ((LinearLayout)duration.getParent()).setVisibility(isActive? View.VISIBLE : View.GONE);
        ((LinearLayout)distance.getParent()).setVisibility(isActive? View.VISIBLE : View.GONE);
        ((LinearLayout)calories.getParent()).setVisibility(isActive? View.VISIBLE : View.GONE);
        ((RelativeLayout)intervalSet.getParent()).setVisibility(isActive? View.VISIBLE : View.GONE);
    }

    private void onSetOnOffAll(boolean isActive){
        duration.setChecked(isActive);
        distance.setChecked(isActive);
        calories.setChecked(isActive);
    }

}
