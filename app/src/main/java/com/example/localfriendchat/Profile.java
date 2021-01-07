package com.example.localfriendchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.localfriendchat.Retrofit.User;
import com.example.localfriendchat.Retrofit.UserApi;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity implements LocationListener  {

    private Button logout;


    public JsonObject newlocation=new JsonObject();;
    private double latitude;
    private double longitude;


    public static final String SHARED_PREFS="shared_prefs";
    public static final String LOGGED_IN="logged_in";
    public static final String USERNAME="username";
    public static final String EMAIL="email";
    public static final String FIRST_NAME="first_name";
    public static final String LAST_NAME="last_name";


    private static String useremail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        useremail=sharedPreferences.getString(EMAIL,"");
        Toast.makeText(this, "EMAIL "+useremail, Toast.LENGTH_SHORT).show();


        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();


                editor.clear();

                editor.putBoolean(LOGGED_IN,false);

                editor.apply();

                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new Profile();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            this,
                            new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                            123);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 10, locationListener);
        }

    }

    @Override
    public void onLocationChanged(Location loc) {
        longitude =loc.getLongitude();
        Log.v("LAATTI", String.valueOf(longitude));
        latitude = loc.getLatitude();
        Log.v("LONGI", String.valueOf(latitude));


        newlocation.addProperty("email",useremail);
        newlocation.addProperty("latitude",latitude);
        newlocation.addProperty("longitude",longitude);


        Call<JsonObject> call= UserApi.getUserService().locationupdate(newlocation);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                Toast.makeText(getApplicationContext(), "Location Update Successfull", Toast.LENGTH_SHORT).show();
                Log.e("USERRRR", String.valueOf(response.body()));

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Location update failed", Toast.LENGTH_SHORT).show();
                Log.e("EEEEEEEEEEEEEEEEEEEEEE",t.toString());

            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}