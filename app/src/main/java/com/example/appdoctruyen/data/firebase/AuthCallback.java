package com.example.appdoctruyen.data.firebase;

import com.google.firebase.auth.FirebaseUser;

public interface AuthCallback {
    void onSuccess(FirebaseUser user);
    void onFailure(String errorMessage);
}
