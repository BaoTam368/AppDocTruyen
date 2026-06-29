package com.example.appdoctruyen.models;

public class Cover {
    private String coverId;
    private String mangaId;
    private String fileName;
    private String coverUrl;
    private String thumbnailUrl;
    private String volume;
    private String locale;
    private String createdAt;
    private String updatedAt;

    public Cover(String coverId, String mangaId, String fileName, String coverUrl, String thumbnailUrl,
                 String volume, String locale, String createdAt, String updatedAt) {
        this.coverId = coverId;
        this.mangaId = mangaId;
        this.fileName = fileName;
        this.coverUrl = coverUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.volume = volume;
        this.locale = locale;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getCoverId() {
        return coverId;
    }

    public String getMangaId() {
        return mangaId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getVolume() {
        return volume;
    }

    public String getLocale() {
        return locale;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}