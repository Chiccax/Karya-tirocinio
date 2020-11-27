package com.example.tirocinio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.tirocinio.graphql.GetUserByIdQuery;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class FirstRegistrationPage extends AppCompatActivity {
    private ApolloClient apolloClient;
    private Timer redBorderColor;
    private Timer blueBorderColor;
    private static final int PERIOD =4000; //1000 = 1 secondo -> 3000 = 4 secondi

    public String nome="";
    public String cognome="";
    public String email="";

    public EditText et_nome;
    public EditText et_cognome;
    public EditText et_email;


    Intent intent_secondResigtrationPage;

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
            setContentView(R.layout._1_registration_page_landscape);
        } else {
            setContentView(R.layout._1_registration_page);
        }

        et_nome = (EditText) findViewById(R.id.reg_name);
        et_cognome = (EditText) findViewById(R.id.reg_surname);
        et_email = (EditText) findViewById(R.id.reg_email);

        if (savedInstanceState != null) {
            nome = savedInstanceState.getString("Nome");
            cognome = savedInstanceState.getString("Cognome");
            email = savedInstanceState.getString("Email");

            if(nome != null)
                et_nome.setText(nome);

            if(cognome != null)
                et_cognome.setText(cognome);

            if(email != null)
                et_email.setText(email);
        }

        //passaggio activity
        if(Utility.ET_email != "" && Utility.ET_surname != "" && Utility.ET_name != ""){
            et_email.setText(Utility.ET_email);
            et_cognome.setText(Utility.ET_surname);
            et_nome.setText(Utility.ET_name);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("Nome",nome);
        savedInstanceState.putString("Cognome",cognome);
        savedInstanceState.putString("Email",email);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        nome = savedInstanceState.getString("Nome");
        cognome = savedInstanceState.getString("Cognome");
        email = savedInstanceState.getString("Email");
    }

    public void pressContinue(View v){
       intent_secondResigtrationPage = new Intent(this, SecondRegistrationPage.class);
       check_ok(intent_secondResigtrationPage);
    }

    public void pressSecondRegPage (View v){
        pressContinue(v);
    }

    public void pressThirdRegPage (View v){
        Intent intent_thirdRegistrationPage = new Intent(this, ThirdRegistrationPage.class);
        check_ok(intent_thirdRegistrationPage);
    }

    private void check_ok_callback(boolean isOk, Intent intent){
        if(isOk){
            startActivity(intent);
            overridePendingTransition(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit);
        }
    }

    private void check_ok(Intent intent){
        //controllo  tutti i campi sono pieni
        if(et_cognome.getText().toString().trim().length() != 0
                && et_nome.getText().toString().trim().length() != 0
                && et_email.getText().toString().trim().length() != 0){

            GetUserByIdQuery queryGetUserById = new GetUserByIdQuery(et_email.getText().toString());
            apolloClient.query(queryGetUserById).enqueue(new ApolloCall.Callback<GetUserByIdQuery.Data>(){
                @Override
                public void onResponse(@NotNull Response<GetUserByIdQuery.Data> response) {
                    GetUserByIdQuery.GetUserById user = response.getData().getUserById();

                    if (user != null) {
                        FirstRegistrationPage.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                        "E' già presente un utente registrato con questa email, accedi o registrati con una email diversa per continuare");
                            }
                        });

                      check_ok_callback(false, null);
                      return;

                    }

                    else

                    {
                        if(Utility.isValidName(et_nome.getText().toString())
                                && Utility.isValidName(et_cognome.getText().toString())){

                            saveParameterFirstRegistrationPage(et_nome.getText().toString(), et_cognome.getText().toString(),et_email.getText().toString());

                            SharedPreferences newUser = getSharedPreferences("user",MODE_PRIVATE);
                            SharedPreferences.Editor editor = newUser.edit();
                            editor.putString("email",et_email.getText().toString().toLowerCase()); //--> SendEmail.class
                            editor.putString("name",et_nome.getText().toString());
                            editor.putString("surname",et_cognome.getText().toString());
                            editor.commit();

                            check_ok_callback(true, intent);
                            return;

                        }

                        else

                        {
                            changeColor();

                            if(!Utility.isValidName(et_nome.getText().toString()))
                                Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                        "Controlla che il nome sia corretto");

                            if(!Utility.isValidName(et_cognome.getText().toString()))
                                Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                        "Controlla che il cognome sia corretto");

                            check_ok_callback(false, null);

                            return;
                        }
                    }
                }
                @Override
                public void onFailure(@NotNull ApolloException e) {
                    e.printStackTrace();
                }
            });
        }

        else

        {
            Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                    "Tutti i campi devono essere compilati prima di proseguire.");

            changeColor();
            check_ok_callback(false, null);
        }
    }


    /*Setta il colore dei bordi degli EdotText a rosso per 4 secondi (tempo del toast)
     */
    private void changeColor(){
        redBorderColor = new Timer();
        blueBorderColor = new Timer();

        redBorderColor.schedule(new TimerTask() {
            @Override
            public void run() {
                if(et_nome.getText().toString().trim().length() == 0) { //nome vuoto
                    Utility.setRedBorderColor(et_nome);
                }
                if(et_cognome.getText().toString().trim().length() == 0) { //cognome vuoto
                    Utility.setRedBorderColor(et_cognome);
                }
                if(et_email.getText().toString().trim().length() == 0) { //email vuota
                    Utility.setRedBorderColor(et_email);
                }

                if(Utility.isValidName(et_nome.getText().toString()))
                    Utility.setRedBorderColor(et_nome);

                if(Utility.isValidName(et_cognome.getText().toString()))
                    Utility.setRedBorderColor(et_cognome);
            }
        },0,PERIOD);

        blueBorderColor.schedule(new TimerTask() {
            @Override
            public void run() {
                redBorderColor.cancel();
                    Utility.setBlueBorderColor(et_nome);
                    Utility.setBlueBorderColor(et_cognome);
                    Utility.setBlueBorderColor(et_email);
                blueBorderColor.cancel();
            }
        },PERIOD);
    }

    private void saveParameterFirstRegistrationPage(String nome, String cognome, String email){
        Utility.ET_name = nome;
        Utility.ET_surname = cognome;
        Utility.ET_email = email;
    }

    public void backActivity(View v){
        Intent intent = new Intent(this, HomepageNoLogin.class);
        startActivity(intent);
    }
}

