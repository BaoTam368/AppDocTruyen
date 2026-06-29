package com.example.appdoctruyen.data.firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

public class AuthManager {
    private FirebaseAuth mAuth;
    @SuppressLint("RestrictedApi")
    private Context context;

//    public AuthManager() {
//        mAuth = FirebaseAuth.getInstance();
//    }

    public AuthManager(@SuppressLint("RestrictedApi") Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get().addOnSuccessListener(document -> {
                    String username = document.getString("username");
                    syncUserToBackend(user, username);
                    callback.onSuccess(user);
                }).addOnFailureListener(e -> {
                    syncUserToBackend(user, null);
                    callback.onSuccess(user);
                });
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getLocalizedMessage() : "Login failed");
            }
        });
    }

    public void register(String email, String password, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                syncUserToBackend(user, null);
                callback.onSuccess(user);
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getLocalizedMessage() : "Registration failed");
            }
        });
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public boolean isLoggedIn() {
        return isUserLoggedIn();
    }

    public boolean isGuest() {
        return !isLoggedIn();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public void logout() {
        mAuth.signOut();
    }

    public void loginWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get().addOnSuccessListener(document -> {
                    String username = document.getString("username");
                    syncUserToBackend(user, username);
                    callback.onSuccess(user);
                }).addOnFailureListener(e -> {
                    syncUserToBackend(user, null);
                    callback.onSuccess(user);
                });
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getLocalizedMessage() : "Google login failed");
            }
        });
    }

    public void loginWithFacebook(String accessToken, AuthCallback callback) {
        com.google.firebase.auth.AuthCredential credential = FacebookAuthProvider.getCredential(accessToken);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get().addOnSuccessListener(document -> {
                    String username = document.getString("username");
                    syncUserToBackend(user, username);
                    callback.onSuccess(user);
                }).addOnFailureListener(e -> {
                    syncUserToBackend(user, null);
                    callback.onSuccess(user);
                });
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getLocalizedMessage() : "Facebook login failed");
            }
        });
    }

    public void syncUserToBackend(FirebaseUser user, String firestoreUsername) {
        if (user == null) {
            android.util.Log.e("SYNC_USER", "Hủy đồng bộ: user bị null");
            return;
        }
        if (context == null) {
            android.util.Log.e("SYNC_USER", "Hủy đồng bộ: context bị null");
            return;
        }

        String url = "http://10.0.2.2:3000/api/users/sync";

        JSONObject body = new JSONObject();
        try {
            body.put("userId", user.getUid());
            body.put("email", user.getEmail());

            String displayName;
            if (firestoreUsername != null && !firestoreUsername.isEmpty()) {
                displayName = firestoreUsername;
            } else if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                displayName = user.getDisplayName();
            } else {
                displayName = user.getEmail();
            }

            body.put("displayName", displayName);
            body.put("avatarUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);

            Log.d("SYNC_USER", "UID = " + user.getUid());
            Log.d("SYNC_USER", "Email = " + user.getEmail());
            Log.d("SYNC_USER", "DisplayName = " + user.getDisplayName());
            Log.d("SYNC_USER", "Avatar = " + user.getPhotoUrl());
        } catch (Exception e) {
            android.util.Log.e("SYNC_USER", "Lỗi tạo JSON: " + e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, response -> {
            // success
        }, error -> {
            android.util.Log.e("SYNC_USER", "Đồng bộ thất bại");
        });

        Volley.newRequestQueue(context).add(request);
    }

    //Hiện tại cập nhật mỗi name thôi nha
    public void updateUserProfile(String displayName) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null || context == null) {
            return;
        }

        String url = "http://10.0.2.2:3000/api/users/" + user.getUid();
        JSONObject body = new JSONObject();

        try {
            body.put("displayName", displayName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, body, response -> Log.d("SYNC_PROFILE", "Update thành công"), error -> Log.e("SYNC_PROFILE", "Update thất bại", error));
        Volley.newRequestQueue(context).add(request);
    }
}
