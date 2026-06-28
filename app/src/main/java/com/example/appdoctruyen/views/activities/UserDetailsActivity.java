package com.example.appdoctruyen.views.activities;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdoctruyen.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDetailsActivity extends AppCompatActivity {
    private ImageView btnBack, ivSettings;
    private TextView tv_username;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        btnBack = findViewById(R.id.iv_profile_back);
        btnBack.setOnClickListener(v -> finish());
        ivSettings = findViewById(R.id.iv_settings_gear);
        tv_username = findViewById(R.id.tv_username);
        loadUserProfile();

        ivSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);


            startActivity(intent);
        });

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
