package com.example.tirocinio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.tirocinio.graphql.GetUserByIdQuery;
import com.example.tirocinio.graphql.UpdateUserEmailMutation;
import com.example.tirocinio.graphql.UpdateUserNameMutation;
import com.example.tirocinio.graphql.UpdateUserPasswordMutation;
import com.example.tirocinio.graphql.UpdateUserPhoneNumberMutation;
import com.example.tirocinio.graphql.UpdateUserSurnameMutation;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class UserProfile extends FragmentActivity {
    private ApolloClient apolloClient;

    EditText nome;
    EditText cognome;
    EditText numeroCellulare;
    EditText email;
    EditText password;
    EditText confermaPassword;
    EditText confermaModifiche;
    Button buttonEdit;
    Button logout;
    LinearLayout confermaDati;
    Boolean openPD = false;
    Boolean openAD = false;

    //Variabili per cambio bordi in caso di errore
    private Timer redBorderColor;
    private Timer blueBorderColor;
    private static final int PERIOD = 4000; //1000 = 1 secondo -> 3000 = 4 secondi
    public boolean isEmpty = true;

    //show password
    private ImageView iv_showPassword;
    private ImageView iv_showConfirmPassword;
    private ImageView iv_showPasswordConfirm;

    //Dati inseriti nelle form
    String nuovoNome = null;
    String nuovoCognome = null;
    String nuovoNumero = null;
    String nuovaEmail = null;
    String nuovaPassword = null;
    String confermaNuovaPassword = null;
    String passwordAttuale = null;

    //Salvataggio dati per landscape
    String name;
    String surname;
    String number;
    String email_address;

    //stay Logged
    private SharedPreferences sharedpreferences;


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
            setContentView(R.layout.user_profile_landscape);
        } else {
            setContentView(R.layout.user_profile);
        }

        nome = findViewById(R.id.change_name);
        nome.setHint(User.getName());

        cognome = findViewById(R.id.change_surname);
        cognome.setHint(User.getSurname());
        numeroCellulare = findViewById(R.id.change_tel);
        numeroCellulare.setHint(User.getPhone_number());

        email = findViewById(R.id.change_email);
        email.setHint(User.getEmail_id());

        password = findViewById(R.id.change_password);
        confermaPassword = findViewById(R.id.reg_confirm_password);
        confermaModifiche = findViewById(R.id.pass_confirm);
        confermaModifiche.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(confermaModifiche.getText().toString().trim().length() != 0)
                    enableButton();
                else
                    disableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        buttonEdit = findViewById(R.id.commit_change);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        logout = findViewById(R.id.b_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressLogout();
            }
        });

        confermaDati = findViewById(R.id.confirm_data);

        iv_showPassword = findViewById(R.id.showPassword);
        iv_showConfirmPassword = findViewById(R.id.showConfirmPassword);
        iv_showPasswordConfirm = findViewById(R.id.showPasswordConfirm);

        iv_showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showPassword(password,iv_showPassword);
            }
        });
        iv_showConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showPassword(confermaPassword, iv_showConfirmPassword);
            }
        });
        iv_showPasswordConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showPassword(confermaModifiche, iv_showConfirmPassword);
            }
        });

        if (savedInstanceState != null) {
            name = savedInstanceState.getString("change_name");
            surname = savedInstanceState.getString("change_surname");
            number = savedInstanceState.getString("change_tel");
            email_address = savedInstanceState.getString("change_email");

            nome.setText(name);
            cognome.setText(surname);
            numeroCellulare.setText(number);
            email.setText(email_address);
        }
    }


    public void editProfile(){
        nuovoNome = nome.getText().toString();
        nuovoCognome = cognome.getText().toString();
        nuovoNumero = numeroCellulare.getText().toString();
        nuovaEmail = email.getText().toString();
        nuovaPassword = password.getText().toString();
        confermaNuovaPassword = confermaPassword.getText().toString();
        passwordAttuale = confermaModifiche.getText().toString();

        if(nuovoNome.trim().length() == 0 && nuovoCognome.trim().length() == 0 && nuovaEmail.trim().length() == 0 && nuovoNumero.trim().length() == 0 && nuovaPassword.trim().length() == 0){
            changeColor();
            Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                    "Compila almeno un campo di modifica!");

        } else if(passwordAttuale.trim().length() == 0){
            isEmpty = false;
            changeColor();
            Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                    "Inserisci la vecchia password per confermare le modifiche effettuate!");

        } else if (passwordAttuale.trim().length() != 0 && nuovoNome.trim().length() == 0 && nuovoCognome.trim().length() == 0 && nuovaEmail.trim().length() == 0 && nuovoNumero.trim().length() == 0 && nuovaPassword.trim().length() == 0) {
            changeColor();
        } else {
            if(passwordAttuale.equals(User.getPassword())) {
                //Se il campo da modificare è il nome
                if(nuovoNome.trim().length() != 0){
                    if(nuovoNome.equals(User.getName())){
                        Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                "Il nome " + nuovoNome + " risulta uguale al vecchio. Inserisci un nome diverso.");
                        nome.setText(null);
                        nome.setHint(User.getName());
                    } else {
                        User.setName(nuovoNome);

                        UpdateUserNameMutation updateUserNameMutation = new UpdateUserNameMutation(User.getEmail_id(), nuovoNome);
                        apolloClient.mutate(updateUserNameMutation).enqueue(new ApolloCall.Callback<UpdateUserNameMutation.Data>(){
                            @Override
                            public void onResponse(@NotNull Response<UpdateUserNameMutation.Data> response) {
                                UserProfile.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        nome.setText(null);
                                        nome.setHint(nuovoNome);
                                        confermaPassword.setText(null);
                                        confermaModifiche.setText(null);
                                        Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_success, null), new Toast(getApplicationContext()),
                                                "Operazione effettuata con successo!");
                                    }
                                });
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }

                //Se il campo da modificare è il cognome
                if(nuovoCognome.trim().length() != 0){
                    if(nuovoCognome.equals(User.getSurname())){
                        Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                "Il cognome " + nuovoCognome + " risulta uguale al vecchio. Inserisci un cognome diverso.");

                        cognome.setText(null);
                        cognome.setHint(User.getSurname());
                    } else {
                        User.setSurname(nuovoCognome);
                        UpdateUserSurnameMutation mutationUpdateUserSurname = new UpdateUserSurnameMutation(User.getEmail_id(), nuovoCognome);
                        apolloClient.mutate(mutationUpdateUserSurname).enqueue(new ApolloCall.Callback<UpdateUserSurnameMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<UpdateUserSurnameMutation.Data> response) {
                                UserProfile.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        cognome.setText(null);
                                        cognome.setHint(nuovoCognome);
                                        confermaModifiche.setText(null);
                                        Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_success, null), new Toast(getApplicationContext()),
                                                "Operazione effettuata con successo!");
                                    }
                                });
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }

                //Se il campo da modificare è l'email
                if(nuovaEmail.trim().length() != 0){
                    if(nuovaEmail.equals(User.getEmail_id())){
                        Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                "Inserire una email diversa da quella già esistente.");
                        email.setText(null);
                        email.setHint(User.getEmail_id());
                    }else {
                        GetUserByIdQuery queryGetUserById = new GetUserByIdQuery(nuovaEmail);
                        apolloClient.query(queryGetUserById).enqueue(new ApolloCall.Callback<GetUserByIdQuery.Data>(){
                            @Override
                            public void onResponse(@NotNull Response<GetUserByIdQuery.Data> response) {
                                GetUserByIdQuery.GetUserById user = response.getData().getUserById();
                                if (user == null) {
                                    UpdateUserEmailMutation mutationUpdateUserEmail = new UpdateUserEmailMutation(User.getEmail_id(), nuovaEmail);
                                    apolloClient.mutate(mutationUpdateUserEmail).enqueue(new ApolloCall.Callback<UpdateUserEmailMutation.Data>() {
                                        @Override
                                        public void onResponse(@NotNull Response<UpdateUserEmailMutation.Data> response) {
                                            UserProfile.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    email.setText(null);
                                                    email.setHint(nuovaEmail);
                                                    confermaModifiche.setText(null);
                                                    Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_success, null), new Toast(getApplicationContext()),
                                                            "Operazione effettuata con successo!");
                                                }
                                            });
                                            User.setEmail_id(nuovaEmail);
                                        }

                                        @Override
                                        public void onFailure(@NotNull ApolloException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                } else{
                                    UserProfile.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                                    "L'email è già esistente!");
                                            email.setText(null);
                                            email.setHint(User.getEmail_id());
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
                }

                //Se il campo da modificare è il numero di telefono
                if (nuovoNumero.trim().length() != 0) {
                    if(nuovoNumero.equals(User.getPhone_number())){
                        Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                "Inserire un numero di telefono diverso da quello già esistente.");
                        numeroCellulare.setText(null);
                        numeroCellulare.setHint(User.getPhone_number());
                    } else {
                        User.setPhone_number(nuovoNumero);
                        UpdateUserPhoneNumberMutation mutationUpdateUserPhoneNumber = new UpdateUserPhoneNumberMutation(User.getEmail_id(), nuovoNumero);
                        apolloClient.mutate(mutationUpdateUserPhoneNumber).enqueue(new ApolloCall.Callback<UpdateUserPhoneNumberMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<UpdateUserPhoneNumberMutation.Data> response) {
                                UserProfile.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        confermaModifiche.setText(null);
                                        numeroCellulare.setText(null);
                                        numeroCellulare.setHint(nuovoNumero);
                                        Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_success, null), new Toast(getApplicationContext()),
                                                "Operazione effettuata con successo!");
                                    }
                                });
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }

                //Se il campo da modificare è la password
                if(nuovaPassword.trim().length() != 0){
                    if(nuovaPassword.equals(User.getPassword())){
                        Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                "Inserire una password diversa da quella già esistente.");
                        password.setText(null);
                        confermaPassword.setText(null);
                    } else {
                        if (Utility.isValidPassword(nuovaPassword)) {
                            if (nuovaPassword.equals(confermaNuovaPassword)) {
                                User.setPassword(nuovaPassword);
                                UpdateUserPasswordMutation mutationUpdateUserPassword = new UpdateUserPasswordMutation(User.getEmail_id(), nuovaPassword);
                                apolloClient.mutate(mutationUpdateUserPassword ).enqueue(new ApolloCall.Callback<UpdateUserPasswordMutation.Data>() {
                                    @Override
                                    public void onResponse(@NotNull Response<UpdateUserPasswordMutation.Data> response) {
                                        UserProfile.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                password.setText(null);
                                                confermaPassword.setText(null);
                                                confermaModifiche.setText(null);
                                                Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_success, null), new Toast(getApplicationContext()),
                                                        "Operazione effettuata con successo!");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NotNull ApolloException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                        "La password e la conferma della password non coincidono");
                            }
                        } else {
                            Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                    "\"La password non è valida. Assicurati che contenga:\n -Minimo 8 caratteri \n - Minimo 2 numeri e 2 lettere \n - Minimo 1 carattere speciale\"");
                        }
                    }
                }
            } else {
                Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                        "La password inserita non è valida! Si prega di inserire la password attuale di accesso.");
                confermaModifiche.setText(null);
                isEmpty = false;
                changeColor();
            }
        }
    }

    private void changeColor(){
        redBorderColor = new Timer();
        blueBorderColor = new Timer();

        redBorderColor.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isEmpty == true){
                    if(nome.getText().toString().trim().length() == 0) {
                        Utility.setRedBorderColor(nome);
                    }
                    if(cognome.getText().toString().trim().length() == 0) {
                        Utility.setRedBorderColor(cognome);
                    }
                    if(numeroCellulare.getText().toString().trim().length() == 0) {
                        Utility.setRedBorderColor(numeroCellulare);
                    }
                    if(email.getText().toString().trim().length() == 0) {
                        Utility.setRedBorderColor(email);
                    }
                    if(password.getText().toString().trim().length() == 0) {
                        Utility.setRedBorderColor(password);
                    }
                    if(confermaPassword.getText().toString().trim().length() == 0) {
                        Utility.setRedBorderColor(confermaPassword);
                    }
                } else {
                    //if(confermaModifiche.getText().toString().trim().length() == 0) {
                        Utility.setRedBorderColor(confermaModifiche);
                    //}
                }
            }
        },0,PERIOD);

        blueBorderColor.schedule(new TimerTask() {
            @Override
            public void run() {
                redBorderColor.cancel();
                if(isEmpty == true) {
                    Utility.setBlueBorderColor(nome);
                    Utility.setBlueBorderColor(cognome);
                    Utility.setBlueBorderColor(numeroCellulare);
                    Utility.setBlueBorderColor(email);
                    Utility.setBlueBorderColor(password);
                    Utility.setBlueBorderColor(confermaPassword);
                } else {
                    Utility.setBlueBorderColor(confermaModifiche);
                    isEmpty = true;
                }
                blueBorderColor.cancel();
            }
        },PERIOD);
    }

    public void pressLogout(){

        sharedpreferences=getApplicationContext().getSharedPreferences("stayLogged", 0);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("nome");
        editor.remove("email");
        editor.commit();

        Intent intent = new Intent(this, HomepageNoLogin.class);
        startActivity(intent);
    }

    public void launchPreviousActivity(View v){
        Intent intent = new Intent(this, HomepageWithLogin.class);
        startActivity(intent);
    }

    public void openPersonalData(View v){
        if(openPD == false) {
            openPD = true;

            nome.setVisibility(View.VISIBLE);
            cognome.setVisibility(View.VISIBLE);
            numeroCellulare.setVisibility(View.VISIBLE);
            confermaDati.setVisibility(View.VISIBLE);
            iv_showPasswordConfirm.setVisibility(View.VISIBLE);

            email.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            iv_showPassword.setVisibility(View.GONE);
            confermaPassword.setVisibility(View.GONE);
            iv_showConfirmPassword.setVisibility(View.GONE);
        } else {
            openPD = false;
            nome.setVisibility(View.GONE);
            cognome.setVisibility(View.GONE);
            numeroCellulare.setVisibility(View.GONE);
            if(openAD == false) {
                confermaDati.setVisibility(View.GONE);
                iv_showPasswordConfirm.setVisibility(View.GONE);
            }
        }
    }

    public void openAccessData(View v) {
        if (openAD == false) {
            openAD = true;

            email.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            iv_showPassword.setVisibility(View.VISIBLE);
            confermaPassword.setVisibility(View.VISIBLE);
            iv_showConfirmPassword.setVisibility(View.VISIBLE);
            confermaDati.setVisibility(View.VISIBLE);
            iv_showPasswordConfirm.setVisibility(View.VISIBLE);

            nome.setVisibility(View.GONE);
            cognome.setVisibility(View.GONE);
            numeroCellulare.setVisibility(View.GONE);
        } else {
            openAD = false;
            email.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            iv_showPassword.setVisibility(View.GONE);
            confermaPassword.setVisibility(View.GONE);
            iv_showConfirmPassword.setVisibility(View.GONE);
            if (openPD == false) {
                confermaDati.setVisibility(View.GONE);
                iv_showPasswordConfirm.setVisibility(View.GONE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void enableButton(){
        {
            buttonEdit.setEnabled(true);
            buttonEdit.setTextColor(this.getResources().getColorStateList(R.color.white));
            buttonEdit.setBackgroundTintList(this.getResources().getColorStateList(R.color.blue));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void disableButton(){
        {
            buttonEdit.setEnabled(true);
            buttonEdit.setTextColor(this.getResources().getColorStateList(R.color.white));
            buttonEdit.setBackgroundTintList(this.getResources().getColorStateList(R.color.whiteTransparent));
        }
    }
}