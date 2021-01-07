package com.example.localfriendchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.localfriendchat.Retrofit.User;
import com.example.localfriendchat.Retrofit.UserApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainScreen extends AppCompatActivity implements MainPageAdapter.ListItemClickListener  {

    
    private Toolbar toolbar;
    private FloatingActionButton searchpeople;
    private RecyclerView recyclerView;


    public static final String SHARED_PREFS="shared_prefs";
    public static final String LOGGED_IN="logged_in";
    public static final String USERNAME="username";
    public static final String EMAIL="email";
    public static final String FIRST_NAME="first_name";
    public static final String LAST_NAME="last_name";


    private double mylat;
    private double mlong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        
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
                    if(distFrom(mylat, mlong, lati, longi)!=0.00000000000000 && distFrom(mylat, mlong, lati, longi)<=5.000000000000 ||distFrom(mylat, mlong, lati, longi)!=0.00000000000000 && lati-mylat <= 0.0001 && longi-mlong<=0.0001 ){
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
            case R.id.pchats:
                startActivity(new Intent(getApplicationContext(),PrivateChats.class));
                break;
            case R.id.serverchats:
                startActivity(new Intent(getApplicationContext(),ServerChats.class));
                break;
        }
        return true;
    }


    @Override
    public void onListItemClick(String username) {

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist)*1000 ;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

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
}