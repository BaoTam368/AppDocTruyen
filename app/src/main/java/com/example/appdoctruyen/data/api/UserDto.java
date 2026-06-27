package com.example.appdoctruyen.data.api;

public class UserDto {
    public int id;
    public String userId;
    public String displayName;
    public String email;
    public String avatarUrl;
    public String createdAt;
    public String updatedAt;

    public UserDto() {
    }

    public UserDto(String userId, String displayName, String email, String avatarUrl) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }
}
