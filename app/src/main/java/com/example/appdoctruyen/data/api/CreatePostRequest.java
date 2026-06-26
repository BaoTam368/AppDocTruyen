package com.example.appdoctruyen.data.api;

public class CreatePostRequest {
    public String userId;
    public String title;
    public String content;
    public String imageUrl;
    public String mangaId;

    public CreatePostRequest(String userId, String title, String content, String imageUrl, String mangaId) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.mangaId = mangaId;
    }
}
