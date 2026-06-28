package com.example.appdoctruyen.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Notification;
import com.example.appdoctruyen.views.adapters.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        btnBack = findViewById(R.id.btnBack);
        rvNotifications = findViewById(R.id.rv_notifications);
        Log.d("TEST", String.valueOf(rvNotifications));

        btnBack.setOnClickListener(v -> finish());

        setupNotifications();
    }

    private void setupNotifications() {

        List<Notification> notifications = new ArrayList<>();

        notifications.add(new Notification("You received 500 coins in your account at 13:45 11-11-2024"));
        notifications.add(new Notification("One Piece has been updated to Chapter 1100"));
        notifications.add(new Notification("Solo Leveling has been updated to Chapter 179"));
        notifications.add(new Notification("Your account received a daily login reward"));

        NotificationAdapter adapter = new NotificationAdapter(notifications);
        Log.d("TEST", "Size = " + notifications.size());
        rvNotifications.setAdapter(adapter);
    }
}
