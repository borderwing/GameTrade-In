package com.example.ye.gametrade_in.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lykav on 9/10/2017.
 */

public class GameTradeApi {
    public static final String SERVER = "http://10.162.172.25:8080";
    public static final String ENDPOINT = SERVER + "/api/";

    private static Retrofit retrofitAuth = null;
    private static Retrofit retrofitNoAuth = null;

    private static OkHttpClient buildClient(String credentials){
        return new OkHttpClient
                .Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new BasicAuthInterceptor(credentials))
                .build();
    }

    private static OkHttpClient buildClient(String username, String password){
        return new OkHttpClient
                .Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new BasicAuthInterceptor(username, password))
                .build();
    }


    private static OkHttpClient buildClient(){
        return new OkHttpClient
                .Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }

    public static Retrofit getClient(String credentials){
        retrofitAuth = new Retrofit.Builder()
                .client(buildClient(credentials))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ENDPOINT)
                .build();

        return retrofitAuth;
    }

    public static Retrofit getClient(String username, String password){
        retrofitAuth = new Retrofit.Builder()
                .client(buildClient(username, password))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ENDPOINT)
                .build();

        return retrofitAuth;
    }

    public static Retrofit getClient(){
        if(retrofitNoAuth == null){
            retrofitNoAuth = new Retrofit.Builder()
                    .client(buildClient())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(ENDPOINT)
                    .build();
        }
        return retrofitNoAuth;
    }
}
