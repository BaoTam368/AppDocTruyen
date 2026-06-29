package com.example.appdoctruyen.views.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    private android.widget.LinearLayout layoutFilter;
    private EditText edtSearchInput;
    private ImageView ivClearSearch;

    private Button btnApplyFilter;
    private MangaRepository mangaRepository;

    private RecyclerView rvSearchResult;
    private RecyclerView rvGenresCheckbox;

    private List<Comic> resultList;
    private BookshelfAdapter resultAdapter;
    private GenreAdapter genreAdapter;

    // Filter state
    private String selectedSort = "title_asc"; // title_asc, title_desc

    // Sort tab views
    private TextView tvSortTitleAsc, tvSortTitleDesc;

    // Debounce handler for search
    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY_MS = 500;

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
        rvSearchResult = findViewById(R.id.rv_search_result);
        rvGenresCheckbox = findViewById(R.id.rv_genres_checkbox);
        rvGenresCheckbox.setNestedScrollingEnabled(false);

        // Sort tabs
        tvSortTitleAsc = findViewById(R.id.tvSortTitleAsc);
        tvSortTitleDesc = findViewById(R.id.tvSortTitleDesc);

        mangaRepository = new MangaRepository();

        setupSortTabs();

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
                // Debounce: cancel previous pending search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                if (query.length() >= 2) {
                    searchRunnable = () -> searchManga(query);
                    searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
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
    }

    private void setupSortTabs() {
        View.OnClickListener sortListener = v -> {
            if (v.getId() == R.id.tvSortTitleAsc) {
                selectedSort = "title_asc";
            } else if (v.getId() == R.id.tvSortTitleDesc) {
                selectedSort = "title_desc";
            }
            updateSortTabUI();
        };

        if (tvSortTitleAsc != null) tvSortTitleAsc.setOnClickListener(sortListener);
        if (tvSortTitleDesc != null) tvSortTitleDesc.setOnClickListener(sortListener);
    }

    private void updateSortTabUI() {
        int selected = R.drawable.bg_tab_selected;
        int unselected = R.drawable.bg_tab_unselected;

        if (tvSortTitleAsc != null) tvSortTitleAsc.setBackgroundResource("title_asc".equals(selectedSort) ? selected : unselected);
        if (tvSortTitleDesc != null) tvSortTitleDesc.setBackgroundResource("title_desc".equals(selectedSort) ? selected : unselected);
    }

    private void setupCheckbox() {
        List<Genre> genres = new ArrayList<>();

        genres.add(new Genre("Manhua"));
        genres.add(new Genre("Manga"));
        genres.add(new Genre("Historical"));
        genres.add(new Genre("Isekai"));
        genres.add(new Genre("Manhwa"));
        genres.add(new Genre("Romance"));
        genres.add(new Genre("System"));
        genres.add(new Genre("Action"));
        genres.add(new Genre("School Life"));
        genres.add(new Genre("Fantasy"));
        genres.add(new Genre("Post-Apocalyptic"));
        genres.add(new Genre("Reincarnation"));
        genres.add(new Genre("Cultivation"));
        genres.add(new Genre("Comedy"));
        genres.add(new Genre("Urban"));
        genres.add(new Genre("Horror"));

        genreAdapter = new GenreAdapter(genres);

        rvGenresCheckbox.setLayoutManager(new GridLayoutManager(this, 3));
        rvGenresCheckbox.setAdapter(genreAdapter);
    }

    private void applySearch() {
        String query = edtSearchInput.getText().toString().trim();
        // Lấy genre đã chọn (nếu có)
        String selectedTag = "";
        if (genreAdapter != null) {
            List<String> selectedGenres = genreAdapter.getSelectedGenres();
            if (!selectedGenres.isEmpty()) {
                selectedTag = selectedGenres.get(0); // Lấy genre đầu tiên được chọn
            }
        }

        if (query.isEmpty()) {
            loadFilteredResults(selectedTag);
        } else {
            searchMangaWithFilter(query, selectedTag);
        }
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
        loadFilteredResults("");
    }

    private void loadFilteredResults(String tag) {
        // TODO: Gọi API với filter status và sort (backend hỗ trợ via query params)
        // Dùng searchLocalMangas với status/sort (trả về từ local DB đã sync)
        mangaRepository.getLocalMangaList(20, 0, new MangaRepository.RepositoryCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> data) {
                resultList.clear();
                if (data != null) {
                    for (Comic comic : data) {
                        // Filter tag phía client
                        if (!tag.isEmpty() && (comic.getTags() == null
                                || !containsTag(comic.getTags(), tag))) {
                            continue;
                        }
                        resultList.add(comic);
                    }
                    applySortToList(resultList);
                }
                resultAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchManga(String query) {
        searchMangaWithFilter(query, "");
    }

    private void searchMangaWithFilter(String query, String tag) {
        // Gọi searchAndSync: tìm từ MangaDex và lưu vào local DB, trả kết quả ngay
        mangaRepository.searchAndSync(query, 20, 0, new MangaRepository.RepositoryCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> data) {
                resultList.clear();
                if (data != null) {
                    for (Comic comic : data) {
                        // Filter tag
                        if (!tag.isEmpty() && (comic.getTags() == null
                                || !containsTag(comic.getTags(), tag))) {
                            continue;
                        }
                        resultList.add(comic);
                    }
                    applySortToList(resultList);
                }
                resultAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                // Nếu searchAndSync lỗi (mạng), fallback sang local search
                mangaRepository.searchLocalMangas(query, 20, 0, new MangaRepository.RepositoryCallback<List<Comic>>() {
                    @Override
                    public void onSuccess(List<Comic> data) {
                        resultList.clear();
                        if (data != null) resultList.addAll(data);
                        applySortToList(resultList);
                        resultAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String fallbackMessage) {
                        Toast.makeText(SearchActivity.this, fallbackMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void applySortToList(List<Comic> list) {
        switch (selectedSort) {
            case "title_asc":
                list.sort((a, b) -> {
                    if (a.getTitle() == null) return 1;
                    if (b.getTitle() == null) return -1;
                    return a.getTitle().compareToIgnoreCase(b.getTitle());
                });
                break;
            case "title_desc":
                list.sort((a, b) -> {
                    if (a.getTitle() == null) return 1;
                    if (b.getTitle() == null) return -1;
                    return b.getTitle().compareToIgnoreCase(a.getTitle());
                });
                break;
            case "year_desc":
                list.sort((a, b) -> {
                    int ya = a.getYear() != null ? a.getYear() : 0;
                    int yb = b.getYear() != null ? b.getYear() : 0;
                    return Integer.compare(yb, ya);
                });
                break;
            case "latest":
            default:
                // Giữ nguyên thứ tự từ server (đã sort by updated_at DESC)
                break;
        }
    }

    private boolean containsTag(List<String> tags, String tag) {
        for (String t : tags) {
            if (t != null && t.equalsIgnoreCase(tag)) return true;
        }
        return false;
    }
}
