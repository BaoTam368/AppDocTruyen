package com.example.appdoctruyen.data.api;

import com.google.gson.annotations.SerializedName;

public class CreateCommentRequest {
    @SerializedName("userId")
    public String userId;

    public String content;

    public CreateCommentRequest(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }
}

