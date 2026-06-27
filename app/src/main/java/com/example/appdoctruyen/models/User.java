package com.example.appdoctruyen.models;

public class User {

    private int id;
    private String userId;
    private String displayName;
    private String email;
    private String avatarUrl;
    private String createdAt;
    private String updatedAt;

    public User(int id, String userId, String displayName, String email,
                String avatarUrl, String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }

    public String getUserId() { return userId; }

    public String getDisplayName() { return displayName; }

    public String getEmail() { return email; }

    public String getAvatarUrl() { return avatarUrl; }

    public String getCreatedAt() { return createdAt; }

    public String getUpdatedAt() { return updatedAt; }
}