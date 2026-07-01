package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.views.activities.ComicDetailActivity;
import com.example.appdoctruyen.views.activities.LoginActivity;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.data.database.BookshelfDatabaseHelper;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.data.firebase.BookshelfFirebaseHelper;
import com.example.appdoctruyen.models.Chapter;
import com.example.appdoctruyen.models.Comic;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.List;

public class ComicInfoFragment extends Fragment {
    private static final String IMAGE_USER_AGENT = "AppDocTruyenAndroid/1.0";
    private static final String ARG_MANGA_ID = "mangaId";
    private static final String ARG_MANGA_TITLE = "mangaTitle";
    
    private ImageView imgCover;
    private ImageView btnBookmark, btnDownload;
    private TextView tvMangaName, tvDescription;
    private ChipGroup chipGroupTags;
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
        tvMangaName = view.findViewById(R.id.tvMangaName);
        tvDescription = view.findViewById(R.id.tvDescription);
        chipGroupTags = view.findViewById(R.id.chipGroupTags);
        
        btnBookmark = view.findViewById(R.id.btnBookmark);
        btnDownload = view.findViewById(R.id.btnDownload);

        // Load manga info từ API
        if (mangaId != null && !mangaId.isEmpty()) {
            loadMangaInfo();
        }

        updateBookmarkUI();
        updateDownloadUI();

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
                    tvDescription.setMaxLines(Integer.MAX_VALUE);
                    tvDescription.setEllipsize(null);
                }

                // Ẩn nút "Đọc thêm" vì giờ đã hiện toàn bộ
                TextView tvReadMore = getView().findViewById(R.id.tvReadMore);
                if (tvReadMore != null) tvReadMore.setVisibility(View.GONE);
                
                String coverUrl = data.getCoverUrl() != null ? data.getCoverUrl().trim() : "";
                if (getActivity() instanceof ComicDetailActivity) {
                    ((ComicDetailActivity) getActivity()).setCoverUrl(coverUrl);
                }

                if (imgCover != null) {
                    if (!coverUrl.isEmpty()) {
                        android.util.Log.d("MANGA_COVER", "Loading coverUrl: " + coverUrl);
                        Glide.with(requireContext())
                                .load(buildImageRequest(coverUrl))
                                .placeholder(R.drawable.placeholder_comic)
                                .error(R.drawable.placeholder_comic)
                                .centerCrop()
                                .into(imgCover);
                    } else {
                        android.util.Log.d("MANGA_COVER", "Empty coverUrl for mangaId: " + mangaId);
                        imgCover.setImageResource(R.drawable.placeholder_comic);
                    }
                }

                // Hiển thị tags nếu có
                if (chipGroupTags != null && data.getTags() != null && !data.getTags().isEmpty()) {
                    chipGroupTags.removeAllViews();
                    for (String tag : data.getTags()) {
                        Chip chip = new Chip(requireContext());
                        chip.setText(tag);
                        chip.setChipBackgroundColorResource(R.color.brand_blue);
                        chip.setTextColor(getResources().getColor(R.color.white, null));
                        chip.setTextSize(11);
                        chip.setChipMinHeight(0);
                        chip.setMinHeight(0);
                        chip.setChipStartPadding(8);
                        chip.setChipEndPadding(8);
                        chip.setPadding(0, 0, 0, 0);
                        chip.setClickable(false);
                        chip.setCheckable(false);
                        chip.setEnsureMinTouchTargetSize(false);
                        chipGroupTags.addView(chip);
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

    private GlideUrl buildImageRequest(String coverUrl) {
        return new GlideUrl(coverUrl, new LazyHeaders.Builder()
                .addHeader("User-Agent", IMAGE_USER_AGENT)
                .build());
    }
}
