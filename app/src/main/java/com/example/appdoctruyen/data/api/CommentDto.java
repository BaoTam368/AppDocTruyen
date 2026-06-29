package com.example.appdoctruyen.data.api;

import com.google.gson.annotations.SerializedName;

public class CommentDto {
    @SerializedName("id")
    public int id;

    @SerializedName(value = "userId", alternate = {"user_id"})
    public String userId;

    @SerializedName(value = "mangaId", alternate = {"manga_id"})
    public String mangaId;

    @SerializedName(value = "chapterId", alternate = {"chapter_id"})
    public String chapterId;

    @SerializedName("content")
    public String content;

    @SerializedName(value = "displayName", alternate = {"display_name", "username"})
    public String displayName;

    @SerializedName(value = "avatarUrl", alternate = {"avatar_url"})
    public String avatarUrl;

    @SerializedName(value = "createdAt", alternate = {"created_at"})
    public String createdAt;

    @SerializedName(value = "updatedAt", alternate = {"updated_at"})
    public String updatedAt;
}