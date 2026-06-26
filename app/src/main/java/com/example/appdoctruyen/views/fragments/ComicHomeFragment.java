package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.views.activities.FilterActivity;
import com.example.appdoctruyen.views.activities.MainActivity;
import com.example.appdoctruyen.views.activities.NewBookshelfActivity;
import com.example.appdoctruyen.views.activities.NotificationActivity;
import com.example.appdoctruyen.views.activities.RankingActivity;
import com.example.appdoctruyen.views.activities.SearchActivity;
import com.example.appdoctruyen.views.adapters.FeaturedComicAdapter;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.views.activities.ComicDetailActivity;
import com.example.appdoctruyen.views.adapters.BookshelfAdapter;
import com.example.appdoctruyen.data.api.MangaRepository;

import java.util.ArrayList;
import java.util.List;

public class ComicHomeFragment extends Fragment {

    private RecyclerView rvFeaturedComics;
    private RecyclerView rvRecentlyUpdated;
    private List<Comic> featuredList;
    private List<Comic> recentList;
    private FeaturedComicAdapter featuredAdapter;
    private BookshelfAdapter recentAdapter;
    private ImageView ivAvatar, ivNotification, ivRefresh;
    private LinearLayout layoutSearchBar, layoutCatNew;
    private MangaRepository mangaRepository;

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

        mangaRepository = new MangaRepository();

        // Khởi tạo list + adapter trước, tránh null
        featuredList = new ArrayList<>();
        recentList = new ArrayList<>();

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

        // Load local data trước (nhanh, không crash)
        loadLocalData();

        // Sync từ API trong background
        syncPopularMangas();

        // Click Avatar
        ivAvatar.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToProfileTab();
            }
        });

        // Click ô Tìm Kiếm -> Mở SearchActivity [INDEX]
        layoutSearchBar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            startActivity(intent);
        });

        // Click Chuông thông báo
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificationActivity.class);
            startActivity(intent);
        });

        // Click nút Refresh để đồng bộ dữ liệu
        ivRefresh.setOnClickListener(v -> {
            syncPopularMangas();
        });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadLocalData() {
        mangaRepository.getLocalMangaList(10, 0, new MangaRepository.RepositoryCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> data) {
                if (!isAdded() || getContext() == null) return;
                featuredList.clear();
                featuredList.addAll(data);
                featuredAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                // Local load fail → không hiện toast, chờ sync
            }
        });

        mangaRepository.getLocalMangaList(20, 0, new MangaRepository.RepositoryCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> data) {
                if (!isAdded() || getContext() == null) return;
                recentList.clear();
                recentList.addAll(data);
                recentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                // Local load fail → không hiện toast, chờ sync
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshListsFromLocal() {
        mangaRepository.getLocalMangaList(10, 0, new MangaRepository.RepositoryCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> data) {
                if (!isAdded() || getContext() == null) return;
                featuredList.clear();
                featuredList.addAll(data);
                featuredAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                // ignore
            }
        });

        mangaRepository.getLocalMangaList(20, 0, new MangaRepository.RepositoryCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> data) {
                if (!isAdded() || getContext() == null) return;
                recentList.clear();
                recentList.addAll(data);
                recentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                // ignore
            }
        });
    }

    private void openComicDetail(Comic comic) {
        Intent intent = new Intent(requireContext(), ComicDetailActivity.class);
        intent.putExtra("comic_id", comic.getMangaId());
        intent.putExtra("comic_title", comic.getTitle());
        intent.putExtra("mangaId", comic.getMangaId());
        intent.putExtra("comic_cover", comic.getCoverUrl());
        startActivity(intent);
    }

    private void syncPopularMangas() {
        Context ctx = getContext();
        if (ctx == null || !isAdded()) return;

        Toast.makeText(ctx, "Đang đồng bộ truyện từ MangaDex...", Toast.LENGTH_SHORT).show();
        mangaRepository.syncPopularMangas(20, new MangaRepository.RepositoryCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> data) {
                if (!isAdded() || getContext() == null) return;
                Toast.makeText(getContext(), "Đã đồng bộ " + data.size() + " truyện!", Toast.LENGTH_SHORT).show();
                // Reload từ local sau khi sync thành công
                refreshListsFromLocal();
            }

            @Override
            public void onError(String message) {
                if (!isAdded() || getContext() == null) return;
                Toast.makeText(getContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}