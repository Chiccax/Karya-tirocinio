package com.example.tirocinio;

public class User {

    private static String email_id;
    private static String name;
    private static String surname;
    private static String password;
    private static String phone_number;
    private static boolean statement;

    /**
     *
     * @param email_id
     * @param name
     * @param surname
     * @param password
     * @param phone_number
     * @param statement
     */
    public User(String email_id, String name, String surname, String password, String phone_number, boolean statement){
        this.email_id = email_id;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.phone_number = phone_number;
        this.statement = statement;
    }

    public static String getEmail_id() {
        return email_id;
    }

    public static void setEmail_id(String email_id) {
        User.email_id = email_id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static String getSurname() {
        return surname;
    }

    public static void setSurname(String surname) {
        User.surname = surname;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        User.password = password;
    }

    public static String getPhone_number() {
        return phone_number;
    }

    public static void setPhone_number(String phone_number) {
        User.phone_number = phone_number;
    }

    public static boolean getStatement() {
        return statement;
    }

    public static void setStatement(boolean statement) {
        User.statement = statement;
    }

    @Override
    public String toString() {
        String s;
        s = "Nome: " + this.name + "\n" + "Cognome: " + this.surname + "\n" + "Email: " + this.email_id + "\n" + "Password: " + this.password + "\n" + "Numero: " + this.phone_number + "\n" + this.statement;
        return s;
    }
}
