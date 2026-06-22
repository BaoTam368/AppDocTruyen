package com.example.appdoctruyen.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Nếu chạy bằng điện thoại thật thì đổi thành IP LAN của máy chạy Node.js
    public static final String BASE_URL = "http://10.0.2.2:3000/api/";

    private static Retrofit retrofit;

    private ApiClient() {
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static MangaApiService getMangaApiService() {
        return getRetrofit().create(MangaApiService.class);
    }
}