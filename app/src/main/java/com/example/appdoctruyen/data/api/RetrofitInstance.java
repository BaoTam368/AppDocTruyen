package com.example.appdoctruyen.data.api;

import retrofit2.Retrofit;

public class RetrofitInstance {

    private RetrofitInstance() {
    }

    public static Retrofit getRetrofit() {
        return ApiClient.getRetrofit();
    }
}
