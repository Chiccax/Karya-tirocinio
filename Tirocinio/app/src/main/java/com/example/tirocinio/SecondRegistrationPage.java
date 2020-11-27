package com.example.tirocinio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SecondRegistrationPage extends AppCompatActivity {

    private Timer redBorderColor;
    private Timer blueBorderColor;
    private static final int PERIOD =4000; //1000 = 1 secondo -> 3000 = 4 secondi

    private static Boolean matchPassword = false;
    private static Boolean isValidPassword = false;


    private String telefono = "";

    private EditText et_telefono;
    private EditText et_password;
    private EditText et_confirm_password;
    private ImageView iv_showPassword;
    private ImageView iv_showConfirmPassword;
    private CheckBox cb1;
    private CheckBox cb2;
    private Boolean tuttiCompilati = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout._2_registration_page_landscape);
        } else {
            setContentView(R.layout._2_registration_page);
        }

        cb1 = findViewById(R.id.checkbox_1);
        cb2 = findViewById(R.id.checkbox_2);
        et_telefono = findViewById(R.id.reg_tel);
        et_password = findViewById(R.id.reg_password);
        et_confirm_password = findViewById(R.id.reg_confirm_password);

        iv_showPassword = findViewById(R.id.showPassword);
        iv_showConfirmPassword = findViewById(R.id.showConfirmPassword);

        iv_showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showPassword(et_password,iv_showPassword);
            }
        });
        iv_showConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showPassword(et_confirm_password, iv_showConfirmPassword);
            }
        });

        if (savedInstanceState != null) {
            telefono = savedInstanceState.getString("Nome");

            if (telefono != null)
                et_telefono.setText(telefono);
        }

        //passaggio activity
        if(Utility.ET_tel_num != ""){
            et_telefono.setText(Utility.ET_tel_num);
        }
        if(Utility.ET_password != ""){
            et_password.setText(Utility.ET_password);
        }
    }

    private boolean checkboxs_ok(){
        if(cb1.isChecked() && cb2.isChecked())
            return true;
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("Telefono",telefono);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        telefono = savedInstanceState.getString("Telefono");
    }

    public void pressContinue(View v){
        Intent thirdRegistrationPage = new Intent (this,ThirdRegistrationPage.class);
        if(check_ok()){
            startActivity(thirdRegistrationPage);
            overridePendingTransition(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit);
        }

    }

    public void pressFirstRegPage (View v){
        Intent intent = new Intent(this, FirstRegistrationPage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit);
    }

    public void pressThirdRegPage (View v){
        pressContinue(v);
    }

    private boolean check_ok(){
        SharedPreferences user = getSharedPreferences("user",MODE_PRIVATE);
        String email = user.getString("email",null);

        //se tutti sono compilati
        if(et_telefono.getText().toString().trim().length() != 0 &&
                et_password.getText().toString().trim().length() != 0 &&
                et_confirm_password.getText().toString().trim().length() != 0){
            tuttiCompilati = true; //tutti i campi sono compilati
            //se la password non è valida esce un errore
            if(!Utility.isValidPassword(et_password.getText().toString())){
                isValidPassword = false;
                Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                        "La password non è valida. Assicurati che contenga:\n -Minimo 8 caratteri \n - Minimo 2 numeri e 2 lettere \n - Minimo 1 carattere speciale");
                changeColor();
                return false;
                //se la password è valida,
            } else {
                isValidPassword = true;

                //se le due password coincidono
                if(et_password.getText().toString().equals(et_confirm_password.getText().toString())){
                    matchPassword = true;
                    saveParameterSecondRegistrationPage(et_telefono.getText().toString(),et_password.getText().toString());
                    //salvo telefono e password in SharedPreferences
                    SharedPreferences.Editor editor = user.edit();
                    editor.putString("phone",et_telefono.getText().toString());
                    editor.putString("password",et_password.getText().toString());
                    editor.commit();

                    if (checkboxs_ok()){ //le checkbox sono selezionate e
                        if (matchPassword){ //le due password sono uguali
                            Config.generateIdentificationCode(10000,100000, Config.CONFIRM_ACCCOUNT);
                            SendMail sm = new SendMail(this, email, Config.SUBJECT, Config.MESSAGE);
                            sm.execute();
                            return true;
                        } else return false;
                    } else {
                        Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                                "Per continuare devi necessariamente dichiarare la tua maggiore età e dare il consenso al trattamento dei tuoi dati personali");

                        return false;
                    }

                } else {
                    //se le due password non coincidono compare l'errore
                    matchPassword = false;
                    //le due password non coincidono
                    Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                            "La password e la conferma della password non coincidono.");
                    changeColor();
                    return false;
                }

            }

        } else { //se almeno uno non è compilato
            Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                    "Controlla di aver inserito correttamente tutti i campi");

            tuttiCompilati = false;
            changeColor();
            return false;
        }
    }

    /*Setta il colore dei bordi degli EditText a rosso per 4 secondi (tempo del toast)
     */
    private void changeColor(){

        redBorderColor = new Timer();
        blueBorderColor = new Timer();

        redBorderColor.schedule(new TimerTask() {
            @Override
            public void run() {
                //per ogni parametro passato alla funzione
                if(et_telefono.getText().toString().trim().length() == 0)//nome vuoto
                    Utility.setRedBorderColor(et_telefono);

                if(et_password.getText().toString().trim().length() == 0)//cognome vuoto
                    Utility.setRedBorderColor(et_password);

                if(et_confirm_password.getText().toString().trim().length() == 0)//email vuota
                    Utility.setRedBorderColor(et_confirm_password);

                if(!matchPassword || !isValidPassword){
                    Utility.setRedBorderColor(et_password);
                    Utility.setRedBorderColor(et_confirm_password);
                }
            }
        },0,PERIOD);

        blueBorderColor.schedule(new TimerTask() {
            @Override
            public void run() {
                redBorderColor.cancel();
                Utility.setBlueBorderColor(et_telefono);
                Utility.setBlueBorderColor(et_password);
                Utility.setBlueBorderColor(et_confirm_password);
                blueBorderColor.cancel();
            }
        },PERIOD);
    }

    private void saveParameterSecondRegistrationPage(String phone_number, String password){
        Utility.ET_tel_num = phone_number;
        Utility.ET_password = password;
    }

    public void backActivity(View v){
        Intent intent = new Intent(this, HomepageNoLogin.class);
        startActivity(intent);
    }
}