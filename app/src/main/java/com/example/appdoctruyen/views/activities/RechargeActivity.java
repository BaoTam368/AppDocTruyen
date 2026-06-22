package com.example.appdoctruyen.views.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdoctruyen.R;

public class RechargeActivity extends AppCompatActivity {
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        }
}
