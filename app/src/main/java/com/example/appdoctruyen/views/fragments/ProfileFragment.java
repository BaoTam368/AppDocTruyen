package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.views.activities.LoginActivity;
import com.example.appdoctruyen.views.activities.RechargeActivity;
import com.example.appdoctruyen.views.activities.UserDetailsActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    private LinearLayout btn_logout, btn_topup, layoutLoginRequired, layoutProfileContent;
    private Button btnProfileLogin;
    private ShapeableImageView iv_user_avatar;
    private TextView tv_username, tv_user_title;
    private AuthManager authManager;
    private boolean profileLoaded = false;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btn_logout = view.findViewById(R.id.btn_logout);
        btn_topup = view.findViewById(R.id.btn_topup);
        btnProfileLogin = view.findViewById(R.id.btnProfileLogin);
        layoutLoginRequired = view.findViewById(R.id.profileLoginRequired);
        layoutProfileContent = view.findViewById(R.id.profileContent);
        iv_user_avatar = view.findViewById(R.id.iv_user_avatar);
        tv_username = view.findViewById(R.id.tv_username);
        tv_user_title = view.findViewById(R.id.tv_user_title);
        authManager = new AuthManager(requireContext());

        setupClickListeners();
        updateAuthState();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAuthState();
    }

    private void setupClickListeners() {
        if (btnProfileLogin != null) {
            btnProfileLogin.setOnClickListener(v -> startActivity(new Intent(requireContext(), LoginActivity.class)));
        }

        if (btn_logout != null) {
            btn_logout.setOnClickListener(v -> {
                authManager.logout();
                profileLoaded = false;
                showLoginRequiredState();
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();
            });
        }

        if (btn_topup != null) {
            btn_topup.setOnClickListener(v -> {
                if (!isLoggedIn()) {
                    showLoginRequiredState();
                    Toast.makeText(requireContext(), "Please log in to use this feature.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(requireContext(), RechargeActivity.class);
                startActivity(intent);
            });
        }

        if (iv_user_avatar != null) {
            iv_user_avatar.setOnClickListener(v -> {
                if (!isLoggedIn()) {
                    showLoginRequiredState();
                    Toast.makeText(requireContext(), "Please log in to view your profile.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(requireContext(), UserDetailsActivity.class);
                startActivity(intent);
            });
        }
    }

    private void updateAuthState() {
        if (!isAdded()) return;
        if (authManager == null) {
            authManager = new AuthManager(requireContext());
        }

        if (!authManager.isLoggedIn()) {
            profileLoaded = false;
            showLoginRequiredState();
            return;
        }

        showProfileContent();
        if (!profileLoaded) {
            loadUserInfo();
            loadUserProfile();
            profileLoaded = true;
        }
    }

    private boolean isLoggedIn() {
        return authManager != null && authManager.isLoggedIn();
    }

    private void showLoginRequiredState() {
        if (layoutLoginRequired != null) layoutLoginRequired.setVisibility(View.VISIBLE);
        if (layoutProfileContent != null) layoutProfileContent.setVisibility(View.GONE);
    }

    private void showProfileContent() {
        if (layoutLoginRequired != null) layoutLoginRequired.setVisibility(View.GONE);
        if (layoutProfileContent != null) layoutProfileContent.setVisibility(View.VISIBLE);
    }

    private void loadUserInfo() {
        FirebaseUser user = authManager.getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            String email = user.getEmail();

            if (displayName != null && !displayName.isEmpty()) {
                tv_username.setText(displayName);
            } else if (email != null && !email.isEmpty()) {
                tv_username.setText(email);
            } else {
                tv_username.setText("User");
            }

            tv_user_title.setText("Member");

            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .circleCrop()
                        .placeholder(R.drawable.placeholder_comic)
                        .error(R.drawable.placeholder_comic)
                        .into(iv_user_avatar);
            }
        }
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = authManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (!isAdded()) return;
                    if (error != null) {
                        Log.e("ProfileFragment", "Unable to load profile data", error);
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("username");
                        if (name != null && !name.isEmpty()) {
                            tv_username.setText(name);
                        }
                    }
                });
    }
}