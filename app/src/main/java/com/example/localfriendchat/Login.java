package com.example.localfriendchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localfriendchat.Retrofit.User;
import com.example.localfriendchat.Retrofit.UserApi;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {


    private com.google.android.material.textfield.TextInputEditText email,pass;
    private Button login;
    private TextView createAcc;

    private JsonObject user;

    private Boolean isLogged;

    public static final String SHARED_PREFS="shared_prefs";
    public static final String LOGGED_IN="logged_in";
    public static final String USERNAME="username";
    public static final String EMAIL="email";
    public static final String FIRST_NAME="first_name";
    public static final String LAST_NAME="last_name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email=findViewById(R.id.log_email);
        pass=findViewById(R.id.log_pass);
        login=findViewById(R.id.login);
        createAcc=findViewById(R.id.createAcc);

        if(isLogin()){
            startActivity(new Intent(getApplicationContext(),MainScreen.class));
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

    }

    private void login() {
        user=new JsonObject();
        user.addProperty("email",email.getText().toString().trim());
        user.addProperty("password",pass.getText().toString().trim());


        Call<List<User>> call= UserApi.getUserService().userlogin(user);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                List<User> user=response.body();

                if(!user.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Login Successfull", Toast.LENGTH_SHORT).show();
                    Log.e("USERRRR", String.valueOf(user.get(0).getUsername()));


                    SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean(LOGGED_IN,true);
                    editor.putString(USERNAME,user.get(0).getUsername());
                    editor.putString(EMAIL,user.get(0).getEmail());
                    editor.putString(FIRST_NAME,user.get(0).getFirstName());
                    editor.putString(LAST_NAME,user.get(0).getLastName());
                    editor.apply();

                    startActivity(new Intent(getApplicationContext(),MainScreen.class));
                }else{
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                Log.e("EEEEEEEEEEEEEEEEEEEEEE",t.toString());

            }
        });

    }



    public boolean isLogin(){
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        isLogged=sharedPreferences.getBoolean(LOGGED_IN,false);

        if(isLogged){
            return true;
        }
        else{
            return false;
        }
    }
}