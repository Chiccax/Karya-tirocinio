package com.example.tirocinio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.tirocinio.graphql.CreateFireMutation;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.OkHttpClient;

public class MoreInformation extends AppCompatActivity {
    private  String more_info;
    private CheckBox ck1;
    private CheckBox ck2;
    private CheckBox ck3;
    private CheckBox ck4;
    private CheckBox ck5;
    private CheckBox ck6;
    private EditText more_information;
    private ApolloClient apolloClient;

    boolean industrial_area = false;
    boolean waste_dump = false;
    boolean city_center = false;
    boolean trusses = false;
    boolean gas_pipeline = false;
    boolean bell_tower = false;
    String information = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        apolloClient = ApolloClient.builder()
                .serverUrl(Config.SERVER_URL)
                .okHttpClient(okHttpClient)
                .build();

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.more_information_landscape);
        } else {
            setContentView(R.layout.more_information);
        }

        ck1 = findViewById(R.id.chk1);
        ck2 = findViewById(R.id.chk2);
        ck3 = findViewById(R.id.chk3);
        ck4 = findViewById(R.id.chk4);
        ck5 = findViewById(R.id.chk5);
        ck6 = findViewById(R.id.chk6);
        more_information = findViewById(R.id.more_information_text);
    }

    public void pressContinue(View v){
        if((ck1).isChecked()) {
            industrial_area = true;
        }

        if((ck2).isChecked())
           waste_dump = true;

        if((ck3).isChecked())
           city_center = true;

        if((ck4).isChecked())
            trusses = true;

        if((ck5).isChecked())
            gas_pipeline = true;

        if((ck6).isChecked())
            bell_tower = true;

        information = more_information.getText().toString();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String dateAndTime = dateFormat.format(cal.getTime());

        SharedPreferences fire = getSharedPreferences("fire",MODE_PRIVATE);
        SharedPreferences.Editor editor = fire.edit();
        Fire.setActual_location(fire.getString("posizioneAttuale", null));
        Fire.setFire_location(fire.getString("posizioneIncendio", null));
        Fire.setPhoto(fire.getString("foto", null));
        Fire.setIndustrial_area(industrial_area);
        Fire.setWaste_dump(waste_dump);
        Fire.setCity_center(city_center);
        Fire.setTrusses(trusses);
        Fire.setGas_pipeline(gas_pipeline);
        Fire.setBell_tower(bell_tower);
        Fire.setMore_info(information);
        Fire.setDateTime(dateAndTime);

        String actual_location = fire.getString("posizioneAttuale", null);
        String fire_location = fire.getString("posizioneIncendio", null);
        String photo = fire.getString("foto", null);
        String user_email = User.getEmail_id();

        CreateFireMutation mutationCreateFire = new CreateFireMutation(actual_location, fire_location, photo, industrial_area, waste_dump, city_center, trusses, gas_pipeline, bell_tower, information, dateAndTime, user_email);
        apolloClient.mutate(mutationCreateFire).enqueue(new ApolloCall.Callback<CreateFireMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<CreateFireMutation.Data> response) {
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
            }
        });

        Intent intent = new Intent (this, DataSubmissionCompleted.class);
        startActivity(intent);
    }

    public void pressForeward(View v){
        Intent intent = new Intent (this, TakePhoto.class);
        startActivity(intent);
    }
}