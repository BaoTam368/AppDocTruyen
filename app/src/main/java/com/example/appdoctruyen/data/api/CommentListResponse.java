package com.example.appdoctruyen.data.api;

import java.util.List;

public class CommentListResponse extends ApiResponse<List<CommentDto>> {
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<CommentDto> getData() {
        return data;
    }
}
