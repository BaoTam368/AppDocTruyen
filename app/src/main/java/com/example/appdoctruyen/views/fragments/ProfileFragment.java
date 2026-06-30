package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.views.activities.LoginActivity;
import com.example.appdoctruyen.views.activities.UserDetailsActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";

    private LinearLayout btn_logout, layoutLoginRequired, layoutProfileContent;
    private LinearLayout btn_terms;
    private SwitchCompat switchDarkMode;
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
        btn_terms = view.findViewById(R.id.btn_terms);
        btnProfileLogin = view.findViewById(R.id.btnProfileLogin);
        layoutLoginRequired = view.findViewById(R.id.profileLoginRequired);
        layoutProfileContent = view.findViewById(R.id.profileContent);
        iv_user_avatar = view.findViewById(R.id.iv_user_avatar);
        tv_username = view.findViewById(R.id.tv_username);
        tv_user_title = view.findViewById(R.id.tv_user_title);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        authManager = new AuthManager(requireContext());

        setupDarkModeSwitch();
        setupClickListeners();
        updateAuthState();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAuthState();
    }

    // ═══════════════════════════════════════════════════
    // Dark Mode — SharedPreferences + AppCompatDelegate
    // ═══════════════════════════════════════════════════

    private void setupDarkModeSwitch() {
        if (switchDarkMode == null) return;

        // Đọc trạng thái từ SharedPreferences và sync Switch
        boolean isDarkMode = getDarkModePreference();
        switchDarkMode.setChecked(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveDarkModePreference(isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }

    private boolean getDarkModePreference() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    private void saveDarkModePreference(boolean enabled) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, 0);
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    /**
     * Gọi phương thức này từ Application.onCreate() hoặc MainActivity.onCreate()
     * để áp dụng Dark Mode preference ngay khi app khởi động.
     */
    public static void applyDarkModeFromPreferences(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    // ═══════════════════════════════════════════════════
    // Click Listeners
    // ═══════════════════════════════════════════════════

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

        if (btn_terms != null) {
            btn_terms.setOnClickListener(v -> showTermsOfUseDialog());
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

    // ═══════════════════════════════════════════════════
    // Terms of Use Dialog
    // ═══════════════════════════════════════════════════

    private void showTermsOfUseDialog() {
        // Tạo ScrollView chứa nội dung dài
        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setPadding(48, 32, 48, 16);

        TextView tvContent = new TextView(requireContext());
        tvContent.setText(getString(R.string.terms_of_use_content));
        tvContent.setTextSize(14f);
        tvContent.setLineSpacing(4f, 1.15f);
        scrollView.addView(tvContent);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.terms_dialog_title)
                .setView(scrollView)
                .setPositiveButton(R.string.btn_close, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    // ═══════════════════════════════════════════════════
    // Auth State Management
    // ═══════════════════════════════════════════════════

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
                tv_username.setText(email.substring(0, email.indexOf("@")));
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
            FirebaseFirestore.getInstance()
                    .collection("users").document(user.getUid())
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (error != null) {
                            android.util.Log.e("ProfileFragment", "Cant load data", error);
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String firestoreName = documentSnapshot.getString("username");
                            if (firestoreName != null && !firestoreName.isEmpty()) {
                                tv_username.setText(firestoreName);
                            }
                        }
                    });
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