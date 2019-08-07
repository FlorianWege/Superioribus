package com.moonlightflower.superioribus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ExercisesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercises);
    }

    public void button_main_onClick(View sender) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    public void button_options_onClick(View sender) {
        Intent intent = new Intent(this, OptionsActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }
}