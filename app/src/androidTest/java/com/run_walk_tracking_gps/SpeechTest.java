package com.run_walk_tracking_gps;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.test.InstrumentationRegistry;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.run_walk_tracking_gps.model.enumerations.Language;
import com.run_walk_tracking_gps.utilities.DateHelper;

import org.junit.Test;

public class SpeechTest {

    private Context context = InstrumentationRegistry.getTargetContext();
    private TextToSpeech tts;
    private Button b1;
    @Test
    public void test(){

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Language.getLocale(context));
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = "Hello";
                Toast.makeText(context, toSpeak,Toast.LENGTH_SHORT).show();
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

    }
}
