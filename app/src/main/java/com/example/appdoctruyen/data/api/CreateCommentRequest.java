package com.example.appdoctruyen.data.api;

public class CreateCommentRequest {
    public String userId;
    public String mangaId;
    public String chapterId;
    public String content;

    public CreateCommentRequest(String userId, String mangaId, String chapterId, String content) {
        this.userId = userId;
        this.mangaId = mangaId;
        this.chapterId = chapterId;
        this.content = content;
    }
}
