package com.example.appdoctruyen.views.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.appdoctruyen.views.fragments.BookshelfFragment;
import com.example.appdoctruyen.views.fragments.BookshelfGroupFragment;
import com.example.appdoctruyen.views.fragments.ComicHomeFragment;
import com.example.appdoctruyen.views.fragments.ProfileFragment;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.views.fragments.WorldFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_bookshelf) {
                selectedFragment = new BookshelfFragment();
            } else if (itemId == R.id.nav_stories) {
                selectedFragment = new ComicHomeFragment();
            } else if (itemId == R.id.nav_world) {
                String openTab = getIntent().getStringExtra("open_tab");
                String worldPage = getIntent().getStringExtra("world_page");

                if (itemId == R.id.nav_world) {
                    WorldFragment fragment = new WorldFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("world_page", worldPage);

                    fragment.setArguments(bundle);

                    selectedFragment = fragment;
                }
            } else if (itemId == R.id.nav_translation_team) {
                selectedFragment = new BookshelfGroupFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Mặc định mở tab Tủ Sách khi khởi động
        if (savedInstanceState == null) {
            String openTab = getIntent().getStringExtra("open_tab");

            if ("world".equals(openTab)) {
                bottomNav.setSelectedItemId(R.id.nav_world);
            } else {
                bottomNav.setSelectedItemId(R.id.nav_bookshelf);
            }
        }
    }

    public void navigateToProfileTab() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_profile);
        }
    }

}