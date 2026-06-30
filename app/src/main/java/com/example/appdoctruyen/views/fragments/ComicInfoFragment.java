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
import com.example.appdoctruyen.views.activities.ComicDetailActivity;
import com.example.appdoctruyen.views.activities.ComicReadingActivity;
import com.example.appdoctruyen.views.activities.GroupDetailActivity;
import com.example.appdoctruyen.views.activities.LoginActivity;
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
        authManager = new AuthManager(requireContext());
        String userId = getCurrentUserId();
        if (!isBlank(userId)) {
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
            if (mangaId == null || mangaId.isEmpty()) {
                Toast.makeText(requireContext(), "Unable to save this manga.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!requireLogin("Please log in to add this manga to your bookshelf.")) return;
            String userId = getCurrentUserId();
        if (isBlank(userId)) {
            btnBookmark.setColorFilter(getResources().getColor(R.color.text_secondary_light, null));
            return;
        }
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
            if (mangaId == null || mangaId.isEmpty()) {
                Toast.makeText(requireContext(), "Unable to save this manga.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!requireLogin("Please log in to save this manga.")) return;
            String userId = getCurrentUserId();
        if (isBlank(userId)) {
            btnDownload.setColorFilter(getResources().getColor(R.color.text_secondary_light, null));
            return;
        }
        boolean isDownloaded = bookshelfDatabaseHelper.isDownloaded(userId, mangaId);
            String title = (mangaInfo != null && mangaInfo.getTitle() != null) ? mangaInfo.getTitle() : mangaTitle;
            String cover = (mangaInfo != null && mangaInfo.getCoverUrl() != null) ? mangaInfo.getCoverUrl() : "";

            if (isDownloaded) {
                bookshelfDatabaseHelper.removeDownloadedComic(userId, mangaId);
                if (firebaseHelper != null) {
                    firebaseHelper.removeBookmark(mangaId);
                }
                Toast.makeText(getContext(), "Removed from saved manga", Toast.LENGTH_SHORT).show();
                updateDownloadUI();
            } else {
                Toast.makeText(getContext(), "Saving manga...", Toast.LENGTH_SHORT).show();
                mangaRepository.getMangaChapters(mangaId, new MangaRepository.RepositoryCallback<List<Chapter>>() {
                    @Override
                    public void onSuccess(List<Chapter> chapters) {
                        if (!isAdded() || bookshelfDatabaseHelper == null) return;
                        if (chapters != null && !chapters.isEmpty()) {
                            Chapter targetChapter = chapters.get(0);
                            String chapterId = targetChapter.getChapterId();
                            String chapterName = targetChapter.getName();
                            if (isBlank(chapterId)) {
                                Toast.makeText(requireContext(), "This chapter is not available to save.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            
                            bookshelfDatabaseHelper.addDownloadedComic(userId, mangaId, chapterId, chapterName,
                                    "", title, cover);
                            if (firebaseHelper != null) {
                                firebaseHelper.addDownloadedComic(mangaId, chapterId, chapterName,
                                        "", title, cover);
                            }
                            Toast.makeText(getContext(), "Saved to Bookshelf (" + chapterName + ")", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "No chapters available to save.", Toast.LENGTH_SHORT).show();
                        }
                        updateDownloadUI();
                    }

                    @Override
                    public void onError(String message) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Unable to save this manga.", Toast.LENGTH_SHORT).show();
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
        if (isBlank(userId)) {
            btnBookmark.setColorFilter(getResources().getColor(R.color.text_secondary_light, null));
            return;
        }
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
        if (isBlank(userId)) {
            btnDownload.setColorFilter(getResources().getColor(R.color.text_secondary_light, null));
            return;
        }
        boolean isDownloaded = bookshelfDatabaseHelper.isDownloaded(userId, mangaId);
        if (isDownloaded) {
            btnDownload.setColorFilter(getResources().getColor(R.color.brand_blue, null));
        } else {
            btnDownload.setColorFilter(getResources().getColor(R.color.text_secondary_light, null));
        }
    }

    private String getCurrentUserId() {
        if (authManager == null || !authManager.isLoggedIn()) return null;
        return authManager.getCurrentUserId();
    }

    private boolean requireLogin(String message) {
        if (authManager == null) {
            authManager = new AuthManager(requireContext());
        }
        if (!authManager.isLoggedIn()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            return false;
        }
        return true;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void loadMangaInfo() {
        mangaRepository.getMangaDetail(mangaId, new MangaRepository.RepositoryCallback<Comic>() {
            @Override
            public void onSuccess(Comic data) {
                if (!isAdded() || getView() == null || data == null) return;
                mangaInfo = data;
                
                // Cập nhật UI với thông tin manga
                if (tvMangaName != null) {
                    tvMangaName.setText(data.getTitle());
                }

                if (tvDescription != null) {
                    tvDescription.setText(isBlank(data.getDescription()) ? "No description available." : data.getDescription());
                    tvDescription.setMaxLines(Integer.MAX_VALUE); // Hiển thị toàn bộ
                }

                // Ẩn nút "Đọc thêm" vì giờ đã hiện toàn bộ
                TextView tvReadMore = getView().findViewById(R.id.tvReadMore);
                if (tvReadMore != null) tvReadMore.setVisibility(View.GONE);

                // Cập nhật Lượt thích
                if (tvLikes != null) tvLikes.setText(String.valueOf(data.getLikes()));
                
                if (tvAuthorName != null) {
                    tvAuthorName.setText("MangaDex");
                }
                
                String coverUrl = data.getCoverUrl();
                if (getActivity() instanceof ComicDetailActivity) {
                    ((ComicDetailActivity) getActivity()).setCoverUrl(coverUrl);
                }

                if (imgCover != null) {
                    if (coverUrl != null && !coverUrl.isEmpty()) {
                        android.util.Log.d("MANGA_COVER", "Loading coverUrl: " + coverUrl);
                        Glide.with(requireContext())
                                .load(coverUrl)
                                .placeholder(R.drawable.placeholder_comic)
                                .error(R.drawable.placeholder_comic)
                                .into(imgCover);
                    } else {
                        android.util.Log.d("MANGA_COVER", "Empty coverUrl for mangaId: " + mangaId);
                        imgCover.setImageResource(R.drawable.placeholder_comic);
                    }
                }                // Hiển thị tags nếu có
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
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Unable to load manga information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGroupDetail() {
        if (!isAdded()) return;
        Toast.makeText(requireContext(), "Translation team details are not available for this manga.", Toast.LENGTH_SHORT).show();
    }
}
