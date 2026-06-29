package com.example.appdoctruyen.data.api;

//public class PostDto {
//    public int id;
//    public String userId;
//    public String title;
//    public String content;
//    public String imageUrl;
//    public String mangaId;
//    public String createdAt;
//    public String updatedAt;
//}

import com.google.gson.annotations.SerializedName;

public class PostDto {
    @SerializedName("id")
    public int id;

    @SerializedName("userId")
    public String userId;

    @SerializedName("display_name")
    public String displayName;

    @SerializedName("avatar_url")
    public String avatarUrl;

    @SerializedName("content")
    public String content;
    @SerializedName("image_url")
    public String imageUrl;

    @SerializedName("like_count")
    public int likeCount;

    @SerializedName("created_at")
    public String createdAt;
}