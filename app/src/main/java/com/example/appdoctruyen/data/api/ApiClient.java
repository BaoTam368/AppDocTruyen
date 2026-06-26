package com.example.appdoctruyen.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // CẤU HÌNH API URL TÙY THEO MÔI TRƯỜNG:
    //
    // 1. Chạy trên Android Emulator (cùng máy với backend):
    //    Sử dụng: "http://10.0.2.2:3000/api/"
    //
    // 2. Chạy trên thiết bị thật (cùng mạng WiFi với backend):
    //    Sử dụng IP LAN của máy chạy backend, ví dụ: "http://192.168.1.100:3000/api/"
    //    Để tìm IP LAN: Mở terminal và chạy 'ipconfig' (Windows) hoặc 'ifconfig' (Mac/Linux)
    //
    // 3. Chạy trên máy khác (khác máy với backend):
    //    Sử dụng IP LAN của máy chạy backend, ví dụ: "http://192.168.1.100:3000/api/"
    //
    // 4. Backend đã deploy lên server:
    //    Sử dụng domain/IP của server, ví dụ: "http://your-server.com/api/"
    //
    // Đảm bảo backend Node.js đang chạy và port 3000 đã được mở trong firewall

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