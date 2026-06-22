package com.example.appdoctruyen.views.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.models.Genre;
import com.example.appdoctruyen.views.adapters.BookshelfAdapter;
import com.example.appdoctruyen.views.adapters.GenreAdapter;
import com.example.appdoctruyen.views.adapters.TopSearchTagAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ImageView btnBack, btnFilter;
    private NestedScrollView layoutFilter;

    private Button btnApplyFilter;

    private RecyclerView rvTopSearchTags;
    private RecyclerView rvSearchResult;
    private RecyclerView rvGenresCheckbox;

    private List<Comic> resultList;
    private BookshelfAdapter resultAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);
        layoutFilter = findViewById(R.id.layoutFilter);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        rvTopSearchTags = findViewById(R.id.rv_top_search_tags);
        rvSearchResult = findViewById(R.id.rv_search_result);
        rvGenresCheckbox = findViewById(R.id.rv_genres_checkbox);
        rvGenresCheckbox.setNestedScrollingEnabled(false);

        btnBack.setOnClickListener(v -> finish());
        btnFilter.setOnClickListener(v -> {
            if (layoutFilter.getVisibility() == View.GONE) {
                layoutFilter.setVisibility(View.VISIBLE);
            } else {
                layoutFilter.setVisibility(View.GONE);
            }
        });
        btnApplyFilter.setOnClickListener(v -> {
            applySearch();
            layoutFilter.setVisibility(View.GONE);
        });

        setupSearchResult();
        setupCheckbox();
        setupTopSearchTags();

    }

    private void setupTopSearchTags() {
        List<String> data = new ArrayList<>();
        data.add("One Piece");
        data.add("Naruto");
        data.add("Attack on Titan");
        data.add("Demon Slayer");
        data.add("Jujutsu Kaisen");
        data.add("Solo Leveling");
        data.add("Dragon Ball");
        data.add("Bleach");
        data.add("Black Clover");
        data.add("Chainsaw Man");
        TopSearchTagAdapter adapter = new TopSearchTagAdapter(data);

        rvTopSearchTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTopSearchTags.setAdapter(adapter);
    }

    private void setupCheckbox() {
        List<Genre> genres = new ArrayList<>();

        genres.add(new Genre("Manhua"));
        genres.add(new Genre("Manga"));
        genres.add(new Genre("Cổ Đại"));
        genres.add(new Genre("Xuyên Không"));
        genres.add(new Genre("Manhwa"));
        genres.add(new Genre("Ngôn Tình"));
        genres.add(new Genre("Hệ Thống"));
        genres.add(new Genre("Hành Động"));
        genres.add(new Genre("Học Đường"));
        genres.add(new Genre("Huyền Huyễn"));
        genres.add(new Genre("Mạt Thế"));
        genres.add(new Genre("Trùng Sinh"));
        genres.add(new Genre("Tu Tiên"));
        genres.add(new Genre("Hài Hước"));
        genres.add(new Genre("Đô Thị"));
        genres.add(new Genre("Kinh Dị"));

        GenreAdapter adapter = new GenreAdapter(genres);

        rvGenresCheckbox.setLayoutManager(new GridLayoutManager(this, 3));
        rvGenresCheckbox.setAdapter(adapter);

    }

    private void applySearch() {
    }

    private void setupSearchResult() {

        resultList = new ArrayList<>();

        resultList.add(new Comic(1, "One Piece", R.drawable.placeholder_comic, "Chapter 1100"));
        resultList.add(new Comic(2, "Naruto", R.drawable.placeholder_comic, "Chapter 700"));
        resultList.add(new Comic(3, "Bleach", R.drawable.placeholder_comic, "Chapter 686"));
        resultList.add(new Comic(4, "Dragon Ball", R.drawable.placeholder_comic, "Chapter 519"));
        resultList.add(new Comic(5, "Jujutsu Kaisen", R.drawable.placeholder_comic, "Chapter 260"));
        resultList.add(new Comic(6, "Solo Leveling", R.drawable.placeholder_comic, "Chapter 179"));

        resultAdapter = new BookshelfAdapter(this, resultList, (comic, position) -> {

            Intent intent = new Intent(SearchActivity.this, ComicDetailActivity.class);

            intent.putExtra("comic_id", comic.getId());
            intent.putExtra("comic_title", comic.getTitle());

            startActivity(intent);
        });

        rvSearchResult.setLayoutManager(new GridLayoutManager(this, 3));

        rvSearchResult.setAdapter(resultAdapter);
    }


}
