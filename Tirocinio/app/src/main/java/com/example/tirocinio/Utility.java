package com.example.tirocinio;

import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Utility{

    public static final int PASSWORD_LENGTH = 8;

    public static String ET_name = "";
    public static String ET_surname = "";
    public static String ET_tel_num = "";
    public static String ET_email = "";
    public static String ET_password = "";


    public static void setRedBorderColor(final EditText et) {
        et.setBackgroundResource(R.drawable.prop_error);
    }

    public static void setBlueBorderColor(final EditText et) {
        et.setBackgroundResource(R.drawable.prop_rounded_text);
    }

    public static void setVisibilityOn (final TextView tv){
        tv.setVisibility(View.VISIBLE);
    }

    public static void setVisibilityOff (final TextView tv){
        tv.setVisibility(View.INVISIBLE);
    }

    // to password's control
    public static boolean isValidPassword(String password) {
        if (password.length() < PASSWORD_LENGTH)
            return false;

        int charCount = 0;
        int numCount = 0;
        int specialCount = 0;

        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (isNumeric(ch))
                numCount++;
            else
                if (isLetter(ch))
                    charCount++;
            else
                if(!isLetter(ch) && !isLetter(ch))
                    specialCount ++;
            else
                return false;
        }
        //ritorna vero se ho almento due caratteri, almeno due numeri e almeno un carattere speciale
        return (charCount >= 2 && numCount >= 2 && specialCount >=1);
    }

    public static boolean isValidName(String name){

        if (name.length() <=2)
            return false;
        else {
            for (int i = 0; i < name.length(); i++) {
                char ch = name.charAt(i);
                if (isLetter(ch) || isSpace(ch))
                    return true;
                return false;
            }
        }
        return false;
    }

    private static boolean isLetter(char ch) {
        ch = Character.toUpperCase(ch);
        if(ch>='A' && ch<='Z')
            return true;
        return false;
    }

    private static boolean isSpace(char ch){
        if(ch == ' ')
            return true;
        return false;
    }

    private static boolean isNumeric(char ch) {
        return (ch >= '0' && ch <= '9');
    }

    public static void showPassword(EditText et, ImageView iv){
        if (et.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
            et.setTransformationMethod(new SingleLineTransformationMethod());
            iv.setImageResource(R.drawable.ic_password_visibility);
        }
        else {
            et.setTransformationMethod(new PasswordTransformationMethod());
            iv.setImageResource(R.drawable.ic_password_visibility_off);
        }

        et.setSelection(et.getText().length());
    }

    public static void newCustomToast (View v, Toast toast, String s){
        TextView tv = (TextView) v.findViewById(R.id.customTextView);
        tv.setText(s);
        toast.setView(v);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0,0);
        toast.show();
    }

    //spiegazioni delle varie activity:
    public static String FirstRegistrationPage = "La registrazione si compone di tre passaggi, in questo primo passaggio ti verrà chiesto l'inserimento di nome, cognome ed e-mail." +
            "Quest'ultima verrà usata per verificare l'autenticità della registrazione.";
    public static String SecondRegistrationPage = "Nella seconda parte, ti verranno poi chiesti un numero di telefono e la password.";
    public static String ThirdRegistrationPage = "Al termine dei precedenti inserimenti verrà inviato un codice all'email specificata e, una volta inserito" +
            "il codice nell'apposito campo, la registrazione sarà terminata.";
    public static String HomepageWithLogin = "Nella homepage sarà possibile modificare le informazioni del proprio account, cliccando sull'icona in alto a sinistra. " +
            "Sarà possibile effettuare la segnalazione di un incendio schiacciando sul bottone al centro dello schermo.";
    public static String UserProfile = "Da quì è possibile modificare i propri dati personali, quali nome, cognome e password;" +
            "e i propri dati di accesso, quali email e password. Per confermare tutti i cambiamenti effettuati è necessario fornire la password.";
    public static String FireLocation = "Per la segnalazione dell'incendio verrà chiesto di indicare la posizione approssimativa dell'incendio. Per farlo basterà" +
            "muoversi con un dito all'interno della mappa e cliccare sulla zona di interesse. Comparirà un marker rosso, a questo punto si può procedere.";
    public static String TakePhoto = "Verrà chiuesto di inserire una foto inerente all'incendio, questa può essere scelta sia dalla galleria che scattata al momento.";
    public static String MoreInformation = "L'ultimo passaggio sarà quello di indicare eventualmente informazioni aggiuntive all'incendio, in tal modo Vigili del Fuoco potranno" +
            "valutare con più facilità la gravità della situazione.";



}
