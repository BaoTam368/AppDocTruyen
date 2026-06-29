package com.example.appdoctruyen.data.api;

import com.google.gson.annotations.SerializedName;

public class CreateCommentRequest {
    @SerializedName("mangaId")
    public String mangaId;

    @SerializedName("chapterId")
    public String chapterId;

    @SerializedName("userId")
    public String userId;

    @SerializedName("content")
    public String content;

    public CreateCommentRequest(String userId, String content) {
        this(null, null, userId, content);
    }

    public CreateCommentRequest(String mangaId, String chapterId, String userId, String content) {
        this.mangaId = mangaId;
        this.chapterId = chapterId;
        this.userId = userId;
        this.content = content;
    }
}