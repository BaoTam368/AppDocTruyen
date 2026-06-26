package com.example.appdoctruyen.data.api;

import java.util.List;

public class PostListResponse extends ApiResponse<List<PostDto>> {
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<PostDto> getData() {
        return data;
    }
}
