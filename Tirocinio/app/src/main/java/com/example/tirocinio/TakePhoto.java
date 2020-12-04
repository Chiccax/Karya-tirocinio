package com.example.tirocinio;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TakePhoto extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 101;

    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    //popup
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button btn_send_anyway;
    private TextView btn_come_back;

    public Button btnCamera;
    public ImageButton btnForward;
    public ImageView img;
    public TextView jump;

    public Bitmap taken_photo = null; //foto scattata
    public String string_photo = " "; //codfica immagine bitmap in stringa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.take_photo_landscape);
        } else {
            setContentView(R.layout.take_photo);
        }

        img = (ImageView) findViewById(R.id.img_tp);

        btnCamera = (Button) findViewById(R.id.b_camera);
        btnForward = (ImageButton) findViewById(R.id.backButton);

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPreviousActivity();
            }
        });

        if (savedInstanceState != null) {
            taken_photo = savedInstanceState.getParcelable("TakenPhoto");

            img.setImageBitmap(taken_photo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){

            switch (requestCode){

                case CAMERA_PERMISSION_CODE:
                    taken_photo = (Bitmap) data.getExtras().get("data");
                    img.setImageBitmap(taken_photo);
                    BitmapToString(taken_photo);
                    break;

                case READ_EXTERNAL_STORAGE_PERMISSION_CODE:
                    if(resultCode == RESULT_OK){
                        Uri selectImage = data.getData();
                        try {
                            taken_photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        img.setImageBitmap(taken_photo);
                        BitmapToString(taken_photo);
                        taken_photo = null; //settato a null per poter riscattare la fotografia
                    }
                    break;
            }
        }
    }

    /**
     * Quando si preme la fotocamera si controlla che ci siano i permessi, successivamente si apre la fotocamera.*/
    public void pressCamera (View v){
        if(checkPermissions()){
            Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,CAMERA_PERMISSION_CODE);
        }

    }

    public void pressGallery (View v){
        if(checkPermissions()){
            Intent galleryIntent = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent,READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        }

    }

    public void pressContinue(View v){
        if(!string_photo.equals(" ")){
            SharedPreferences fire = getSharedPreferences("fire",MODE_PRIVATE);
            SharedPreferences.Editor editor = fire.edit();

            try {
                string_photo = Utility.compress(string_photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            editor.putString("foto", string_photo);

            editor.commit();

            Intent intent = new Intent (this, MoreInformation.class);
            startActivity(intent);
        } else {
            dialogBuilder = new AlertDialog.Builder(this);
            int orientation = getResources().getConfiguration().orientation;
            final View dialogShow;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                dialogShow = getLayoutInflater().inflate(R.layout.custom_popup_confirm_operation_landscape, null);
            } else {
                dialogShow = getLayoutInflater().inflate(R.layout.custom_popup_confirm_operation, null);
            }
            btn_come_back = dialogShow.findViewById(R.id.b_come_back);
            btn_send_anyway = dialogShow.findViewById(R.id.b_continue);
            dialogBuilder.setView(dialogShow);
            dialog = dialogBuilder.create();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            btn_send_anyway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences fire = getSharedPreferences("fire",MODE_PRIVATE);
                    SharedPreferences.Editor editor = fire.edit();
                    editor.putString("foto", string_photo);
                    editor.commit();
                    Intent intent = new Intent (getApplicationContext(), MoreInformation.class);
                    startActivity(intent);
                }
            });

            btn_come_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }
    }

    private void BitmapToString (Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        string_photo = Base64.encodeToString(b, Base64.DEFAULT);
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }
            return;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("TakenPhoto",taken_photo);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        taken_photo = savedInstanceState.getParcelable("TakenPhoto");
    }

    public void launchPreviousActivity(){
        Intent intent = new Intent(this, FireLocation.class);
        startActivity(intent);
    }
}