package com.example.appdoctruyen.models;

public class Group {
    private String groupName, time, caption,  avatarUrl;
    private int numLikes, numComments;

    public Group(String groupName, String time, String caption, String avatarUrl, int numLikes, int numComments) {
        this.groupName = groupName;
        this.time = time;
        this.caption = caption;
        this.avatarUrl = avatarUrl;
        this.numLikes = numLikes;
        this.numComments = numComments;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
