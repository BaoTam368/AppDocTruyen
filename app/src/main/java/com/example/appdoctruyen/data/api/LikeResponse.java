package com.example.appdoctruyen.data.api;

import com.google.gson.annotations.SerializedName;

public class LikeResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("liked")
    private boolean liked;

    @SerializedName("likeCount")
    private int likeCount;

    // Hàm getter để kiểm tra trạng thái thành công
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
