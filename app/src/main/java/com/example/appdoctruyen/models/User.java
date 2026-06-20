package com.example.appdoctruyen.models;

public class User {
    private int rank;
    private String name;
    private int avatar;

    public User(int rank, String name, int avatar) {
        this.rank = rank;
        this.name = name;
        this.avatar = avatar;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public int getAvatar() {
        return avatar;
    }
}
