package com.example.appdoctruyen.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.data.database.BookshelfDatabaseHelper;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.models.Chapter;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.views.adapters.ComicDetailAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class ComicDetailActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView btnBack;
    private MaterialButton btnReadChapter;
    private String mangaId;
    private String mangaTitle;
    private String currentCoverUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        mangaId = getIntent().getStringExtra("mangaId");
        if (mangaId == null) {
            mangaId = getIntent().getStringExtra("manga_id");
        }
        if (mangaId == null) {
            mangaId = getIntent().getStringExtra("comic_id");
        }
        if (mangaId == null) {
            int numericId = getIntent().getIntExtra("comic_id", -1);
            if (numericId != -1) {
                mangaId = String.valueOf(numericId);
            }
        }
        mangaTitle = getIntent().getStringExtra("comic_title");
        currentCoverUrl = getIntent().getStringExtra("comic_cover");
        if (currentCoverUrl == null) currentCoverUrl = "";

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);
        btnReadChapter = findViewById(R.id.btnReadChapter);

        ComicDetailAdapter adapter = new ComicDetailAdapter(this, mangaId, mangaTitle);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Introduction");
            } else if (position == 1) {
                tab.setText("Chapter List");
            } else {
                tab.setText("Comments");
            }
        }).attach();

        btnBack.setOnClickListener(v -> finish());
        setupReadButton();
    }

    private void setupReadButton() {
        MangaRepository mangaRepository = new MangaRepository();
        if (mangaId != null && !mangaId.isEmpty()) {
            btnReadChapter.setVisibility(View.GONE);
            mangaRepository.getMangaChapters(mangaId, new MangaRepository.RepositoryCallback<List<Chapter>>() {
                @Override
                public void onSuccess(List<Chapter> chapters) {
                    if (chapters != null && !chapters.isEmpty() && !isFinishing()) {
                        BookshelfDatabaseHelper dbHelper = new BookshelfDatabaseHelper(ComicDetailActivity.this);
                        AuthManager authManager = new AuthManager(ComicDetailActivity.this);
                        String userId = authManager.getCurrentUserId();
                        if (userId == null) userId = "local_user";

                        Comic history = dbHelper.getReadingHistoryForManga(userId, mangaId);
                        final Chapter targetChapter;
                        if (history != null && history.getChapterId() != null) {
                            targetChapter = findChapterById(chapters, history.getChapterId());
                            btnReadChapter.setText("Continue " + (history.getLastReadChapter() != null ? history.getLastReadChapter() : "previous chapter") + " \u2192");
                        } else {
                            targetChapter = findFirstChapter(chapters);
                            btnReadChapter.setText("Read from start (" + targetChapter.getName() + ") \u2192");
                        }

                        btnReadChapter.setOnClickListener(v -> {
                            Intent intent = new Intent(ComicDetailActivity.this, ComicReadingActivity.class);
                            intent.putExtra("mangaId", mangaId);
                            intent.putExtra("mangaTitle", mangaTitle);
                            intent.putExtra("chapterId", targetChapter.getChapterId());
                            intent.putExtra("chapterName", targetChapter.getName());
                            intent.putExtra("comic_cover", currentCoverUrl);
                            startActivity(intent);
                        });
                        btnReadChapter.setVisibility(View.VISIBLE);
                    } else {
                        btnReadChapter.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(String message) {
                    btnReadChapter.setVisibility(View.GONE);
                }
            });
        } else {
            btnReadChapter.setVisibility(View.GONE);
        }
    }

    private Chapter findChapterById(List<Chapter> chapters, String chapterId) {
        for (Chapter ch : chapters) {
            if (ch.getChapterId() != null && ch.getChapterId().equals(chapterId)) {
                return ch;
            }
        }
        return findFirstChapter(chapters);
    }

    private Chapter findFirstChapter(List<Chapter> chapters) {
        Chapter firstChapter = chapters.get(0);
        for (Chapter ch : chapters) {
            String name = ch.getName().toLowerCase();
            if (name.contains("chapter 1") || name.contains("chapter 1") || name.equals("1")) {
                firstChapter = ch;
                break;
            }
        }
        return firstChapter;
    }

    public String getMangaId() {
        return mangaId;
    }

    public String getMangaTitle() {
        return mangaTitle;
    }

    public String getCoverUrl() {
        return currentCoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        currentCoverUrl = coverUrl != null ? coverUrl : "";
    }
}