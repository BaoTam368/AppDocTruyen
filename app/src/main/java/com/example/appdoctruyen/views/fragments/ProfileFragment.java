package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

public class ProfileFragment extends Fragment {
    LinearLayout btn_logout, btn_topup;
    ShapeableImageView iv_user_avatar;
    private AuthManager authManager;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btn_logout = view.findViewById(R.id.btn_logout);
        btn_topup = view.findViewById(R.id.btn_topup);
        iv_user_avatar = view.findViewById(R.id.iv_user_avatar);
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
}
