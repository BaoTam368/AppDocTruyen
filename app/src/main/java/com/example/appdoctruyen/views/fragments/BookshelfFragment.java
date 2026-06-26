package com.example.appdoctruyen.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.appdoctruyen.views.adapters.BookshelfAdapter;
import com.example.appdoctruyen.views.activities.ComicDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class BookshelfFragment extends Fragment {
    private static final String DEMO_USER_ID = "local_user";

    private TextView tabFollowing, tabRecentlyRead, tabDownloaded;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private BookshelfAdapter adapter;
    private BookshelfDatabaseHelper bookshelfDatabaseHelper;
    private MangaRepository mangaRepository;
    private AuthManager authManager;
    private BookshelfFirebaseHelper firebaseHelper;

    // Tab hiện tại (0 = Theo Dõi, 1 = Vừa Xem, 2 = Đã Tải)
    private int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);

        tabFollowing = view.findViewById(R.id.tabFollowing);
        tabRecentlyRead = view.findViewById( R.id.tabRecentlyRead);
        tabDownloaded = view.findViewById(R.id.tabDownloaded);
        recyclerView = view.findViewById(R.id.recyclerViewBookshelf);
        tvEmpty = view.findViewById(R.id.tvEmptyBookshelf);

        bookshelfDatabaseHelper = new BookshelfDatabaseHelper(requireContext());
        mangaRepository = new MangaRepository();
        authManager = new AuthManager();
        
        String userId = getCurrentUserId();
        if (userId != null && !userId.equals(DEMO_USER_ID)) {
            firebaseHelper = new BookshelfFirebaseHelper(userId);
        }

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        adapter = new BookshelfAdapter(requireContext(), new ArrayList<>(),
                (comic, position) -> openComicDetail(comic));
        recyclerView.setAdapter(adapter);

        setupTabListeners();
        selectTab(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String userId = getCurrentUserId();
        if (userId != null && !userId.equals(DEMO_USER_ID)) {
            firebaseHelper = new BookshelfFirebaseHelper(userId);
        } else {
            firebaseHelper = null;
        }
        selectTab(currentTab);
    }

    // Gắn sự kiện cho ba tab của tủ sách: Theo dõi, Vừa xem, Đã tải.
    private void setupTabListeners() {
        tabFollowing.setOnClickListener(v -> selectTab(0));
        tabRecentlyRead.setOnClickListener(v -> selectTab(1));
        tabDownloaded.setOnClickListener(v -> selectTab(2));
    }

    private void selectTab(int tabIndex) {
        currentTab = tabIndex;
        resetAllTabs();

        switch (tabIndex) {
            case 0:
                tabFollowing.setBackgroundResource(R.drawable.bg_tab_selected);
                tabFollowing.setTextColor(getResources().getColor(R.color.white, null));
                tabFollowing.setTypeface(null, android.graphics.Typeface.BOLD);
                loadFollowedComics();
                break;
            case 1:
                tabRecentlyRead.setBackgroundResource(R.drawable.bg_tab_selected);
                tabRecentlyRead.setTextColor(getResources().getColor(R.color.white, null));
                tabRecentlyRead.setTypeface(null, android.graphics.Typeface.BOLD);
                loadReadingHistory();
                break;
            case 2:
                tabDownloaded.setBackgroundResource(R.drawable.bg_tab_selected);
                tabDownloaded.setTextColor(getResources().getColor(R.color.white, null));
                tabDownloaded.setTypeface(null, android.graphics.Typeface.BOLD);
                loadDownloadedComics();
                break;
        }
    }

    private void loadFollowedComics() {
        String userId = getCurrentUserId();
        
        // 1. Load local SQLite data first for instant UI response
        List<Comic> localComics = bookshelfDatabaseHelper.getBookmarks(userId);
        if (localComics.isEmpty()) {
            localComics = createSampleFollowedComics();
        }
        if (isAdded()) {
            adapter.updateList(localComics);
            updateEmptyState(localComics, 0);
            refreshComicsFromApi(localComics, 0);
        }
        
        // 2. Fetch from Firebase in background and sync
        if (firebaseHelper != null) {
            firebaseHelper.getBookmarks(new BookshelfFirebaseHelper.BookshelfCallback() {
                @Override
                public void onSuccess(List<Comic> remoteComics) {
                    if (!isAdded()) return;
                    
                    // Sync remote to local SQLite
                    for (Comic rc : remoteComics) {
                        bookshelfDatabaseHelper.addBookmark(userId, rc.getMangaId(), rc.getTitle(), rc.getCoverUrl());
                    }
                    
                    // Reload local SQLite to show synced results
                    List<Comic> updatedComics = bookshelfDatabaseHelper.getBookmarks(userId);
                    if (!updatedComics.isEmpty()) {
                        adapter.updateList(updatedComics);
                        updateEmptyState(updatedComics, 0);
                        refreshComicsFromApi(updatedComics, 0);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Fail silently, we already have SQLite loaded
                }
            });
        }
    }

    private void loadReadingHistory() {
        String userId = getCurrentUserId();
        
        // 1. Load local SQLite data first
        List<Comic> localComics = bookshelfDatabaseHelper.getReadingHistory(userId);
        if (localComics.isEmpty()) {
            localComics = createSampleRecentComics();
        }
        if (isAdded()) {
            adapter.updateList(localComics);
            updateEmptyState(localComics, 1);
            refreshComicsFromApi(localComics, 1);
        }
        
        // 2. Fetch from Firebase in background and sync
        if (firebaseHelper != null) {
            firebaseHelper.getReadingHistory(new BookshelfFirebaseHelper.BookshelfCallback() {
                @Override
                public void onSuccess(List<Comic> remoteComics) {
                    if (!isAdded()) return;
                    
                    // Sync remote to local SQLite
                    for (Comic rc : remoteComics) {
                        bookshelfDatabaseHelper.saveReadingHistory(userId, rc.getMangaId(), rc.getChapterId(), rc.getChapterName(), rc.getTitle(), rc.getCoverUrl());
                    }
                    
                    // Reload local SQLite
                    List<Comic> updatedComics = bookshelfDatabaseHelper.getReadingHistory(userId);
                    if (!updatedComics.isEmpty()) {
                        adapter.updateList(updatedComics);
                        updateEmptyState(updatedComics, 1);
                        refreshComicsFromApi(updatedComics, 1);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {}
            });
        }
    }

    private void loadDownloadedComics() {
        String userId = getCurrentUserId();
        
        // 1. Load local SQLite data first
        List<Comic> localComics = bookshelfDatabaseHelper.getDownloadedComics(userId);
        if (localComics.isEmpty()) {
            localComics = createSampleDownloadedComics();
        }
        if (isAdded()) {
            adapter.updateList(localComics);
            updateEmptyState(localComics, 2);
        }
        
        // 2. Fetch from Firebase in background and sync
        if (firebaseHelper != null) {
            firebaseHelper.getDownloadedComics(new BookshelfFirebaseHelper.BookshelfCallback() {
                @Override
                public void onSuccess(List<Comic> remoteComics) {
                    if (!isAdded()) return;
                    
                    // Sync remote to local SQLite
                    for (Comic rc : remoteComics) {
                        bookshelfDatabaseHelper.addDownloadedComic(userId, rc.getMangaId(), rc.getChapterId(), rc.getChapterName(), rc.getLocalPath(), rc.getTitle(), rc.getCoverUrl());
                    }
                    
                    // Reload local SQLite
                    List<Comic> updatedComics = bookshelfDatabaseHelper.getDownloadedComics(userId);
                    if (!updatedComics.isEmpty()) {
                        adapter.updateList(updatedComics);
                        updateEmptyState(updatedComics, 2);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {}
            });
        }
    }

    private String getCurrentUserId() {
        String userId = authManager.getCurrentUserId();
        return userId != null ? userId : DEMO_USER_ID;
    }

    private void refreshComicsFromApi(List<Comic> comics, int tabIndex) {
        if (comics == null || comics.isEmpty() || tabIndex == 2) {
            return;
        }

        for (Comic cachedComic : comics) {
            String mangaId = resolveMangaId(cachedComic);
            if (isDemoMangaId(mangaId)) {
                continue;
            }

            mangaRepository.getMangaDetail(mangaId, new MangaRepository.RepositoryCallback<Comic>() {
                @Override
                public void onSuccess(Comic apiComic) {
                    if (!isAdded() || currentTab != tabIndex) {
                        return;
                    }
                    mergeApiComic(cachedComic, apiComic);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(String message) {
                    // Giữ dữ liệu cache trong SQLite để app vẫn demo được khi backend hoặc mạng lỗi
                }
            });
        }
    }

    // Cập nhật giao diện empty state khi danh sách truyện rỗng
    private void updateEmptyState(List<Comic> data, int tabIndex) {
        if (data.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            if (tabIndex == 2) {
                tvEmpty.setText(R.string.bookshelf_empty_downloaded);
            } else if (tabIndex == 1) {
                tvEmpty.setText(R.string.bookshelf_empty_history);
            } else {
                tvEmpty.setText(R.string.bookshelf_empty_following);
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
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

    private void openComicDetail(Comic comic) {
        Intent intent = new Intent(requireContext(), ComicDetailActivity.class);
        intent.putExtra("comic_id", comic.getId());
        intent.putExtra("manga_id", resolveMangaId(comic));
        intent.putExtra("comic_title", comic.getTitle());
        startActivity(intent);
    }

    private List<Comic> createSampleFollowedComics() {
        List<Comic> list = new ArrayList<>();
        list.add(createDemoComic(1, "Bất bại chân ma", "Chapter 312"));
        list.add(createDemoComic(2, "Ta không muốn...", "Chapter 251"));
        list.add(createDemoComic(3, "Vạn cổ chí tôn", "Chapter 541"));
        list.add(createDemoComic(4, "Người chơi khô...", "Chapter 95"));
        list.add(createDemoComic(5, "Hảo đồ nhi hãy...", "Chapter 214"));
        list.add(createDemoComic(6, "Cung quỷ kiếm...", "Chapter 251"));
        return list;
    }

    private List<Comic> createSampleRecentComics() {
        long now = System.currentTimeMillis();
        List<Comic> list = new ArrayList<>();
        list.add(createDemoHistoryComic(4, "Người chơi khô...", "Chapter 95", "Chapter 4", now - 30 * 60 * 1000L));
        list.add(createDemoHistoryComic(3, "Vạn cổ chí tôn", "Chapter 541", "Chapter 1", now - 2 * 60 * 60 * 1000L));
        list.add(createDemoHistoryComic(1, "Bất bại chân ma", "Chapter 312", "Chapter 4", now - 5 * 60 * 60 * 1000L));
        list.add(createDemoHistoryComic(7, "Tứ kỵ sĩ khải huyền", "Chapter 50", "Chapter 14", now - 24 * 60 * 60 * 1000L));
        list.add(createDemoHistoryComic(6, "Cung quỷ kiếm thần", "Chapter 251", "Chapter 1", now - 2 * 24 * 60 * 60 * 1000L));
        list.add(createDemoHistoryComic(8, "Thám tử Kindaichi", "Chapter 100", "Chapter 9", now - 3 * 24 * 60 * 60 * 1000L));
        return list;
    }

    private List<Comic> createSampleDownloadedComics() {
        // Tab Đã Tải vẫn để rỗng vì đồ án chưa làm chức năng tải truyện offline thật.
        return new ArrayList<>();
    }

    private Comic createDemoComic(int id, String title, String latestChapter) {
        Comic comic = new Comic(id, title, R.drawable.placeholder_comic, latestChapter);
        comic.setMangaId("demo-manga-" + id);
        return comic;
    }

    private Comic createDemoHistoryComic(int id, String title, String latestChapter,
                                         String chapterName, long lastReadTime) {
        Comic comic = createDemoComic(id, title, latestChapter);
        comic.setChapterId("demo-chapter-" + id);
        comic.setChapterName(chapterName);
        comic.setLastReadChapter(chapterName);
        comic.setLastReadTime(lastReadTime);
        return comic;
    }

    private String resolveMangaId(Comic comic) {
        if (comic.getMangaId() != null && !comic.getMangaId().trim().isEmpty()) {
            return comic.getMangaId();
        }
        return String.valueOf(comic.getId());
    }

    private void mergeApiComic(Comic cachedComic, Comic apiComic) {
        if (apiComic == null) return;

        if (!isBlank(apiComic.getTitle())) {
            cachedComic.setTitle(apiComic.getTitle());
        }
        if (!isBlank(apiComic.getDescription())) {
            cachedComic.setDescription(apiComic.getDescription());
        }
        if (!isBlank(apiComic.getCoverUrl())) {
            cachedComic.setCoverUrl(apiComic.getCoverUrl());
        }
        if (!isBlank(apiComic.getLatestChapter())) {
            cachedComic.setLatestChapter(apiComic.getLatestChapter());
        }
    }

    private boolean isDemoMangaId(String mangaId) {
        return isBlank(mangaId) || mangaId.startsWith("demo-manga-");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
