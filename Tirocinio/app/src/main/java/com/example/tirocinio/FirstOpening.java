package com.example.tirocinio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FirstOpening extends AppCompatActivity {

    private TextView tv_start;
    private CountDownTimer cd_timer;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_opening);
        tv_start = findViewById(R.id.tv_startKARYA);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String tutorialKey = "SOME_KEY";
        Boolean firstTime = getPreferences(MODE_PRIVATE).getBoolean(tutorialKey, true);
        if (firstTime) {

             cd_timer = new CountDownTimer(20000, 1000) {

                public void onTick(long millisUntilFinished) {
                    tv_start.setText("(... " + millisUntilFinished / 1000 +" secondi rimanenti) ");
                }

                public void onFinish() {
                    pressHomepageNoLogin(null);
                }
            }.start();
            getPreferences(MODE_PRIVATE).edit().putBoolean(tutorialKey, false).apply();
        } else {
            pressHomepageNoLogin(null);

        }
    }


    public void pressStartTutorial (View v){
        cd_timer.cancel();
        startActivity( new Intent (this,Tutorial.class));
    }

    public void pressHomepageNoLogin(View v){
        startActivity( new Intent (this, HomepageNoLogin.class));
    }
}

