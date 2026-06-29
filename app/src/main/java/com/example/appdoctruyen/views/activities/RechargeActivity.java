package com.example.appdoctruyen.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.firebase.AuthManager;

public class RechargeActivity extends AppCompatActivity {
    private ImageView ivTopUpBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        AuthManager authManager = new AuthManager(this);
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to use this feature.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        ivTopUpBack = findViewById(R.id.iv_top_up_back);
        ivTopUpBack.setOnClickListener(v -> finish());
    }
}