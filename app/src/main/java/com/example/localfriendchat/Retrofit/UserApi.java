package com.example.localfriendchat.Retrofit;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public class UserApi {
    public static final String BASE_URL="http://192.168.43.183:8000/api/users/";

    public static UserService userService=null;

    public static UserService getUserService(){
        if(userService==null){


            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS).build();

            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            userService=retrofit.create(UserService.class);
        }
        return userService;
    }

    public interface UserService{
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        @POST("user/")
        Call<JsonObject> userregister(@Body JsonObject jsonObject);

        @Headers({"Content-Type: application/json;charset=UTF-8"})
        @POST("login/")
        Call<List<User>> userlogin(@Body JsonObject jsonObject);

        @Headers({"Content-Type: application/json;charset=UTF-8"})
        @POST("locationupdate/")
        Call<JsonObject> locationupdate(@Body JsonObject jsonObject);

        @Headers({"Content-Type: application/json;charset=UTF-8"})
        @GET("getusers/")
        Call<List<User>> allusers();

        @Headers({"Content-Type: application/json;charset=UTF-8"})
        @POST("socketupdate/")
        Call<JsonObject> socketupdate(@Body JsonObject jsonObject);
    }
}
