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
import com.example.appdoctruyen.data.database.BookshelfDatabaseHelper;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.data.firebase.BookshelfFirebaseHelper;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.views.activities.ComicDetailActivity;
import com.example.appdoctruyen.views.adapters.BookshelfAdapter;

import java.util.ArrayList;
import java.util.List;

public class BookshelfFragment extends Fragment {

    private static final String DEMO_USER_ID = "local_user";

    private TextView tabFollowing, tabRecentlyRead, tabDownloaded;

    private RecyclerView recyclerView;
    private TextView tvEmpty;

    private BookshelfAdapter adapter;
    private BookshelfDatabaseHelper bookshelfDatabaseHelper;

    // Tab hiện tại (0 = Theo Dõi, 1 = Vừa Xem, 2 = Đã Tải)
    private int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);

        tabFollowing = view.findViewById(R.id.tabFollowing);
        tabRecentlyRead = view.findViewById(R.id.tabRecentlyRead);
        tabDownloaded = view.findViewById(R.id.tabDownloaded);
        recyclerView = view.findViewById(R.id.recyclerViewBookshelf);
        tvEmpty = view.findViewById(R.id.tvEmptyBookshelf);
        bookshelfDatabaseHelper = new BookshelfDatabaseHelper(requireContext().getApplicationContext());

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        adapter = new BookshelfAdapter(requireContext(), new ArrayList<>(),
                (comic, position) -> openComicDetail(comic));
        recyclerView.setAdapter(adapter);

        setupTabListeners();

        // Mặc định chọn tab Theo Dõi
        selectTab(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if (comic == null) return;

        Intent intent = new Intent(requireContext(), ComicDetailActivity.class);
        intent.putExtra("mangaId", resolveMangaId(comic));
        intent.putExtra("comic_id", comic.getId());
        intent.putExtra("comic_title", comic.getTitle());
        startActivity(intent);
    }


    private void setupTabListeners() {
        tabFollowing.setOnClickListener(v -> selectTab(0));
        tabRecentlyRead.setOnClickListener(v -> selectTab(1));
        tabDownloaded.setOnClickListener(v -> selectTab(2));
    }


    private void selectTab(int tabIndex) {
        currentTab = tabIndex;

        resetAllTabs();

        List<Comic> data;
        switch (tabIndex) {
            case 0:
                tabFollowing.setBackgroundResource(R.drawable.bg_tab_selected);
                tabFollowing.setTextColor(getResources().getColor(R.color.white, null));
                tabFollowing.setTypeface(null, android.graphics.Typeface.BOLD);
                data = loadFollowedComics();
                break;
            case 1:
                tabRecentlyRead.setBackgroundResource(R.drawable.bg_tab_selected);
                tabRecentlyRead.setTextColor(getResources().getColor(R.color.white, null));
                tabRecentlyRead.setTypeface(null, android.graphics.Typeface.BOLD);
                data = loadRecentComics();
                break;
            case 2:
                tabDownloaded.setBackgroundResource(R.drawable.bg_tab_selected);
                tabDownloaded.setTextColor(getResources().getColor(R.color.white, null));
                tabDownloaded.setTypeface(null, android.graphics.Typeface.BOLD);
                data = loadDownloadedComics();
                break;
            default:
                data = new ArrayList<>();
        }

        adapter.updateList(data);
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

    // SQLite loading with sample fallback

    private List<Comic> loadFollowedComics() {
        List<Comic> comics = getBookmarksFromDatabase();
        return comics.isEmpty() ? createSampleFollowedComics() : comics;
    }

    private List<Comic> loadRecentComics() {
        List<Comic> comics = getReadingHistoryFromDatabase();
        return comics.isEmpty() ? createSampleRecentComics() : comics;
    }

    private List<Comic> loadDownloadedComics() {
        List<Comic> comics = getDownloadedComicsFromDatabase();
        return comics.isEmpty() ? createSampleDownloadedComics() : comics;
    }

    private List<Comic> getBookmarksFromDatabase() {
        if (bookshelfDatabaseHelper == null) return new ArrayList<>();
        try {
            return bookshelfDatabaseHelper.getBookmarks(getCurrentUserId());
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }

    private List<Comic> getReadingHistoryFromDatabase() {
        if (bookshelfDatabaseHelper == null) return new ArrayList<>();
        try {
            return bookshelfDatabaseHelper.getReadingHistory(getCurrentUserId());
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }

    private List<Comic> getDownloadedComicsFromDatabase() {
        if (bookshelfDatabaseHelper == null) return new ArrayList<>();
        try {
            return bookshelfDatabaseHelper.getDownloadedComics(getCurrentUserId());
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }

    private String getCurrentUserId() {
        AuthManager authManager = new AuthManager();
        String userId = authManager.getCurrentUserId();
        return userId != null ? userId : "local_user";
    }

    private void syncWithFirebase() {
        String userId = getCurrentUserId();
        if (userId.equals("local_user")) return;

        BookshelfFirebaseHelper fbHelper = new BookshelfFirebaseHelper(userId);
        
        fbHelper.getBookmarks(new BookshelfFirebaseHelper.BookshelfCallback() {
            @Override
            public void onSuccess(List<Comic> comics) {
                if (comics != null && bookshelfDatabaseHelper != null) {
                    boolean hasNew = false;
                    for (Comic comic : comics) {
                        if (!bookshelfDatabaseHelper.isBookmarked(userId, comic.getMangaId())) {
                            bookshelfDatabaseHelper.addBookmark(userId, comic.getMangaId(), comic.getTitle(), comic.getCoverUrl());
                            hasNew = true;
                        }
                    }
                    if (hasNew && currentTab == 0 && getActivity() != null) {
                        getActivity().runOnUiThread(() -> selectTab(0));
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage) {}
        });

        fbHelper.getReadingHistory(new BookshelfFirebaseHelper.BookshelfCallback() {
            @Override
            public void onSuccess(List<Comic> comics) {
                if (comics != null && bookshelfDatabaseHelper != null) {
                    boolean hasNew = false;
                    for (Comic comic : comics) {
                        Comic localHist = bookshelfDatabaseHelper.getReadingHistoryForManga(userId, comic.getMangaId());
                        if (localHist == null || localHist.getLastReadTime() < comic.getLastReadTime()) {
                            bookshelfDatabaseHelper.saveReadingHistory(userId, comic.getMangaId(), comic.getChapterId(), comic.getChapterName(), comic.getTitle(), comic.getCoverUrl());
                            hasNew = true;
                        }
                    }
                    if (hasNew && currentTab == 1 && getActivity() != null) {
                        getActivity().runOnUiThread(() -> selectTab(1));
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage) {}
        });

        fbHelper.getDownloadedComics(new BookshelfFirebaseHelper.BookshelfCallback() {
            @Override
            public void onSuccess(List<Comic> comics) {
                if (comics != null && bookshelfDatabaseHelper != null) {
                    boolean hasNew = false;
                    for (Comic comic : comics) {
                        if (!bookshelfDatabaseHelper.isDownloaded(userId, comic.getMangaId())) {
                            bookshelfDatabaseHelper.addDownloadedComic(userId, comic.getMangaId(), comic.getChapterId(), comic.getChapterName(), comic.getLocalPath(), comic.getTitle(), comic.getCoverUrl());
                            hasNew = true;
                        }
                    }
                    if (hasNew && currentTab == 2 && getActivity() != null) {
                        getActivity().runOnUiThread(() -> selectTab(2));
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage) {}
        });
    }

    private String resolveMangaId(Comic comic) {
        return isBlank(comic.getMangaId()) ? String.valueOf(comic.getId()) : comic.getMangaId();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // DỮ LIỆU MẪU

    private List<Comic> createSampleFollowedComics() {
        List<Comic> list = new ArrayList<>();
        list.add(new Comic(1, "Bất bại chân ma", R.drawable.placeholder_comic, "Chapter 312"));
        list.add(new Comic(2, "Ta không muốn...", R.drawable.placeholder_comic, "Chapter 251"));
        list.add(new Comic(3, "Vạn cổ chí tôn", R.drawable.placeholder_comic, "Chapter 541"));
        list.add(new Comic(4, "Người chơi khô...", R.drawable.placeholder_comic, "Chapter 95"));
        list.add(new Comic(5, "Hảo đồ nhi hãy...", R.drawable.placeholder_comic, "Chapter 214"));
        list.add(new Comic(6, "Cung quỷ kiếm...", R.drawable.placeholder_comic, "Chapter 251"));
        return list;
    }

    private List<Comic> createSampleRecentComics() {
        List<Comic> list = new ArrayList<>();
        Comic c1 = new Comic(4, "Người chơi khô...", R.drawable.placeholder_comic, "Chapter 95");
        c1.setLastReadChapter("Chapter 4");
        list.add(c1);

        Comic c2 = new Comic(3, "Vạn cổ chí tôn", R.drawable.placeholder_comic, "Chapter 541");
        c2.setLastReadChapter("Chapter 1");
        list.add(c2);

        Comic c3 = new Comic(1, "Bất bại chân ma", R.drawable.placeholder_comic, "Chapter 312");
        c3.setLastReadChapter("Chapter 4");
        list.add(c3);

        Comic c4 = new Comic(7, "Tứ kỵ sĩ khải huyền", R.drawable.placeholder_comic, "Chapter 50");
        c4.setLastReadChapter("Chapter 14");
        list.add(c4);

        Comic c5 = new Comic(6, "Cung quỷ kiếm thần", R.drawable.placeholder_comic, "Chapter 251");
        c5.setLastReadChapter("Chapter 1");
        list.add(c5);

        Comic c6 = new Comic(8, "Thám tử Kindaichi", R.drawable.placeholder_comic, "Chapter 100");
        c6.setLastReadChapter("Chapter 9");
        list.add(c6);

        return list;
    }

    private List<Comic> createSampleDownloadedComics() {
        // Trả về rỗng để hiển thị "Không có truyện đã tải"
        return new ArrayList<>();
    }
}
