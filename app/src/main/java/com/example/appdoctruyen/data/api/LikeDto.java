package com.example.appdoctruyen.data.api;

import com.google.gson.annotations.SerializedName;

public class LikeDto {
    @SerializedName("userId")
    private String userId;

    public LikeDto(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
