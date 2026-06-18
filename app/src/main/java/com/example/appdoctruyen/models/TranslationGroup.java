package com.example.appdoctruyen.models;
public class TranslationGroup {

    private int id;
    private String name;
    private String description;
    private int avatarResId;
    private String avatarUrl;
    private int comicCount;
    private int memberCount;
    private int followerCount;
    private int rank;
    private String createdDate;

    public TranslationGroup(int id, String name, String description, int avatarResId,
                            String avatarUrl, int comicCount, int memberCount,
                            int followerCount, int rank, String createdDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.avatarResId = avatarResId;
        this.avatarUrl = avatarUrl;
        this.comicCount = comicCount;
        this.memberCount = memberCount;
        this.followerCount = followerCount;
        this.rank = rank;
        this.createdDate = createdDate;
    }

    public TranslationGroup(int id, String name, int avatarResId,
                            int comicCount, int memberCount) {
        this.id = id;
        this.name = name;
        this.avatarResId = avatarResId;
        this.comicCount = comicCount;
        this.memberCount = memberCount;
        this.rank = 0;
    }

    public TranslationGroup(int id, String name, String description, int avatarResId,
                            int comicCount, int memberCount, int followerCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.avatarResId = avatarResId;
        this.comicCount = comicCount;
        this.memberCount = memberCount;
        this.followerCount = followerCount;
        this.rank = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAvatarResId() { return avatarResId; }
    public void setAvatarResId(int avatarResId) { this.avatarResId = avatarResId; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public int getComicCount() { return comicCount; }
    public void setComicCount(int comicCount) { this.comicCount = comicCount; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public int getFollowerCount() { return followerCount; }
    public void setFollowerCount(int followerCount) { this.followerCount = followerCount; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}