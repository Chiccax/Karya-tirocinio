package com.example.tirocinio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.tirocinio.graphql.CreateUserMutation;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class ThirdRegistrationPage extends AppCompatActivity {
    private ApolloClient apolloClient;
    //UI
    private EditText et_code;
    private int user_code;

    //to resend code - security
    private Timer resendCode;
    private static final int PERIOD =3000000; //1000 = 1 secondo -> 3000000 = 5 minuti
    public static boolean isIdentificate=false; //flag utente identificato

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        apolloClient = ApolloClient.builder()
                .serverUrl(Config.SERVER_URL)
                .okHttpClient(okHttpClient)
                .build();

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout._3_registration_page_landscape);
        } else {
            setContentView(R.layout._3_registration_page);
        }

        et_code = findViewById(R.id.code);

        resendCode(); //invio nuovo codice allo scadere del timeout

    }

    public void resendCode(){
        resendCode = new Timer();
        resendCode.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("EMAIL","codice cambiato");
                Config.generateIdentificationCode(10000,100000, Config.RESEND_CODE);
                if(isIdentificate)
                    resendCode.cancel();
            }
        },PERIOD,PERIOD);
    }

    private boolean isEqualCodes(){
        if (user_code == Config.INDENTICATION_CODE_REG)
            return true;
        return false;
    }


    public void pressContinue(View v){
        user_code = Integer.parseInt(et_code.getText().toString());

        if(isEqualCodes()){
            isIdentificate=true; //thred redend code cancelled

            //set user parameters:
            SharedPreferences user = getSharedPreferences("user",MODE_PRIVATE);
            User.setEmail_id(user.getString("email",null));
            User.setName(user.getString("name",null));
            User.setSurname(user.getString("surname",null));
            User.setPhone_number(user.getString("phone",null));
            User.setPassword(user.getString("password",null));
            User.setStatement(true);
            Intent intent = new Intent(this, HomepageWithLogin.class);
            startActivity(intent);

            CreateUserMutation mutationCreateUser = new CreateUserMutation(User.getEmail_id(), User.getName(), User.getSurname(), User.getPassword(), User.getPhone_number(), User.getStatement());
            apolloClient.mutate(mutationCreateUser).enqueue(new ApolloCall.Callback<CreateUserMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<CreateUserMutation.Data> response) {}

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    e.printStackTrace();
                }
            });

        } else { //se il codice inviato in email e quello inserito dall'utente NON combaciano
            Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                    "In codice inserito non è valido.");
        }

    }

    public void pressResendCode(View v){
        //get data user_email from FirstRegistrationPage
        SharedPreferences user = getSharedPreferences("user",MODE_PRIVATE);
        String email = user.getString("email",null);

        Config.generateIdentificationCode(10000,100000, Config.RESEND_CODE);
        SendMail sm = new SendMail(this, email, Config.SUBJECT, Config.MESSAGE);
        sm.execute();
    }

    public void pressFirstRegPage (View v){
        Intent intent = new Intent(this, FirstRegistrationPage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit);
    }

    public void pressSecondRegPage (View v){
        Intent intent = new Intent(this, SecondRegistrationPage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit);
    }

    public void backActivity(View v){
        Intent intent = new Intent(this, HomepageNoLogin.class);
        startActivity(intent);
    }
}
