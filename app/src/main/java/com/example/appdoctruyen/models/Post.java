package com.example.appdoctruyen.models;

import java.io.Serializable;

public class Post implements Serializable {
//    private String username, time, caption, avatarUrl, imageUrl;
//    private int numLikes;
//            numComments;

    private int id;
    private String userId;
    private String displayName;
    private String avatarUrl;

    private String content;
    private String imageUrl;

    private int likeCount;
    private String createdAt;

    public Post(int id, String userId, String displayName, String avatarUrl, String content, String imageUrl, int likeCount, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
