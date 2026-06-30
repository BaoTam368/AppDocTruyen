package com.example.appdoctruyen.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.data.database.BookshelfDatabaseHelper;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.data.firebase.BookshelfFirebaseHelper;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.views.activities.ComicDetailActivity;
import com.example.appdoctruyen.views.activities.ComicReadingActivity;
import com.example.appdoctruyen.views.activities.LoginActivity;
import com.example.appdoctruyen.views.adapters.BookshelfAdapter;

import java.util.ArrayList;
import java.util.List;

public class BookshelfFragment extends Fragment {
    private TextView tabFollowing, tabRecentlyRead, tabDownloaded;
    private LinearLayout tabContainer;
    private RecyclerView recyclerView;
    private LinearLayout layoutBookshelfState;
    private TextView tvEmpty;
    private Button btnLogin;

    private BookshelfAdapter adapter;
    private BookshelfDatabaseHelper bookshelfDatabaseHelper;
    private MangaRepository mangaRepository;
    private AuthManager authManager;

    private int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);

        tabFollowing = view.findViewById(R.id.tabFollowing);
        tabRecentlyRead = view.findViewById(R.id.tabRecentlyRead);
        tabDownloaded = view.findViewById(R.id.tabDownloaded);
        tabContainer = view.findViewById(R.id.tabContainerBookshelf);
        recyclerView = view.findViewById(R.id.recyclerViewBookshelf);
        layoutBookshelfState = view.findViewById(R.id.layoutBookshelfState);
        tvEmpty = view.findViewById(R.id.tvEmptyBookshelf);
        btnLogin = view.findViewById(R.id.btnBookshelfLogin);
        bookshelfDatabaseHelper = new BookshelfDatabaseHelper(requireContext().getApplicationContext());
        mangaRepository = new MangaRepository();
        authManager = new AuthManager(requireContext());

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        adapter = new BookshelfAdapter(requireContext(), new ArrayList<>(),
                (comic, position) -> openComicDetail(comic));
        recyclerView.setAdapter(adapter);

        setupTabListeners();
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> startActivity(new Intent(requireContext(), LoginActivity.class)));
        }
        if (!isLoggedIn()) {
            showLoginRequiredState();
            return view;
        }
        selectTab(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLoggedIn()) {
            showLoginRequiredState();
            return;
        }
        selectTab(currentTab);
        syncWithFirebase();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bookshelfDatabaseHelper != null) {
            bookshelfDatabaseHelper.close();
            bookshelfDatabaseHelper = null;
        }
    }

    private void openComicDetail(Comic comic) {
        if (comic == null || isBlank(comic.getMangaId())) return;

        Intent intent = new Intent(requireContext(), ComicDetailActivity.class);
        intent.putExtra("mangaId", comic.getMangaId());
        intent.putExtra("comic_title", comic.getTitle());
        intent.putExtra("comic_cover", comic.getCoverUrl());
        startActivity(intent);
    }

    private void setupTabListeners() {
        tabFollowing.setOnClickListener(v -> selectTab(0));
        tabRecentlyRead.setOnClickListener(v -> selectTab(1));
        tabDownloaded.setOnClickListener(v -> selectTab(2));
    }

    private void selectTab(int tabIndex) {
        if (!isLoggedIn()) {
            showLoginRequiredState();
            return;
        }
        if (tabContainer != null) tabContainer.setVisibility(View.VISIBLE);
        currentTab = tabIndex;
        resetAllTabs();

        List<Comic> data;
        int emptyMessageResId;

        switch (tabIndex) {
            case 0:
                tabFollowing.setBackgroundResource(R.drawable.bg_tab_selected);
                tabFollowing.setTextColor(getResources().getColor(R.color.white, null));
                tabFollowing.setTypeface(null, android.graphics.Typeface.BOLD);
                data = getBookmarksFromDatabase();
                emptyMessageResId = R.string.bookshelf_empty_following;
                break;
            case 1:
                tabRecentlyRead.setBackgroundResource(R.drawable.bg_tab_selected);
                tabRecentlyRead.setTextColor(getResources().getColor(R.color.white, null));
                tabRecentlyRead.setTypeface(null, android.graphics.Typeface.BOLD);
                data = getReadingHistoryFromDatabase();
                emptyMessageResId = R.string.bookshelf_empty_history;
                break;
            case 2:
                tabDownloaded.setBackgroundResource(R.drawable.bg_tab_selected);
                tabDownloaded.setTextColor(getResources().getColor(R.color.white, null));
                tabDownloaded.setTypeface(null, android.graphics.Typeface.BOLD);
                data = getDownloadedComicsFromDatabase();
                emptyMessageResId = R.string.bookshelf_empty_downloaded;
                break;
            default:
                data = new ArrayList<>();
                emptyMessageResId = R.string.bookshelf_empty_following;
                break;
        }

        showBookshelfData(data, emptyMessageResId);
        enrichMissingComicDetails(data, tabIndex);
    }

    private void showBookshelfData(List<Comic> data, int emptyMessageResId) {
        List<Comic> safeData = data != null ? data : new ArrayList<>();
        adapter.updateList(safeData);

        if (safeData.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            if (layoutBookshelfState != null) layoutBookshelfState.setVisibility(View.VISIBLE);
            if (tvEmpty != null) {
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText(emptyMessageResId);
            }
            if (btnLogin != null) btnLogin.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            if (layoutBookshelfState != null) layoutBookshelfState.setVisibility(View.GONE);
            if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
            if (btnLogin != null) btnLogin.setVisibility(View.GONE);
        }
    }

    private void resetAllTabs() {
        tabFollowing.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabFollowing.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabFollowing.setTypeface(null, android.graphics.Typeface.NORMAL);

        tabRecentlyRead.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabRecentlyRead.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabRecentlyRead.setTypeface(null, android.graphics.Typeface.NORMAL);

        tabDownloaded.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabDownloaded.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabDownloaded.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private List<Comic> getBookmarksFromDatabase() {
        if (bookshelfDatabaseHelper == null) return new ArrayList<>();
        String userId = getCurrentUserId();
        if (isBlank(userId)) return new ArrayList<>();
        try {
            return bookshelfDatabaseHelper.getBookmarks(userId);
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }

    private List<Comic> getReadingHistoryFromDatabase() {
        if (bookshelfDatabaseHelper == null) return new ArrayList<>();
        String userId = getCurrentUserId();
        if (isBlank(userId)) return new ArrayList<>();
        try {
            return bookshelfDatabaseHelper.getReadingHistory(userId);
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }

    private List<Comic> getDownloadedComicsFromDatabase() {
        if (bookshelfDatabaseHelper == null) return new ArrayList<>();
        String userId = getCurrentUserId();
        if (isBlank(userId)) return new ArrayList<>();
        try {
            return bookshelfDatabaseHelper.getDownloadedComics(userId);
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }

    private String getCurrentUserId() {
        if (authManager == null && isAdded()) {
            authManager = new AuthManager(requireContext());
        }
        return authManager != null ? authManager.getCurrentUserId() : null;
    }

    private void syncWithFirebase() {
        if (!isLoggedIn() || bookshelfDatabaseHelper == null) return;
        String userId = getCurrentUserId();
        if (isBlank(userId)) return;

        BookshelfFirebaseHelper firebaseHelper = new BookshelfFirebaseHelper(userId);

        firebaseHelper.getBookmarks(new BookshelfFirebaseHelper.BookshelfCallback() {
            @Override
            public void onSuccess(List<Comic> comics) {
                if (!isAdded() || comics == null || bookshelfDatabaseHelper == null) return;

                boolean hasNew = false;
                for (Comic comic : comics) {
                    if (comic == null || isBlank(comic.getMangaId())) continue;
                    if (!bookshelfDatabaseHelper.isBookmarked(userId, comic.getMangaId())) {
                        bookshelfDatabaseHelper.addBookmark(userId, comic);
                        hasNew = true;
                    }
                }
                if (hasNew && currentTab == 0) {
                    requireActivity().runOnUiThread(() -> selectTab(0));
                }
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });

        firebaseHelper.getReadingHistory(new BookshelfFirebaseHelper.BookshelfCallback() {
            @Override
            public void onSuccess(List<Comic> comics) {
                if (!isAdded() || comics == null || bookshelfDatabaseHelper == null) return;

                boolean hasNew = false;
                for (Comic comic : comics) {
                    if (comic == null || isBlank(comic.getMangaId())) continue;
                    Comic localHistory = bookshelfDatabaseHelper.getReadingHistoryForManga(userId, comic.getMangaId());
                    if (localHistory == null || localHistory.getLastReadTime() < comic.getLastReadTime()) {
                        bookshelfDatabaseHelper.saveReadingHistory(userId, comic);
                        hasNew = true;
                    }
                }
                if (hasNew && currentTab == 1) {
                    requireActivity().runOnUiThread(() -> selectTab(1));
                }
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });

        firebaseHelper.getDownloadedComics(new BookshelfFirebaseHelper.BookshelfCallback() {
            @Override
            public void onSuccess(List<Comic> comics) {
                if (!isAdded() || comics == null || bookshelfDatabaseHelper == null) return;

                boolean hasNew = false;
                for (Comic comic : comics) {
                    if (comic == null || isBlank(comic.getMangaId())) continue;
                    if (!bookshelfDatabaseHelper.isDownloaded(userId, comic.getMangaId())) {
                        bookshelfDatabaseHelper.addDownloadedComic(userId, comic);
                        hasNew = true;
                    }
                }
                if (hasNew && currentTab == 2) {
                    requireActivity().runOnUiThread(() -> selectTab(2));
                }
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }

    private void showLoginRequiredState() {
        if (tabContainer != null) tabContainer.setVisibility(View.GONE);
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        if (adapter != null) adapter.updateList(new ArrayList<>());
        if (layoutBookshelfState != null) layoutBookshelfState.setVisibility(View.VISIBLE);
        if (tvEmpty != null) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(R.string.bookshelf_login_required);
        }
        if (btnLogin != null) btnLogin.setVisibility(View.VISIBLE);
    }

    private boolean isLoggedIn() {
        if (authManager == null && isAdded()) {
            authManager = new AuthManager(requireContext());
        }
        return authManager != null && authManager.isLoggedIn();
    }

    private void enrichMissingComicDetails(List<Comic> comics, int tabIndex) {
        if (mangaRepository == null || comics == null || comics.isEmpty()) return;

        for (Comic comic : comics) {
            if (comic == null || isBlank(comic.getMangaId()) || !needsRemoteDetails(comic)) {
                continue;
            }

            mangaRepository.getMangaDetail(comic.getMangaId(), new MangaRepository.RepositoryCallback<Comic>() {
                @Override
                public void onSuccess(Comic remoteComic) {
                    if (!isAdded() || currentTab != tabIndex || remoteComic == null) return;
                    mergeRemoteComic(comic, remoteComic);
                    cacheRemoteComic(comic);
                    adapter.updateList(comics);
                }

                @Override
                public void onError(String message) {
                    // Keep the real local row visible; do not replace it with sample data.
                }
            });
        }
    }

    private boolean needsRemoteDetails(Comic comic) {
        return isBlank(comic.getTitle())
                || "Unknown".equalsIgnoreCase(comic.getTitle())
                || isBlank(comic.getCoverUrl())
                || isBlank(comic.getDescription())
                || isBlank(comic.getStatus())
                || comic.getYear() == null;
    }

    private void mergeRemoteComic(Comic target, Comic remote) {
        if (target == null || remote == null) return;

        if (!isBlank(remote.getTitle())) target.setTitle(remote.getTitle());
        if (!isBlank(remote.getCoverUrl())) target.setCoverUrl(remote.getCoverUrl());
        if (!isBlank(remote.getDescription())) target.setDescription(remote.getDescription());
        if (!isBlank(remote.getLatestChapter())) target.setLatestChapter(remote.getLatestChapter());
        if (!isBlank(remote.getStatus())) target.setStatus(remote.getStatus());
        if (remote.getYear() != null) target.setYear(remote.getYear());
        if (remote.getTags() != null) target.setTags(remote.getTags());
        if (!isBlank(remote.getContentRating())) target.setContentRating(remote.getContentRating());
        if (remote.getAvailableTranslatedLanguages() != null) {
            target.setAvailableTranslatedLanguages(remote.getAvailableTranslatedLanguages());
        }
    }

    private void cacheRemoteComic(Comic comic) {
        if (bookshelfDatabaseHelper == null || comic == null || isBlank(comic.getMangaId())) return;
        String userId = getCurrentUserId();
        if (isBlank(userId)) return;
        try {
            bookshelfDatabaseHelper.updateComicCache(userId, comic);
        } catch (RuntimeException ignored) {
            // Cache refresh failure must not break the bookshelf UI.
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
