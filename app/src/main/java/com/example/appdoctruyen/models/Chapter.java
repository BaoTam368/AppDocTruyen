package com.example.appdoctruyen.models;

public class Chapter {
    private String chapterId;
    private String name;
    private String date;
    private boolean isFree; // true là miễn phí, false là khóa
    private String chapterNumber;

    public Chapter(String name, String date, boolean isFree) {
        this(null, name, date, isFree, null);
    }

    public Chapter(String chapterId, String name, String date, boolean isFree) {
        this(chapterId, name, date, isFree, null);
    }

    public Chapter(String chapterId, String name, String date, boolean isFree, String chapterNumber) {
        this.chapterId = chapterId;
        this.name = name;
        this.date = date;
        this.isFree = isFree;
        this.chapterNumber = chapterNumber;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public boolean isFree() {
        return isFree;
    }

    public String getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(String chapterNumber) {
        this.chapterNumber = chapterNumber;
    }
}