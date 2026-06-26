package com.example.appdoctruyen.data.api;

public class PostResponse extends ApiResponse<PostDto> {
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public PostDto getData() {
        return data;
    }
}
