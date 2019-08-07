package com.moonlightflower.superioribus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.api.GoogleAPI;
import com.google.api.GoogleAPIException;
import com.google.api.translate.*;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void button_exercises_onClick(View sender) {
        Intent intent = new Intent(this, ExercisesActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    public void button_vocabulary_onClick(View sender) {
        Intent intent = new Intent(this, VocabularyActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    public void button_swap_onClick(View sender) {

    }

    public void button_translate_onClick(View sender) {
        Translate t = new TranslateV2();

        GoogleAPI.setHttpReferrer("www.google.de");
        GoogleAPI.setKey("AIzaSyDWeAkZuOQYWFB1sJRxE6JTcqh1qNRBJsM");

        try {
            t.execute("hello world", Language.ENGLISH, Language.GERMAN);
        } catch (GoogleAPIException e) {
            Log.e(getClass().getSimpleName(), e.toString(), e);
        }
    }
}
