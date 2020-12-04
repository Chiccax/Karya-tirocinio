package com.example.tirocinio;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.tirocinio.graphql.GetFireByTimeDateAndLocationQuery;

import org.jetbrains.annotations.NotNull;
import java.util.List;

import okhttp3.OkHttpClient;

public class DataSubmissionCompleted extends AppCompatActivity {
    private ApolloClient apolloClient;
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
            setContentView(R.layout.data_submission_completed_landscape);
        } else {
            setContentView(R.layout.data_submission_completed);
        }

        String dateAndTime= Fire.getDateTime();
        String fire_location = Fire.getFire_location();

        GetFireByTimeDateAndLocationQuery queryGetFireByTimeDateAndLocation = new GetFireByTimeDateAndLocationQuery(dateAndTime, fire_location);
        apolloClient.query(queryGetFireByTimeDateAndLocation).enqueue(new ApolloCall.Callback<GetFireByTimeDateAndLocationQuery.Data>(){
            @Override
            public void onResponse(@NotNull Response<GetFireByTimeDateAndLocationQuery.Data> response) {
                List<GetFireByTimeDateAndLocationQuery.GetFireByTimeDateAndLocation> fire = response.getData().getFireByTimeDateAndLocation();
                if(fire.size() <= 1){
                    DataSubmissionCompleted.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textForThanks = findViewById(R.id.textForThanks);
                            textForThanks.setText("Grazie per la tua collaborazione, sei il primo ad aver segnalato questo incendio. Dei nostri operatori provvederanno al più presto!");
                        }
                    });
                } else if (fire.size() == 2) {
                    DataSubmissionCompleted.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textForThanks = findViewById(R.id.textForThanks);
                            textForThanks.setText("Grazie per la tua collaborazione, un'altra persona ha segnalato questo incendio. Dei nostri operatori provvederanno al più presto!");
                        }
                    });
                } else {
                    DataSubmissionCompleted.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textForThanks = findViewById(R.id.textForThanks);
                            textForThanks.setText("Grazie per la tua collaborazione, altre " + fire.size() + " persone hanno segnalato questo incendio. Dei nostri operatori provvederanno al più presto!");
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
            }
        });
    }

    public void pressContinue(View v){
        Intent intent = new Intent (this, HomepageWithLogin.class);
        startActivity(intent);
    }
}