package com.example.appdoctruyen.models;

public class Comment  {
    private int id;
    private String userId;
    private String username;
    private String time;
    private String content;
    private String avatarUrl;

    public Comment(int id, String userId, String username, String time, String content, String avatarUrl) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.time = time;
        this.content = content;
        this.avatarUrl = avatarUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
