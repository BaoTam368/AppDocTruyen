package com.example.appdoctruyen.data.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class AuthManager {
    private FirebaseAuth mAuth;
    public AuthManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getLocalizedMessage() : "Đăng nhập thất bại");
                    }
                });
    }

    public void register(String email, String password, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getLocalizedMessage() : "Đăng ký thất bại");
                    }
                });
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public void logout() {
        mAuth.signOut();
    }
}
