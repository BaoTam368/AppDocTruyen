package com.example.appdoctruyen.data.api;

public class UserResponse extends ApiResponse<UserDto> {
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public UserDto getData() {
        return data;
    }
}
