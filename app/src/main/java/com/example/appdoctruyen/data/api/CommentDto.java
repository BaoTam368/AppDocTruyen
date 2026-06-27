package com.example.appdoctruyen.data.api;

import com.google.gson.annotations.SerializedName;

public class CommentDto {
    @SerializedName("id")
    public int id;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("content")
    public String content;

    @SerializedName("display_name")
    public String displayName;

    @SerializedName("avatar_url")
    public String avatarUrl;

    @SerializedName("created_at")
    public String createdAt;
}
