package com.example.appdoctruyen.data.api;

public class CommentResponse extends ApiResponse<CommentDto> {
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public CommentDto getData() {
        return data;
    }
}
