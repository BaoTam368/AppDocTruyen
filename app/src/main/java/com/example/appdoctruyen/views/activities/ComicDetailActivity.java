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
        if (isBlank(mangaId)) {
            btnReadChapter.setVisibility(View.GONE);
            return;
        }

        btnReadChapter.setVisibility(View.GONE);
        mangaRepository.getMangaChapters(mangaId, new MangaRepository.RepositoryCallback<List<Chapter>>() {
            @Override
            public void onSuccess(List<Chapter> chapters) {
                if (isFinishing() || isDestroyed()) return;

                Comic history = getReadingHistoryIfLoggedIn();
                Chapter targetChapter = null;
                if (history != null && !isBlank(history.getChapterId())) {
                    targetChapter = findChapterById(chapters, history.getChapterId());
                    if (targetChapter != null) {
                        btnReadChapter.setText("Continue " + displayChapterName(history.getLastReadChapter(), "previous chapter") + " \u2192");
                    }
                }

                if (targetChapter == null) {
                    targetChapter = findFirstChapter(chapters);
                    if (targetChapter == null) {
                        btnReadChapter.setVisibility(View.GONE);
                        return;
                    }
                    btnReadChapter.setText("Read from start (" + displayChapterName(targetChapter.getName(), "Chapter") + ") \u2192");
                }

                final Chapter chapterToOpen = targetChapter;
                btnReadChapter.setOnClickListener(v -> {
                    if (chapterToOpen == null || isBlank(chapterToOpen.getChapterId())) {
                        btnReadChapter.setVisibility(View.GONE);
                        return;
                    }
                    Intent intent = new Intent(ComicDetailActivity.this, ComicReadingActivity.class);
                    intent.putExtra("mangaId", mangaId);
                    intent.putExtra("mangaTitle", mangaTitle);
                    intent.putExtra("chapterId", chapterToOpen.getChapterId());
                    intent.putExtra("chapterName", chapterToOpen.getName());
                    intent.putExtra("comic_cover", currentCoverUrl);
                    startActivity(intent);
                });
                btnReadChapter.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) return;
                btnReadChapter.setVisibility(View.GONE);
            }
        });
    }

    private Comic getReadingHistoryIfLoggedIn() {
        AuthManager authManager = new AuthManager(this);
        String userId = authManager.getCurrentUserId();
        if (isBlank(userId)) return null;

        BookshelfDatabaseHelper dbHelper = null;
        try {
            dbHelper = new BookshelfDatabaseHelper(this);
            return dbHelper.getReadingHistoryForManga(userId, mangaId);
        } catch (RuntimeException ignored) {
            return null;
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }

    private Chapter findChapterById(List<Chapter> chapters, String chapterId) {
        if (chapters == null || isBlank(chapterId)) return null;
        for (Chapter ch : chapters) {
            if (ch != null && chapterId.equals(ch.getChapterId())) {
                return ch;
            }
        }
        return null;
    }

    private Chapter findFirstChapter(List<Chapter> chapters) {
        if (chapters == null || chapters.isEmpty()) return null;

        Chapter firstChapter = null;
        for (Chapter ch : chapters) {
            if (ch == null || isBlank(ch.getChapterId())) continue;
            if (firstChapter == null) firstChapter = ch;
            String name = ch.getName() != null ? ch.getName().toLowerCase() : "";
            if (name.contains("chapter 1") || name.equals("1")) {
                return ch;
            }
        }
        return firstChapter;
    }

    private String displayChapterName(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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