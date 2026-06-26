package com.example.appdoctruyen.views.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

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
import com.example.appdoctruyen.data.api.MangaRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ImageView btnBack, btnFilter;
    private NestedScrollView layoutFilter;
    private EditText edtSearchInput;
    private ImageView ivClearSearch;

    private Button btnApplyFilter;
    private MangaRepository mangaRepository;

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
        edtSearchInput = findViewById(R.id.edt_search_input);
        ivClearSearch = findViewById(R.id.iv_clear_search);
        rvTopSearchTags = findViewById(R.id.rv_top_search_tags);
        rvSearchResult = findViewById(R.id.rv_search_result);
        rvGenresCheckbox = findViewById(R.id.rv_genres_checkbox);
        rvGenresCheckbox.setNestedScrollingEnabled(false);

        mangaRepository = new MangaRepository();

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

        edtSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    searchManga(query);
                } else if (query.isEmpty()) {
                    loadDefaultResults();
                }
            }
        });

        ivClearSearch.setOnClickListener(v -> {
            edtSearchInput.setText("");
            loadDefaultResults();
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

        resultAdapter = new BookshelfAdapter(this, resultList, (comic, position) -> {
            Intent intent = new Intent(SearchActivity.this, ComicDetailActivity.class);
            intent.putExtra("comic_id", comic.getId());
            intent.putExtra("mangaId", comic.getMangaId());
            intent.putExtra("comic_title", comic.getTitle());
            startActivity(intent);
        });

        rvSearchResult.setLayoutManager(new GridLayoutManager(this, 3));
        rvSearchResult.setAdapter(resultAdapter);

        loadDefaultResults();
    }

    private void loadDefaultResults() {
        mangaRepository.getLocalMangaList(20, 0, new MangaRepository.RepositoryCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> data) {
                resultList.clear();
                resultList.addAll(data);
                resultAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchManga(String query) {
        mangaRepository.searchLocalMangas(query, 20, 0, new MangaRepository.RepositoryCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> data) {
                resultList.clear();
                resultList.addAll(data);
                resultAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
