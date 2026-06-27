package com.example.appdoctruyen.data.api;

public class EmptyResponse extends ApiResponse<Object> {
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
