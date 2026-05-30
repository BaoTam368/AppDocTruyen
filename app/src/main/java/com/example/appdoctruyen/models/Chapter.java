package com.example.appdoctruyen.models;

public class Chapter {
    private String name;
    private String date;
    private boolean isFree; // true là miễn phí, false là khóa

    public Chapter(String name, String date, boolean isFree) {
        this.name = name;
        this.date = date;
        this.isFree = isFree;
    }

    public String getName() { return name; }
    public String getDate() { return date; }
    public boolean isFree() { return isFree; }
}