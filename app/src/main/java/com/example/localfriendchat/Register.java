package com.example.localfriendchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.localfriendchat.Retrofit.UserApi;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity implements LocationListener {

    private com.google.android.material.textfield.TextInputEditText username, firstname, lastname, email, password;
    private Button register;
    private JsonObject user;


    public static final String SHARED_PREFS="shared_prefs";
    public static final String LOGGED_IN="logged_in";
    public static final String USERNAME="username";
    public static final String EMAIL="email";
    public static final String FIRST_NAME="first_name";
    public static final String LAST_NAME="last_name";


    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        username = findViewById(R.id.reg_username);
        firstname = findViewById(R.id.reg_fname);
        lastname = findViewById(R.id.reg_lname);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_pass);

        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                senddata();
            }
        });

        LocationListener locationListener = new Register();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            Register.this,
                            new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                            123);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 10, locationListener);
        }

    }

    private void senddata(){

        user=new JsonObject();
        user.addProperty("username",username.getText().toString().trim());
        user.addProperty("first_name",firstname.getText().toString().trim());
        user.addProperty("last_name",lastname.getText().toString().trim());
        user.addProperty("email",email.getText().toString().trim());
        user.addProperty("latitude",78.0000000);
        user.addProperty("longitude",26.0000000);
        user.addProperty("password",password.getText().toString().trim());


        Call<JsonObject> call= UserApi.getUserService().userregister(user);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Toast.makeText(getApplicationContext(), " Registered Successfully", Toast.LENGTH_SHORT).show();
                Log.e("RRRRESSSSS",response.body().toString());
//                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Registered Failed", Toast.LENGTH_SHORT).show();
                Log.e("EEEEEEEEEEEEEEEEEEEEEE",t.toString());

            }
        });

    }

    @Override
    public void onLocationChanged(Location loc) {
//        longitude =loc.getLongitude();
//        Log.v("LAATTI", String.valueOf(longitude));
//        latitude = loc.getLatitude();
//        Log.v("LONGI", String.valueOf(latitude));
        /*------- To get city name from coordinates -------- */
//        String cityName = null;
//        Geocoder gcd = new Geocoder(this, Locale.getDefault());
//        List<Address> addresses;
//        try {
//            addresses = gcd.getFromLocation(loc.getLatitude(),
//                    loc.getLongitude(), 1);
//            if (addresses.size() > 0) {
//                System.out.println(addresses.get(0).getLocality());
//                cityName = addresses.get(0).getLocality();
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
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


    public void saveData(){
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(LOGGED_IN,true);
        editor.putString(USERNAME,username.getText().toString().trim());
        editor.putString(EMAIL,email.getText().toString().trim());
        editor.putString(FIRST_NAME,firstname.getText().toString().trim());
        editor.putString(LAST_NAME,lastname.getText().toString().trim());
        editor.apply();
        Toast.makeText(this, "SAVED", Toast.LENGTH_SHORT).show();
    }
}