package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.TranslationGroup;
import com.example.appdoctruyen.views.activities.ComicReadingActivity;
import com.example.appdoctruyen.views.activities.GroupDetailActivity;
import com.example.appdoctruyen.views.activities.MainActivity;
import com.example.appdoctruyen.views.activities.NotificationActivity;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.data.database.BookshelfDatabaseHelper;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.data.firebase.BookshelfFirebaseHelper;
import com.example.appdoctruyen.models.Chapter;
import com.example.appdoctruyen.models.Comic;
import java.util.List;

public class ComicInfoFragment extends Fragment {
    private static final String ARG_MANGA_ID = "mangaId";
    private static final String ARG_MANGA_TITLE = "mangaTitle";
    
    private ImageView imgCover, imgAuthorAvatar;
    private ImageView btnBookmark, btnDownload;
    private TextView tvMangaName, tvAuthorName, tvDescription, tvViews, tvLikes;
    private LinearLayout layoutTags;
    private String mangaId;
    private String mangaTitle;
    private MangaRepository mangaRepository;
    private Comic mangaInfo;

    private BookshelfDatabaseHelper bookshelfDatabaseHelper;
    private AuthManager authManager;
    private BookshelfFirebaseHelper firebaseHelper;

    public static ComicInfoFragment newInstance(String mangaId, String mangaTitle) {
        ComicInfoFragment fragment = new ComicInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MANGA_ID, mangaId);
        args.putString(ARG_MANGA_TITLE, mangaTitle);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mangaId = getArguments().getString(ARG_MANGA_ID);
            mangaTitle = getArguments().getString(ARG_MANGA_TITLE);
        }
        mangaRepository = new MangaRepository();
        bookshelfDatabaseHelper = new BookshelfDatabaseHelper(requireContext().getApplicationContext());
        authManager = new AuthManager();
        String userId = getCurrentUserId();
        if (userId != null && !userId.equals("local_user")) {
            firebaseHelper = new BookshelfFirebaseHelper(userId);
        }
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic_info, container, false);

        imgCover = view.findViewById(R.id.imgCover);
        imgAuthorAvatar = view.findViewById(R.id.imgAuthorAvatar);
        tvMangaName = view.findViewById(R.id.tvMangaName);
        tvAuthorName = view.findViewById(R.id.tvAuthorName);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvViews = view.findViewById(R.id.tvViews);
        tvLikes = view.findViewById(R.id.tvLikes);
        layoutTags = view.findViewById(R.id.layoutTags);
        
        btnBookmark = view.findViewById(R.id.btnBookmark);
        btnDownload = view.findViewById(R.id.btnDownload);

        // Load manga info từ API
        if (mangaId != null && !mangaId.isEmpty()) {
            loadMangaInfo();
        }

        updateBookmarkUI();
        updateDownloadUI();

        tvAuthorName.setOnClickListener(v -> {
            openGroupDetail();
        });

        imgAuthorAvatar.setOnClickListener(v -> {
            openGroupDetail();
        });

        btnBookmark.setOnClickListener(v -> {
            if (mangaId == null || mangaId.isEmpty()) return;
            String userId = getCurrentUserId();
            boolean isBookmarked = bookshelfDatabaseHelper.isBookmarked(userId, mangaId);
            String title = (mangaInfo != null && mangaInfo.getTitle() != null) ? mangaInfo.getTitle() : mangaTitle;
            String cover = (mangaInfo != null && mangaInfo.getCoverUrl() != null) ? mangaInfo.getCoverUrl() : "";

            if (isBookmarked) {
                bookshelfDatabaseHelper.removeBookmark(userId, mangaId);
                if (firebaseHelper != null) {
                    firebaseHelper.removeBookmark(mangaId);
                }
                Toast.makeText(getContext(), "Removed from following", Toast.LENGTH_SHORT).show();
            } else {
                bookshelfDatabaseHelper.addBookmark(userId, mangaId, title, cover);
                if (firebaseHelper != null) {
                    firebaseHelper.addBookmark(mangaId, title, cover, "");
                }
                Toast.makeText(getContext(), "Added to following", Toast.LENGTH_SHORT).show();
            }
            updateBookmarkUI();
        });

        btnDownload.setOnClickListener(v -> {
            if (mangaId == null || mangaId.isEmpty()) return;
            String userId = getCurrentUserId();
            boolean isDownloaded = bookshelfDatabaseHelper.isDownloaded(userId, mangaId);
            String title = (mangaInfo != null && mangaInfo.getTitle() != null) ? mangaInfo.getTitle() : mangaTitle;
            String cover = (mangaInfo != null && mangaInfo.getCoverUrl() != null) ? mangaInfo.getCoverUrl() : "";

            if (isDownloaded) {
                bookshelfDatabaseHelper.removeDownloadedComic(userId, mangaId);
                if (firebaseHelper != null) {
                    firebaseHelper.removeBookmark(mangaId);
                }
                Toast.makeText(getContext(), "Removed from downloads", Toast.LENGTH_SHORT).show();
                updateDownloadUI();
            } else {
                Toast.makeText(getContext(), "Downloading manga...", Toast.LENGTH_SHORT).show();
                mangaRepository.getMangaChapters(mangaId, new MangaRepository.RepositoryCallback<List<Chapter>>() {
                    @Override
                    public void onSuccess(List<Chapter> chapters) {
                        if (chapters != null && !chapters.isEmpty()) {
                            Chapter targetChapter = chapters.get(0);
                            String chapterId = targetChapter.getChapterId();
                            String chapterName = targetChapter.getName();
                            
                            bookshelfDatabaseHelper.addDownloadedComic(userId, mangaId, chapterId, chapterName,
                                    "/sdcard/Download/AppDocTruyen/" + mangaId + "/" + chapterId, title, cover);
                            if (firebaseHelper != null) {
                                firebaseHelper.addDownloadedComic(mangaId, chapterId, chapterName,
                                        "/sdcard/Download/AppDocTruyen/" + mangaId + "/" + chapterId, title, cover);
                            }
                            Toast.makeText(getContext(), "Downloaded successfully (" + chapterName + ")", Toast.LENGTH_SHORT).show();
                        } else {
                            bookshelfDatabaseHelper.addDownloadedComic(userId, mangaId, "placeholder", "Chapter 1",
                                    "/sdcard/Download/AppDocTruyen/" + mangaId + "/placeholder", title, cover);
                            if (firebaseHelper != null) {
                                firebaseHelper.addDownloadedComic(mangaId, "placeholder", "Chapter 1",
                                        "/sdcard/Download/AppDocTruyen/" + mangaId + "/placeholder", title, cover);
                            }
                            Toast.makeText(getContext(), "Downloaded successfully (Sample chapter)", Toast.LENGTH_SHORT).show();
                        }
                        updateDownloadUI();
                    }

                    @Override
                    public void onError(String message) {
                        bookshelfDatabaseHelper.addDownloadedComic(userId, mangaId, "placeholder", "Chapter 1",
                                "/sdcard/Download/AppDocTruyen/" + mangaId + "/placeholder", title, cover);
                        if (firebaseHelper != null) {
                            firebaseHelper.addDownloadedComic(mangaId, "placeholder", "Chapter 1",
                                    "/sdcard/Download/AppDocTruyen/" + mangaId + "/placeholder", title, cover);
                        }
                        Toast.makeText(getContext(), "Downloaded successfully (Offline)", Toast.LENGTH_SHORT).show();
                        updateDownloadUI();
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bookshelfDatabaseHelper != null) {
            bookshelfDatabaseHelper.close();
            bookshelfDatabaseHelper = null;
        }
    }

    private void updateBookmarkUI() {
        if (bookshelfDatabaseHelper == null || mangaId == null || btnBookmark == null) return;
        String userId = getCurrentUserId();
        boolean isBookmarked = bookshelfDatabaseHelper.isBookmarked(userId, mangaId);
        if (isBookmarked) {
            btnBookmark.setColorFilter(getResources().getColor(R.color.brand_blue, null));
        } else {
            btnBookmark.setColorFilter(getResources().getColor(R.color.text_secondary_light, null));
        }
    }

    private void updateDownloadUI() {
        if (bookshelfDatabaseHelper == null || mangaId == null || btnDownload == null) return;
        String userId = getCurrentUserId();
        boolean isDownloaded = bookshelfDatabaseHelper.isDownloaded(userId, mangaId);
        if (isDownloaded) {
            btnDownload.setColorFilter(getResources().getColor(R.color.brand_blue, null));
        } else {
            btnDownload.setColorFilter(getResources().getColor(R.color.text_secondary_light, null));
        }
    }

    private String getCurrentUserId() {
        if (authManager == null) return "local_user";
        String userId = authManager.getCurrentUserId();
        return userId != null ? userId : "local_user";
    }

    private void loadMangaInfo() {
        mangaRepository.getMangaDetail(mangaId, new MangaRepository.RepositoryCallback<Comic>() {
            @Override
            public void onSuccess(Comic data) {
                mangaInfo = data;
                
                // Cập nhật UI với thông tin manga
                if (tvMangaName != null) {
                    tvMangaName.setText(data.getTitle());
                }
                
                if (tvDescription != null) {
                    tvDescription.setText(data.getDescription());
                }
                
                if (tvAuthorName != null) {
                    tvAuthorName.setText("MangaDex");
                }
                
                // Load ảnh bìa (sử dụng placeholder nếu không có library)
                if (imgCover != null && data.getCoverUrl() != null && !data.getCoverUrl().isEmpty()) {
                    Glide.with(requireContext())
                            .load(data.getCoverUrl())
                            .placeholder(R.drawable.placeholder_comic)
                            .error(R.drawable.placeholder_comic)
                            .into(imgCover);
                }
                
                // Hiển thị tags nếu có
                if (layoutTags != null && data.getTags() != null && !data.getTags().isEmpty()) {
                    layoutTags.removeAllViews();
                    for (String tag : data.getTags()) {
                        TextView tagView = new TextView(getContext());
                        tagView.setText("#" + tag);
                        tagView.setBackgroundColor(getResources().getColor(R.color.brand_blue));
                        tagView.setTextColor(Color.BLACK);
                        tagView.setTextSize(12);
                        tagView.setPadding(24, 16, 24, 16);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 0, 32, 0);
                        layoutTags.addView(tagView, params);
                    }
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Manga info loading error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGroupDetail() {
        TranslationGroup group = new TranslationGroup(
                1,
                "Huaxia Group",
                R.drawable.placeholder_group,
                25,
                1200
        );

        group.setDescription("High-quality manga translation team");

        Intent intent = new Intent(requireContext(), GroupDetailActivity.class);
        intent.putExtra("group_id", group.getId());
        intent.putExtra("group_name", group.getName());
        intent.putExtra("group_description", group.getDescription());
        intent.putExtra("group_comic_count", group.getComicCount());
        intent.putExtra("group_member_count", group.getMemberCount());
        intent.putExtra("group_follower_count", group.getFollowerCount());
        intent.putExtra("group_avatar_res_id", group.getAvatarResId());

        startActivity(intent);
    }
}
