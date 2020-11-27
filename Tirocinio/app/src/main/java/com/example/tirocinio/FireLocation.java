package com.example.tirocinio;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class FireLocation extends FragmentActivity implements OnMapReadyCallback {
    ImageButton backButton;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private LatLng latLng;
    private GoogleMap map;

    public Button buttonNext;

    Geocoder geocoder;
    public String positionActually;
    public String positionFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.fire_location_landscape);
        } else {
            setContentView(R.layout.fire_location);
        }

        geocoder = new Geocoder(this,Locale.getDefault());

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPreviousActivity();
            }
        });

        buttonNext = (Button) findViewById(R.id.next_fl);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchNextActivity();
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
    }

    public void launchPreviousActivity(){
        Intent intent = new Intent(this, HomepageWithLogin.class);
        startActivity(intent);
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(FireLocation.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        List<Address> addresses;

        latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Sono qui!");
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        map.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        try {
            addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            positionActually = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setting a click event handler for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            //LatLngTwo is the position of the second point
            @Override
            public void onMapClick(LatLng latLngTwo) {
                map.clear();

                List<Address> addressesFire;

                latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Sono qui!");
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                map.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                MarkerOptions markerOptionsTwo = new MarkerOptions().position(latLngTwo);
                map.animateCamera(CameraUpdateFactory.newLatLng(latLngTwo));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngTwo, 20));
                map.addMarker(markerOptionsTwo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                try {
                    addressesFire = geocoder.getFromLocation(latLngTwo.latitude, latLngTwo.longitude, 1);
                    positionFire = addressesFire.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    public void launchNextActivity(){
        if(positionFire != null){
            SharedPreferences fire = getSharedPreferences("fire",MODE_PRIVATE);
            SharedPreferences.Editor editor = fire.edit();

            editor.putString("posizioneAttuale", positionActually);
            editor.putString("posizioneIncendio", positionFire);

            editor.commit();


            Intent intent = new Intent(this, TakePhoto.class);
            startActivity(intent);
        } else {
            Utility.newCustomToast(getLayoutInflater().inflate(R.layout.custom_toast_error, null), new Toast(getApplicationContext()),
                    "Prima di continuare devi indicare la posizione dell'incendio.");
        }
    }
}