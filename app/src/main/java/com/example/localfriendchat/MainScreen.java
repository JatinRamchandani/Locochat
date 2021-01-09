package com.example.localfriendchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.localfriendchat.Retrofit.User;
import com.example.localfriendchat.Retrofit.UserApi;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainScreen extends AppCompatActivity implements MainPageAdapter.ListItemClickListener , LocationListener  {

    
    private Toolbar toolbar;
    private FloatingActionButton searchpeople;
    private RecyclerView recyclerView;

    public JsonObject newlocation=new JsonObject();;
    private double latitude;
    private double longitude;

    private static final String TAG="THIS IS TH ERROR";


    public static final String SHARED_PREFS="shared_prefs";
    public static final String LOGGED_IN="logged_in";
    public static final String USERNAME="username";
    public static final String EMAIL="email";
    public static final String FIRST_NAME="first_name";
    public static final String LAST_NAME="last_name";
    public static final String CURRENT_USER_CHAT="current_user_chat";
    public static final String SOCKET_ID="socket_id";

    private String mUsername;
    private static String useremail;
    private Socket mSocket;

    private Boolean isConnected = true;

    private double mylat;
    private double mlong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);



        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        useremail=sharedPreferences.getString(EMAIL,"");
        
        toolbar=findViewById(R.id.mstoolbar);
        setSupportActionBar(toolbar);

        recyclerView=findViewById(R.id.recyclerresults);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        searchpeople=findViewById(R.id.searchpeople);
        searchpeople.setImageResource(R.drawable.magni_glass);
        searchpeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getallusers();
            }
        });



        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.connect();




        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MainScreen();
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

    private void getallusers() {
        Call<List<User>> call= UserApi.getUserService().allusers();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {


                List<User> users=response.body();
                List<User> requsers=new ArrayList<>();

                SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

                for (User user : users){
                    if(user.getEmail().equals(sharedPreferences.getString(EMAIL, ""))){
                        mylat=Double.parseDouble(user.getLatitude());
                        mlong=Double.parseDouble(user.getLongitude());
                    }
                }

                for(User user:users){
                    double lati= Double.parseDouble(user.getLatitude());
                    double longi=Double.parseDouble(user.getLongitude());

                    Log.e("DIST", String.valueOf(distFrom(mylat, mlong, lati, longi)));
                    if(distFrom(mylat, mlong, lati, longi)!=0.00000000000000 && distFrom(mylat, mlong, lati, longi)<=70.000000000000 ||distFrom(mylat, mlong, lati, longi)!=0.00000000000000 && lati-mylat <= 0.0001 && longi-mlong<=0.0001 ){
                         requsers.add(user);
                    }
                }


                recyclerView.setAdapter(new MainPageAdapter(MainScreen.this,requsers));
                Toast.makeText(getApplicationContext(), "Fetched all users", Toast.LENGTH_SHORT).show();
                Log.e("USERRRR", String.valueOf(users));

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Fetching failed", Toast.LENGTH_SHORT).show();
                Log.e("EEEEEEEEEEEEEEEEEEEEEE",t.toString());

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_page_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        switch(id){
            case R.id.profile:
                startActivity(new Intent(getApplicationContext(),Profile.class));
                break;
            case R.id.serverchats:
//                attemptLogin();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }
        return true;
    }

    private void attemptLogin() {
        mUsername = useremail;
        mSocket.emit("add user", mUsername);
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            String socketid;
            try {
                numUsers = data.getInt("numUsers");
                socketid = data.getString("socketID");

                SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(SOCKET_ID,socketid);
                editor.apply();

                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("email",sharedPreferences.getString(EMAIL,""));
                jsonObject.addProperty("socket_id",socketid);

                Call<JsonObject> call= UserApi.getUserService().socketupdate(jsonObject);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.e("UPDATED BHAIYAA JI", String.valueOf(response.body()));

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                        Toast.makeText(getApplicationContext(), "Location update failed", Toast.LENGTH_SHORT).show();
                        Log.e("EEEEEEEEEEE NOT UPDATED",t.toString());

                    }
                });

                Log.e("SOCKET_IDDDD",socketid);

                Log.e("SOCKET_IDDDD",socketid);
            } catch (JSONException e) {
                return;
            }



//            Intent intent = new Intent();
//            intent.putExtra("username", mUsername);
//            intent.putExtra("numUsers", numUsers);
//            setResult(RESULT_OK, intent);
//            finish();
        }
    };


    @Override
    public void onListItemClick(String username,String socket_id) {
//        attemptLogin();
//        mSocket.on("login", onLogin);

        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(CURRENT_USER_CHAT,socket_id);
        editor.apply();

        Intent intent=new Intent(getApplicationContext(),PrivateChats.class);
        intent.putExtra("chattername",username);
        startActivity(intent);
    }

//    private double distance(double lat1, double lon1, double lat2, double lon2) {
//        double theta = lon1 - lon2;
//        double dist = Math.sin(deg2rad(lat1))
//                * Math.sin(deg2rad(lat2))
//                + Math.cos(deg2rad(lat1))
//                * Math.cos(deg2rad(lat2))
//                * Math.cos(deg2rad(theta));
//        dist = Math.acos(dist);
//        dist = rad2deg(dist);
//        dist = dist * 60 * 1.1515;
//        return (dist)*1000 ;
//    }
//
//    private double deg2rad(double deg) {
//        return (deg * Math.PI / 180.0);
//    }
//
//    private double rad2deg(double rad) {
//        return (rad * 180.0 / Math.PI);
//    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dLng / 2),2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double dist = (double) (earthRadius * c);

        return dist;
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        if(null!=mUsername)
                            mSocket.emit("add user", mUsername);
                        Toast.makeText(getApplicationContext(),
                                R.string.connect, Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                    Toast.makeText(getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };





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