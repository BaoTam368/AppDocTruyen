package com.example.appdoctruyen.views.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.User;
import com.example.appdoctruyen.views.adapters.RankingAdapter;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    private ImageView btnBack;
    private RecyclerView rv_top_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        btnBack = findViewById(R.id.btnBack);
        rv_top_users = findViewById(R.id.rv_top_users);

        btnBack.setOnClickListener(v -> finish());
        setupTopUser();
    }

    private void setupTopUser() {

        List<User> users = new ArrayList<>();

//        users.add(new User(2, "Thạch Hầu A", R.drawable.avatar_default));
//        users.add(new User(3, "Thạch Hầu B", R.drawable.avatar_default));
//        users.add(new User(4, "Thạch Hầu C", R.drawable.avatar_default));
//        users.add(new User(5, "Thạch Hầu D", R.drawable.avatar_default));
//        users.add(new User(6, "Thạch Hầu E", R.drawable.avatar_default));

        RankingAdapter adapter = new RankingAdapter(users);

        rv_top_users.setLayoutManager(new LinearLayoutManager(this));

        rv_top_users.setAdapter(adapter);
    }
}
