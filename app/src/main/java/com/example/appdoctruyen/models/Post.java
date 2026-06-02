package com.example.appdoctruyen.models;

import java.io.Serializable;

public class Post implements Serializable {
    private String username, time, caption, avatarUrl, imageUrl;
    private int numLikes, numComments;

    public Post(String username, String time, String caption, String avatarUrl, String imageUrl, int numLikes, int numComments) {
        this.username = username;
        this.time = time;
        this.caption = caption;
        this.avatarUrl = avatarUrl;
        this.imageUrl = imageUrl;
        this.numLikes = numLikes;
        this.numComments = numComments;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }
}
