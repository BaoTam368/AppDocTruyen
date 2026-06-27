package com.example.appdoctruyen.models;

public class Comment {
    private String username, time, content, avatarUrl;
//            comicName;

    public Comment(String username, String time, String content, String avatarUrl) {
        this.username = username;
        this.time = time;
        this.content = content;
        this.avatarUrl = avatarUrl;
//        this.comicName = comicName;
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

//    public String getComicName() {
//        return comicName;
//    }
//
//    public void setComicName(String comicName) {
//        this.comicName = comicName;
//    }
}
