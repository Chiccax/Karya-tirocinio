package com.example.tirocinio;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class Tutorial extends AppCompatActivity {

    private TextView tv_description;
    private Button btn_next;
    private ImageView layout_screen;
    private ImageView imageView_1, imageView_2, imageView_3, imageView_4, imageView_5, imageView_6, imageView_7, imageView_8, imageView_9;
    private static int i = 1; //indice imgView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);
        tv_description = findViewById(R.id.description);
        btn_next = findViewById(R.id.b_continue);

        layout_screen = findViewById(R.id.iv_container);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                i++;
                switch (i){
                    case 2:
                        imageView_1 = findViewById(R.id.v1);
                        imageView_2 = findViewById(R.id.v2);
                        tv_description.setText(Utility.FirstRegistrationPage);
                        changeColorPoint(imageView_1,imageView_2);
                        layout_screen.setBackgroundResource(R.drawable._2_bg);
                        break;
                    case 3:
                        imageView_3 = findViewById(R.id.v3);
                        changeColorPoint(imageView_2,imageView_3);
                        tv_description.setText(Utility.SecondRegistrationPage);
                        layout_screen.setBackgroundResource(R.drawable._3_bg);
                        break;
                    case 4:
                        imageView_4 = findViewById(R.id.v4);
                        changeColorPoint(imageView_3,imageView_4);
                        tv_description.setText(Utility.ThirdRegistrationPage);
                        layout_screen.setBackgroundResource(R.drawable._4_bg);
                        break;
                    case 5:
                        imageView_5 = findViewById(R.id.v5);
                        changeColorPoint(imageView_4,imageView_5);
                        tv_description.setText(Utility.HomepageWithLogin);
                        layout_screen.setBackgroundResource(R.drawable._5_bg);
                        break;
                    case 6:
                        imageView_6 = findViewById(R.id.v6);
                        changeColorPoint(imageView_5,imageView_6);
                        tv_description.setText(Utility.UserProfile);
                        //layout_screen.setBackgroundResource(R.drawable._6_bg);
                        break;
                    case 7:
                        imageView_7 = findViewById(R.id.v7);
                        changeColorPoint(imageView_6,imageView_7);
                        tv_description.setText(Utility.FireLocation);
                        layout_screen.setBackgroundResource(R.drawable._7_bg);
                        break;
                    case 8:
                        imageView_8 = findViewById(R.id.v8);
                        changeColorPoint(imageView_7,imageView_8);
                        tv_description.setText(Utility.TakePhoto);
                        layout_screen.setBackgroundResource(R.drawable._8_bg);
                        break;
                    case 9:
                        imageView_9 = findViewById(R.id.v9);
                        changeColorPoint(imageView_8,imageView_9);
                        tv_description.setText(Utility.MoreInformation);
                        layout_screen.setBackgroundResource(R.drawable._9_bg);
                        break;
                    default:
                        startApplication();
                        break;
                }



            }
        });
    }


    private void startApplication(){
        startActivity(new Intent(this, HomepageNoLogin.class));
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeColorPoint(ImageView iv1, ImageView iv2){
        iv1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.palette_two_transparent)));
        iv2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.palette_two)));
    }

}
