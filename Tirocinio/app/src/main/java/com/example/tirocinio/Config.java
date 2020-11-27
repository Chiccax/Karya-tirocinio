package com.example.tirocinio;

import java.util.UUID;

public class Config {
    public static String IDENTIFICATION_CODE;
    public static String NEW_IDENTIFICATION_CODE;
    public static int INDENTICATION_CODE_REG;


    //connection to database - change with your ipv4 id:
    public static final String SERVER_URL = "http://YOUR_IPV4_ID:4000/graphql";

    //to send email - change with your email data:
    public static final String SENDER_EMAIL = "YOUR_EMAIL";
    public static final String SENDER_PASSWORD = "YOUR_PASSWORD";
    //content
    public static final String SUBJECT = "Conferma la tua identità";
    public static String MESSAGE ="";
    //operation
    public static final int CONFIRM_ACCCOUNT = 0;
    public static final int RECOVERY_PASSWORD = 1;
    public static final int RESEND_CODE = 2;

    /**
     * generatore di numero casuale da lowerbound a upperbound
     * @param upperbound
     * @param lowerbound
     * @return
     */
    public static void generateIdentificationCode(int upperbound, int lowerbound, int operation) {
        INDENTICATION_CODE_REG = (int)(Math.random() * (upperbound - lowerbound + 1) + lowerbound);
        UUID uid = UUID.randomUUID();
        IDENTIFICATION_CODE = "" + uid;
        NEW_IDENTIFICATION_CODE = IDENTIFICATION_CODE.substring(0, 10);
        switch (operation){
            case CONFIRM_ACCCOUNT:
                MESSAGE =  INDENTICATION_CODE_REG  + " è il codice identificativo per confermare il tuo account!" +
                        "\n Il codice identificativo appena inviato sarà valido per 5 minuti, dopo di chè non potrà più essere utilizzato " +
                        "per confermare il tuo account.";
                break;

            case RECOVERY_PASSWORD:
                MESSAGE = NEW_IDENTIFICATION_CODE + " è la tua nuova password. Potrai usarla per accedere alle funzionalità dell'applicazione.\n" +
                        "Per cambiare la password vai nel tuo profilo utente e modifica le tue informazioni.";
                break;

            case RESEND_CODE:
                MESSAGE =  INDENTICATION_CODE_REG  + " è il nuovo codice identificativo per confermare il tuo account." +
                        "\n Il vecchio codice identificativo inviato in precedenza non sarà più valido." +
                        "\n Il codice identificativo appena inviato sarà valido per 5 minuti, dopo di chè non potrà più essere utilizzato " +
                        "per confermare il tuo account.";
                break;
        }
    }


}
