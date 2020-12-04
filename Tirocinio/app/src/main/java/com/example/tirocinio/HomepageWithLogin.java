package com.example.tirocinio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static java.lang.String.format;

public class HomepageWithLogin extends AppCompatActivity {

    private Button button;
    private ImageButton edit;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.home_page_with_login_landscape);
        } else {
            setContentView(R.layout.home_page_with_login);
        }

        button = (Button) findViewById(R.id.reporting);
        edit = (ImageButton) findViewById(R.id.editProfile);
        tv = (TextView) findViewById(R.id.nameUser);

        setUserName();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity();
            }
        });

        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
    }

    public void launchActivity(){
        Intent intent = new Intent(this, FireLocation.class);
        startActivity(intent);
    }

    private void setUserName(){
        SharedPreferences sharedPreferences;
        sharedPreferences=getApplicationContext().getSharedPreferences("stayLogged", 0);
        String nome = sharedPreferences.getString("nome", null);

        String s = "";

        if(nome != null) {
            s = format("Benvenuto %s",nome);
        } else {
            s = format("Benvenuto %s",User.getName());
        }

        tv.setText(s);
    }

    public void editProfile(){
        Intent intent = new Intent(this, UserProfile.class);
        startActivity(intent);
    }

    public void telegramConnection(View v){
        final String appName = "org.telegram.messenger";
        final boolean isAppInstalled = isAppAvailable(this.getApplicationContext(), appName);
        if(!isAppInstalled) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appName)));
            }
        } else {
            Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/karyareports_bot"));
            startActivity(telegram);
        }
    }

    public static boolean isAppAvailable(Context context, String appName)
    {
        PackageManager pm = context.getPackageManager();
        try
        {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }
}