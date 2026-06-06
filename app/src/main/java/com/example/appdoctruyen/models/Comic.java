package com.example.appdoctruyen.models;

public class Comic {

    private int id;
    private String title;
    private int coverImageResId;     // Resource ID ảnh bìa trong drawable
    private String coverUrl;          // URL ảnh bìa (dùng khi load từ API)
    private String latestChapter;     // Chapter mới nhất của truyện
    private String lastReadChapter;   // Chapter đã đọc gần nhất
    private String author;
    private String description;
    private boolean isFollowed;       // Đang theo dõi
    private boolean isDownloaded;     // Đã tải về

    public Comic(int id, String title, int coverImageResId, String coverUrl,
                 String latestChapter, String lastReadChapter, String author,
                 String description, boolean isFollowed, boolean isDownloaded) {
        this.id = id;
        this.title = title;
        this.coverImageResId = coverImageResId;
        this.coverUrl = coverUrl;
        this.latestChapter = latestChapter;
        this.lastReadChapter = lastReadChapter;
        this.author = author;
        this.description = description;
        this.isFollowed = isFollowed;
        this.isDownloaded = isDownloaded;
    }

    public Comic(int id, String title, int coverImageResId, String latestChapter) {
        this.id = id;
        this.title = title;
        this.coverImageResId = coverImageResId;
        this.latestChapter = latestChapter;
        this.isFollowed = false;
        this.isDownloaded = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCoverImageResId() { return coverImageResId; }
    public void setCoverImageResId(int coverImageResId) { this.coverImageResId = coverImageResId; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getLatestChapter() { return latestChapter; }
    public void setLatestChapter(String latestChapter) { this.latestChapter = latestChapter; }

    public String getLastReadChapter() { return lastReadChapter; }
    public void setLastReadChapter(String lastReadChapter) { this.lastReadChapter = lastReadChapter; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isFollowed() { return isFollowed; }
    public void setFollowed(boolean followed) { isFollowed = followed; }

    public boolean isDownloaded() { return isDownloaded; }
    public void setDownloaded(boolean downloaded) { isDownloaded = downloaded; }
}
