package com.example.appdoctruyen.views.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appdoctruyen.views.adapters.ComicDetailAdapter;
import com.example.appdoctruyen.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ComicDetailActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        // 1. Ánh xạ các View từ XML
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);

        // 2. Khởi tạo Adapter và gắn vào ViewPager2
        ComicDetailAdapter adapter = new ComicDetailAdapter(this);
        viewPager.setAdapter(adapter);

        // 3. Liên kết TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Giới thiệu");
            } else {
                tab.setText("Danh sách chương");
            }
        }).attach();

        // 4. Xử lý nút Back (đóng màn hình hiện tại)
        btnBack.setOnClickListener(v -> finish());
    }
}
