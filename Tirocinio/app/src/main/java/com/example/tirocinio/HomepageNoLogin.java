package com.example.tirocinio;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.tirocinio.graphql.GetUserByIdAndPasswordQuery;
import com.example.tirocinio.graphql.GetUserByIdQuery;
import com.example.tirocinio.graphql.UpdateUserPasswordMutation;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class HomepageNoLogin extends AppCompatActivity {
    private ApolloClient apolloClient;

    private Timer redBorderColor;
    private Timer blueBorderColor;
    private static final int PERIOD =4000; //1000 = 1 secondo -> 3000 = 4 secondi
    private static boolean popup_email_ok = true;
    private static boolean popup_email_inexistent = true;

    private static boolean email_ok = true;
    private static boolean password_ok = true;

    //popup
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText popup_email;
    private Button btn_sendRecoveryCode;
    private TextView btn_cancel_operation;
    private TextView rec_insert_email;
    private TextView rec_email_inexistent;

    public String email="";
    public String password="";

    //stayLogged
    private SharedPreferences sharedPreferences;
    private CheckBox stay_logged;

    //orizzontale
    public EditText et_email;
    public EditText et_password;
    public ImageView iv_showPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        apolloClient = ApolloClient.builder()
                .serverUrl(Config.SERVER_URL)
                .okHttpClient(okHttpClient)
                .build();

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.homepage_no_login_landscape);
        } else {
            setContentView(R.layout.homepage_no_login);
        }

        et_email = (EditText) findViewById(R.id.email);
        et_password = (EditText) findViewById(R.id.password);
        iv_showPassword = findViewById(R.id.showPassword);
        stay_logged = findViewById(R.id.checkbox_stay_logged);

        if (savedInstanceState != null) {
            email = savedInstanceState.getString("Email");
            password = savedInstanceState.getString("Password");
            //settaggio parametri
            et_email.setText(email);
            et_password.setText(password);
        }

        iv_showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showPassword(et_password, iv_showPassword);
            }
        });

        sharedPreferences=getApplicationContext().getSharedPreferences("stayLogged", 0);
        String nome_sp = sharedPreferences.getString("nome", null);
        String email_sp = sharedPreferences.getString("email",null);
        if(nome_sp != null && email_sp != null){
            GetUserByIdQuery queryGetUserById = new GetUserByIdQuery(email_sp);
            apolloClient.query(queryGetUserById).enqueue(new ApolloCall.Callback<GetUserByIdQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<GetUserByIdQuery.Data> response) {
                    GetUserByIdQuery.GetUserById user = response.getData().getUserById();
                    if(user != null){
                        User.setEmail_id(user.email());
                        User.setName(user.name());
                        User.setSurname(user.surname());
                        User.setPassword(user.password());
                        User.setPhone_number(user.phone_number());
                        User.setStatement(true);
                    }
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    e.printStackTrace();
                }
            });
            Intent i = new Intent (getApplicationContext(),HomepageWithLogin.class);
            startActivity(i);
        }
    }

    public void pressLog(View v){
        if (et_email.getText().toString().trim().length() != 0 && et_password.getText().toString().trim().length() !=0){
            GetUserByIdQuery queryGetUserById = new GetUserByIdQuery(et_email.getText().toString());
            apolloClient.query(queryGetUserById).enqueue(new ApolloCall.Callback<GetUserByIdQuery.Data>(){
                @Override
                public void onResponse(@NotNull Response<GetUserByIdQuery.Data> response) {
                    GetUserByIdQuery.GetUserById user = response.getData().getUserById();
                    if(user != null){
                        User.setEmail_id(user.email());
                        User.setName(user.name());
                        User.setSurname(user.surname());
                        User.setPassword(user.password());
                        User.setPhone_number(user.phone_number());
                        User.setStatement(true);
                        email = et_email.getText().toString();
                        password = et_password.getText().toString();
                        GetUserByIdAndPasswordQuery queryGetUserByIdAndPassword = new GetUserByIdAndPasswordQuery(et_email.getText().toString(), et_password.getText().toString());
                        apolloClient.query(queryGetUserByIdAndPassword).enqueue(new ApolloCall.Callback<GetUserByIdAndPasswordQuery.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<GetUserByIdAndPasswordQuery.Data> response) {
                                GetUserByIdAndPasswordQuery.GetUserByIdAndPassword userComplete = response.getData().getUserByIdAndPassword();
                                if(userComplete != null){

                                    if(stay_logged.isChecked()){
                                        sharedPreferences=getApplicationContext().getSharedPreferences("stayLogged", 0);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("email",User.getEmail_id());
                                        editor.putString("nome",User.getName());
                                        editor.commit();
                                    }

                                    Intent intent = new Intent(HomepageNoLogin.this, HomepageWithLogin.class);
                                    startActivity(intent);
                                } else
                                {
                                    password_ok = false;
                                    changeColor();
                                    HomepageNoLogin.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                                    "La password inserita non è corretta.");
                                        }
                                    });
                                    et_password.setText(null);
                                    et_password.setHint("Password");
                                }
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        email_ok = false;
                        changeColor();
                        HomepageNoLogin.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                        "L'email inserita non è corretta.");
                            }
                        });
                        et_email.setText(null);
                        et_email.setHint("Email");
                    }
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    e.printStackTrace();
                }
            });
        } else {
            if(et_email.getText().toString().trim().length() == 0 && et_password.getText().toString().trim().length() != 0){
                changeColor();
                Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                        "Inserisci l'email per continuare");
            } else
            if(et_password.getText().toString().trim().length() == 0 && et_email.getText().toString().trim().length() != 0){
                changeColor();
                Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                        "Inserisci la password per continuare");
            } else
            if(et_password.getText().toString().trim().length() == 0 && et_email.getText().toString().trim().length() == 0){
                changeColor();
                Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                        "Compila i campi per continuare.");
            }
        }
    }

    public void pressReg(View v){
        Intent intent = new Intent(this, FirstRegistrationPage.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        email = et_email.getText().toString();
        password = et_password.getText().toString();

        savedInstanceState.putString("Email",email);
        savedInstanceState.putString("Password",password);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        email = savedInstanceState.getString("Email");
        password = savedInstanceState.getString("Password");
    }

    public void pressPasswordRecovery(View v){
        dialogBuilder = new AlertDialog.Builder(this);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            final View recoveryPasswordPopupView = getLayoutInflater().inflate(R.layout.custom_popup_recovery_password_landscape, null);
            popup_email = (EditText) recoveryPasswordPopupView.findViewById(R.id.popup_email);
            rec_insert_email = (TextView) recoveryPasswordPopupView.findViewById(R.id.rec_insert_email);
            rec_email_inexistent = (TextView) recoveryPasswordPopupView.findViewById(R.id.rec_email_inexistent);
            btn_sendRecoveryCode = (Button) recoveryPasswordPopupView.findViewById(R.id.b_send_code);
            btn_cancel_operation = (TextView) recoveryPasswordPopupView.findViewById(R.id.b_cancel_operation);

            dialogBuilder.setView(recoveryPasswordPopupView);
            dialog = dialogBuilder.create();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }
        else{
            final View recoveryPasswordPopupView = getLayoutInflater().inflate(R.layout.custom_popup_recovery_password, null);
            popup_email = (EditText) recoveryPasswordPopupView.findViewById(R.id.popup_email);
            rec_insert_email = (TextView) recoveryPasswordPopupView.findViewById(R.id.rec_insert_email);
            rec_email_inexistent = (TextView) recoveryPasswordPopupView.findViewById(R.id.rec_email_inexistent);
            btn_sendRecoveryCode = (Button) recoveryPasswordPopupView.findViewById(R.id.b_send_code);
            btn_cancel_operation = (TextView) recoveryPasswordPopupView.findViewById(R.id.b_cancel_operation);

            dialogBuilder.setView(recoveryPasswordPopupView);
            dialog = dialogBuilder.create();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }

        if(et_email.getText().toString().trim().length() != 0){
            popup_email.setText(et_email.getText().toString());
        }

        btn_sendRecoveryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popup_email.getText().toString().trim().length() == 0) {
                    popup_email_ok = false;
                    Utility.setVisibilityOn(rec_insert_email);
                    changeColor();

                } else if(popup_email.getText().toString().trim().length() != 0){
                    GetUserByIdQuery queryGetUserById = new GetUserByIdQuery(popup_email.getText().toString());
                    apolloClient.query(queryGetUserById).enqueue(new ApolloCall.Callback<GetUserByIdQuery.Data>(){
                        @Override
                        public void onResponse(@NotNull Response<GetUserByIdQuery.Data> response) {
                            GetUserByIdQuery.GetUserById user = response.getData().getUserById();
                            if(user!=null){
                                User.setEmail_id(user.email());
                                User.setName(user.name());
                                User.setSurname(user.surname());
                                User.setPassword(user.password());
                                User.setPhone_number(user.phone_number());
                                HomepageNoLogin.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendEmailANDChangingPassword();
                                        dialog.cancel();
                                        et_email.setText(popup_email.getText().toString());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    GetUserByIdQuery queryGetUserById = new GetUserByIdQuery(et_email.getText().toString());
                    apolloClient.query(queryGetUserById).enqueue(new ApolloCall.Callback<GetUserByIdQuery.Data>(){
                        @Override
                        public void onResponse(@NotNull Response<GetUserByIdQuery.Data> response) {
                            GetUserByIdQuery.GetUserById user = response.getData().getUserById();
                            if(user==null){
                                popup_email_inexistent = false;
                                Utility.setVisibilityOn(rec_email_inexistent);
                                changeColor();
                                popup_email.setText(null);
                                popup_email.setHint("E-mail");
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });

        btn_cancel_operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void sendEmailANDChangingPassword(){
        Config.generateIdentificationCode(10000,100000, Config.RECOVERY_PASSWORD);
        SendMail sm = new SendMail(HomepageNoLogin.this, popup_email.getText().toString(), Config.SUBJECT, Config.MESSAGE);
        sm.execute();

        UpdateUserPasswordMutation mutationUpdateUserPassword = new UpdateUserPasswordMutation(User.getEmail_id(), Config.NEW_IDENTIFICATION_CODE);
        apolloClient.mutate(mutationUpdateUserPassword ).enqueue(new ApolloCall.Callback<UpdateUserPasswordMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<UpdateUserPasswordMutation.Data> response) {}

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
            }
        });
    }

    private void changeColor(){

        redBorderColor = new Timer();
        blueBorderColor = new Timer();

        redBorderColor.schedule(new TimerTask() {
            @Override
            public void run() {

                if(popup_email_ok && popup_email_inexistent){
                    if(et_email.getText().toString().trim().length() == 0 && et_password.getText().toString().trim().length() != 0){
                        Utility.setRedBorderColor(et_email);
                    } else if(et_password.getText().toString().trim().length() == 0 && et_email.getText().toString().trim().length() != 0) {
                        Utility.setRedBorderColor(et_password);
                    } else if(et_password.getText().toString().trim().length() == 0 && et_email.getText().toString().trim().length() == 0){
                        Utility.setRedBorderColor(et_password);
                        Utility.setRedBorderColor(et_email);
                    }
                }

                if(!email_ok)
                    Utility.setRedBorderColor(et_email);
                if(!password_ok)
                    Utility.setRedBorderColor(et_password);


                if(!popup_email_ok || !popup_email_inexistent)
                    Utility.setRedBorderColor(popup_email);

            }
        },0,PERIOD);

        blueBorderColor.schedule(new TimerTask() {
            @Override
            public void run() {
                redBorderColor.cancel();
                Utility.setBlueBorderColor(et_email);
                Utility.setBlueBorderColor(et_password);
                if(!popup_email_ok){
                    Utility.setBlueBorderColor(popup_email);
                    Utility.setVisibilityOff(rec_insert_email);
                }
                if(!popup_email_inexistent) {
                    Utility.setBlueBorderColor(popup_email);
                    Utility.setVisibilityOff(rec_email_inexistent);
                }
                blueBorderColor.cancel();
            }
        },PERIOD);
    }
}
