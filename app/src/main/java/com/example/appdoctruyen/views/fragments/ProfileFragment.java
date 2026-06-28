package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.views.activities.LoginActivity;
import com.example.appdoctruyen.views.activities.RechargeActivity;
import com.example.appdoctruyen.views.activities.SearchActivity;
import com.example.appdoctruyen.views.activities.UserDetailsActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    LinearLayout btn_logout, btn_topup;
    ShapeableImageView iv_user_avatar;
    private AuthManager authManager;
    private TextView tv_username;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btn_logout = view.findViewById(R.id.btn_logout);
        btn_topup = view.findViewById(R.id.btn_topup);
        iv_user_avatar = view.findViewById(R.id.iv_user_avatar);
        tv_username = view.findViewById(R.id.tv_username);
        loadUserProfile();
        authManager = new AuthManager();

        btn_logout.setOnClickListener(v -> {
            authManager.logout();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btn_topup.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RechargeActivity.class);
            startActivity(intent);
        });

        iv_user_avatar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UserDetailsActivity.class);
            startActivity(intent);
        });
        return view;
    }
    private void loadUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid)
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (error != null) {
                            Log.e("ProfileFragment", "Lỗi tải dữ liệu", error);
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("username");

                            if (name != null && !name.isEmpty()) {
                                tv_username.setText(name);
                            } else {
                                String email = currentUser.getEmail();
                                if (email != null && email.contains("@")) {
                                    tv_username.setText(email.substring(0, email.indexOf("@")));
                                }
                            }
                        }
                    });
        }
    }
}
