package com.example.appdoctruyen.views.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdoctruyen.R;
import com.google.android.material.imageview.ShapeableImageView;

public class UserDetailsActivity extends AppCompatActivity {
    private ImageView btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

    }
}
