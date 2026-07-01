package com.example.appdoctruyen.data.firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appdoctruyen.data.api.ApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.HashMap;

public class AuthManager {
    private static final String TAG = "AuthDebug";
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
        Log.d(TAG, "AuthManager.login: attempting email login for " + email);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Log.d(TAG, "AuthManager.login: signIn SUCCESS, uid=" + user.getUid());
                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get().addOnSuccessListener(document -> {
                    String username = document.getString("username");
                    syncUserToBackend(user, username);
                    callback.onSuccess(user);
                }).addOnFailureListener(e -> {
                    Log.w(TAG, "AuthManager.login: Firestore user doc fetch failed, continuing anyway: " + e.getMessage());
                    syncUserToBackend(user, null);
                    callback.onSuccess(user);
                });
            } else {
                String errMsg = task.getException() != null ? task.getException().getLocalizedMessage() : "Login failed";
                Log.e(TAG, "AuthManager.login: signIn FAILED: " + errMsg, task.getException());
                callback.onFailure(errMsg);
            }
        });
    }

    public void register(String email, String password, String username, AuthCallback callback) {
        Log.d(TAG, "AuthManager.register: attempting registration for " + email + ", username=" + username);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Log.d(TAG, "AuthManager.register: SUCCESS, uid=" + user.getUid());

                // Lưu username vào Firebase Auth profile (displayName)
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build();
                user.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                    if (profileTask.isSuccessful()) {
                        Log.d(TAG, "AuthManager.register: Firebase Auth profile updated with username=" + username);
                    } else {
                        Log.w(TAG, "AuthManager.register: Failed to update Firebase Auth profile", profileTask.getException());
                    }
                });

                // Lưu username vào Firestore collection 'users'
                HashMap<String, Object> userDoc = new HashMap<>();
                userDoc.put("username", username);
                userDoc.put("email", email);
                userDoc.put("uid", user.getUid());
                FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                        .set(userDoc)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "AuthManager.register: Firestore user doc saved with username=" + username))
                        .addOnFailureListener(e -> Log.w(TAG, "AuthManager.register: Failed to save Firestore user doc", e));

                // Sync về backend Node.js với username thực tế
                syncUserToBackend(user, username);
                callback.onSuccess(user);
            } else {
                String errMsg = task.getException() != null ? task.getException().getLocalizedMessage() : "Registration failed";
                Log.e(TAG, "AuthManager.register: FAILED: " + errMsg, task.getException());
                callback.onFailure(errMsg);
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
        Log.d(TAG, "AuthManager.loginWithGoogle: attempting signInWithCredential...");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Log.d(TAG, "AuthManager.loginWithGoogle: signInWithCredential SUCCESS, uid=" + user.getUid());
                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get().addOnSuccessListener(document -> {
                    String username = document.getString("username");
                    syncUserToBackend(user, username);
                    callback.onSuccess(user);
                }).addOnFailureListener(e -> {
                    Log.w(TAG, "AuthManager.loginWithGoogle: Firestore user doc fetch failed, continuing anyway: " + e.getMessage());
                    syncUserToBackend(user, null);
                    callback.onSuccess(user);
                });
            } else {
                String errMsg = task.getException() != null ? task.getException().getLocalizedMessage() : "Google login failed";
                Log.e(TAG, "AuthManager.loginWithGoogle: signInWithCredential FAILED: " + errMsg, task.getException());
                callback.onFailure(errMsg);
            }
        });
    }

    public void loginWithFacebook(String accessToken, AuthCallback callback) {
        Log.d(TAG, "AuthManager.loginWithFacebook: attempting signInWithCredential...");
        com.google.firebase.auth.AuthCredential credential = FacebookAuthProvider.getCredential(accessToken);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Log.d(TAG, "AuthManager.loginWithFacebook: signInWithCredential SUCCESS, uid=" + user.getUid());
                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get().addOnSuccessListener(document -> {
                    String username = document.getString("username");
                    syncUserToBackend(user, username);
                    callback.onSuccess(user);
                }).addOnFailureListener(e -> {
                    Log.w(TAG, "AuthManager.loginWithFacebook: Firestore user doc fetch failed, continuing anyway: " + e.getMessage());
                    syncUserToBackend(user, null);
                    callback.onSuccess(user);
                });
            } else {
                String errMsg = task.getException() != null ? task.getException().getLocalizedMessage() : "Facebook login failed";
                Log.e(TAG, "AuthManager.loginWithFacebook: signInWithCredential FAILED: " + errMsg, task.getException());
                callback.onFailure(errMsg);
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

        String url = ApiClient.BASE_URL + "users/sync";

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

        String url = ApiClient.BASE_URL + "users/" + user.getUid();
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
