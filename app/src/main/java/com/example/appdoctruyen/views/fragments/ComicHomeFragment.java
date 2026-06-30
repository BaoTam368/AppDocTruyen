package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.views.activities.ComicDetailActivity;
import com.example.appdoctruyen.views.activities.MainActivity;
import com.example.appdoctruyen.views.activities.NotificationActivity;
import com.example.appdoctruyen.views.activities.SearchActivity;
import com.example.appdoctruyen.views.adapters.BookshelfAdapter;
import com.example.appdoctruyen.views.adapters.FeaturedComicAdapter;

import java.util.ArrayList;
import java.util.List;

public class ComicHomeFragment extends Fragment {

    private RecyclerView rvFeaturedComics;
    private RecyclerView rvRecentlyUpdated;
    private final List<Comic> featuredList = new ArrayList<>();
    private final List<Comic> recentList = new ArrayList<>();
    private FeaturedComicAdapter featuredAdapter;
    private BookshelfAdapter recentAdapter;
    private ImageView ivAvatar, ivNotification, ivRefresh;
    private LinearLayout layoutSearchBar, layoutHomeState;
    private TextView tvHomeState;
    private Button btnHomeRetry;
    private MangaRepository mangaRepository;
    private boolean isSyncing = false;
    private boolean hasHomeData = false;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic_home, container, false);

        rvFeaturedComics = view.findViewById(R.id.rv_featured_comics);
        rvRecentlyUpdated = view.findViewById(R.id.rv_recently_updated);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        layoutSearchBar = view.findViewById(R.id.layout_search_bar);
        ivNotification = view.findViewById(R.id.iv_notification);
        ivRefresh = view.findViewById(R.id.iv_refresh);
        layoutHomeState = view.findViewById(R.id.layout_home_state);
        tvHomeState = view.findViewById(R.id.tv_home_state);
        btnHomeRetry = view.findViewById(R.id.btn_home_retry);
        mangaRepository = new MangaRepository();

        setupAdapters();
        setupClickListeners();
        loadHomeData();

        return view;
    }

    private void setupAdapters() {
        featuredAdapter = new FeaturedComicAdapter(requireContext(), featuredList,
                (comic, position) -> openComicDetail(comic));
        rvFeaturedComics.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        rvFeaturedComics.setNestedScrollingEnabled(false);
        rvFeaturedComics.setAdapter(featuredAdapter);

        recentAdapter = new BookshelfAdapter(requireContext(), recentList,
                (comic, position) -> openComicDetail(comic));
        rvRecentlyUpdated.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvRecentlyUpdated.setNestedScrollingEnabled(false);
        rvRecentlyUpdated.setAdapter(recentAdapter);
    }

    private void setupClickListeners() {
        ivAvatar.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToProfileTab();
            }
        });

        layoutSearchBar.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SearchActivity.class)));

        ivNotification.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), NotificationActivity.class)));

        ivRefresh.setOnClickListener(v -> refreshHomeMangas());

        btnHomeRetry.setOnClickListener(v -> {
            if (hasHomeData) {
                refreshHomeMangas();
            } else {
                loadHomeData();
            }
        });
    }

    private void loadHomeData() {
        showHomeState("Loading cached manga...", false);
        setRefreshEnabled(false);

        mangaRepository.getLocalMangaList(20, 0, new MangaRepository.RepositoryCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> data) {
                if (!isAdded()) return;
                setRefreshEnabled(true);
                List<Comic> safeData = data != null ? data : new ArrayList<>();
                applyHomeData(safeData);
                if (safeData.isEmpty()) {
                    showHomeState("No manga available.", true);
                } else {
                    hideHomeState();
                }
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                setRefreshEnabled(true);
                applyHomeData(new ArrayList<>());
                showHomeState("Unable to load manga. Please try again.", true);
            }
        });
    }

    private void refreshHomeMangas() {
        if (isSyncing) {
            Toast.makeText(requireContext(), "Refreshing manga. Please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        isSyncing = true;
        setRefreshEnabled(false);
        showHomeState(hasHomeData ? "Refreshing manga..." : "Loading manga...", false);

        mangaRepository.syncPopularMangas(200, new MangaRepository.RepositoryCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> data) {
                if (!isAdded()) return;
                isSyncing = false;
                setRefreshEnabled(true);
                List<Comic> safeData = data != null ? data : new ArrayList<>();
                applyHomeData(safeData);
                if (safeData.isEmpty()) {
                    showHomeState("No manga available.", true);
                } else {
                    hideHomeState();
                    Toast.makeText(requireContext(), "Manga refreshed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                isSyncing = false;
                setRefreshEnabled(true);
                if (hasHomeData) {
                    hideHomeState();
                    Toast.makeText(requireContext(), "Unable to refresh. Showing cached data.", Toast.LENGTH_SHORT).show();
                } else {
                    showHomeState("Unable to load manga. Please try again.", true);
                }
            }
        });
    }

    private void applyHomeData(List<Comic> data) {
        featuredList.clear();
        recentList.clear();

        if (data != null) {
            int featuredCount = Math.min(10, data.size());
            for (int i = 0; i < featuredCount; i++) {
                featuredList.add(data.get(i));
            }
            recentList.addAll(data);
        }

        hasHomeData = !recentList.isEmpty();
        rvFeaturedComics.setVisibility(hasHomeData ? View.VISIBLE : View.GONE);
        rvRecentlyUpdated.setVisibility(hasHomeData ? View.VISIBLE : View.GONE);
        featuredAdapter.notifyDataSetChanged();
        recentAdapter.notifyDataSetChanged();
    }

    private void openComicDetail(Comic comic) {
        if (comic == null || comic.getMangaId() == null || comic.getMangaId().trim().isEmpty()) {
            if (isAdded()) {
                Toast.makeText(requireContext(), "Missing mangaId", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        Intent intent = new Intent(requireContext(), ComicDetailActivity.class);
        intent.putExtra("comic_id", comic.getMangaId());
        intent.putExtra("comic_title", comic.getTitle());
        intent.putExtra("mangaId", comic.getMangaId());
        intent.putExtra("comic_cover", comic.getCoverUrl());
        startActivity(intent);
    }

    private void showHomeState(String message, boolean showRetry) {
        if (layoutHomeState == null || tvHomeState == null || btnHomeRetry == null) return;
        tvHomeState.setText(message);
        layoutHomeState.setVisibility(View.VISIBLE);
        btnHomeRetry.setVisibility(showRetry ? View.VISIBLE : View.GONE);
    }

    private void hideHomeState() {
        if (layoutHomeState != null) {
            layoutHomeState.setVisibility(View.GONE);
        }
    }

    private void setRefreshEnabled(boolean enabled) {
        if (ivRefresh != null) {
            ivRefresh.setEnabled(enabled);
            ivRefresh.setAlpha(enabled ? 1f : 0.5f);
        }
    }
}