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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.TranslationGroup;
import com.example.appdoctruyen.views.activities.ComicReadingActivity;
import com.example.appdoctruyen.views.activities.GroupDetailActivity;
import com.example.appdoctruyen.views.activities.MainActivity;
import com.example.appdoctruyen.views.activities.NotificationActivity;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.models.Comic;

public class ComicInfoFragment extends Fragment {
    private static final String ARG_MANGA_ID = "mangaId";
    private static final String ARG_MANGA_TITLE = "mangaTitle";

    private ImageView imgCover, imgAuthorAvatar, btnBookmark, btnShare, btnDownload;
    private TextView tvMangaName, tvAuthorName, tvDescription, tvViews, tvLikes, tvReadMore;
    private LinearLayout layoutTags;
    private ConstraintLayout layoutRating;
    private String mangaId;
    private String mangaTitle;
    private MangaRepository mangaRepository;
    private Comic mangaInfo;
    private boolean isDescriptionExpanded = false;
    private com.example.appdoctruyen.data.database.BookshelfDatabaseHelper bookshelfDatabaseHelper;
    private com.example.appdoctruyen.data.firebase.AuthManager authManager;
    private com.example.appdoctruyen.data.firebase.BookshelfFirebaseHelper firebaseHelper;

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
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic_info, container, false);

        try {
            imgCover = view.findViewById(R.id.imgCover);
            imgAuthorAvatar = view.findViewById(R.id.imgAuthorAvatar);
            btnBookmark = view.findViewById(R.id.btnBookmark);
            btnShare = view.findViewById(R.id.btnShare);
            btnDownload = view.findViewById(R.id.btnDownload);
            tvMangaName = view.findViewById(R.id.tvMangaName);
            tvAuthorName = view.findViewById(R.id.tvAuthorName);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvViews = view.findViewById(R.id.tvViews);
            tvLikes = view.findViewById(R.id.tvLikes);
            tvReadMore = view.findViewById(R.id.tvReadMore);
            layoutTags = view.findViewById(R.id.layoutTags);
    //        layoutRating = view.findViewById(R.id.layoutRating);

            bookshelfDatabaseHelper = new com.example.appdoctruyen.data.database.BookshelfDatabaseHelper(requireContext());
            authManager = new com.example.appdoctruyen.data.firebase.AuthManager();
            String userId = getCurrentUserId();
            if (userId != null && !userId.equals("local_user")) {
                firebaseHelper = new com.example.appdoctruyen.data.firebase.BookshelfFirebaseHelper(userId);
            }

            // Load manga info từ API
            if (mangaId != null && !mangaId.isEmpty()) {
                loadMangaInfo();
                checkBookmarkStatus();
            } else {
                Toast.makeText(getContext(), "Không tìm thấy ID truyện", Toast.LENGTH_SHORT).show();
            }

            if (tvAuthorName != null) {
                tvAuthorName.setOnClickListener(v -> {
                    openGroupDetail();
                });
            }

            if (imgAuthorAvatar != null) {
                imgAuthorAvatar.setOnClickListener(v -> {
                    openGroupDetail();
                });
            }

            if (tvReadMore != null) {
                tvReadMore.setOnClickListener(v -> {
                    isDescriptionExpanded = !isDescriptionExpanded;
                    if (isDescriptionExpanded) {
                        tvDescription.setMaxLines(Integer.MAX_VALUE);
                        tvReadMore.setText("Thu gọn");
                    } else {
                        tvDescription.setMaxLines(3);
                        tvReadMore.setText("Đọc thêm");
                    }
                });
            }

            // Add click handlers for action icons
            if (btnBookmark != null) {
                btnBookmark.setOnClickListener(v -> {
                    if (mangaId == null || mangaId.isEmpty()) {
                        Toast.makeText(getContext(), "Không có ID truyện", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    String curUserId = getCurrentUserId();
                    boolean isCurrentlyBookmarked = "bookmarked".equals(btnBookmark.getTag());
                    
                    String title = mangaInfo != null ? mangaInfo.getTitle() : mangaTitle;
                    String coverUrl = mangaInfo != null ? mangaInfo.getCoverUrl() : null;
                    String latestChapter = mangaInfo != null ? mangaInfo.getLatestChapter() : null;
                    
                    if (isCurrentlyBookmarked) {
                        // Hủy bookmark
                        bookshelfDatabaseHelper.removeBookmark(curUserId, mangaId);
                        if (firebaseHelper != null) {
                            firebaseHelper.removeBookmark(mangaId);
                        }
                        updateBookmarkIcon(false);
                        Toast.makeText(getContext(), "Đã xóa khỏi tủ sách", Toast.LENGTH_SHORT).show();
                    } else {
                        // Thêm bookmark
                        bookshelfDatabaseHelper.addBookmark(curUserId, mangaId, title, coverUrl);
                        if (firebaseHelper != null) {
                            firebaseHelper.addBookmark(mangaId, title, coverUrl, latestChapter);
                        }
                        updateBookmarkIcon(true);
                        Toast.makeText(getContext(), "Đã lưu vào tủ sách", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (btnShare != null) {
                btnShare.setOnClickListener(v -> {
                    if (mangaInfo != null) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Đọc truyện: " + mangaInfo.getTitle());
                        startActivity(Intent.createChooser(shareIntent, "Chia sẻ truyện"));
                    }
                });
            }

            if (btnDownload != null) {
                btnDownload.setOnClickListener(v -> {
                    if (mangaId == null || mangaId.isEmpty()) {
                        Toast.makeText(getContext(), "Không có ID truyện", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String curUserId = getCurrentUserId();
                    String title = mangaInfo != null ? mangaInfo.getTitle() : mangaTitle;
                    String coverUrl = mangaInfo != null ? mangaInfo.getCoverUrl() : null;
                    String latestChapter = mangaInfo != null ? mangaInfo.getLatestChapter() : "Chapter 1";
                    
                    // Giả lập lưu truyện tải xuống
                    bookshelfDatabaseHelper.addDownloadedComic(curUserId, mangaId, "demo-chap", latestChapter, "/storage/emulated/0/Download/comic.zip", title, coverUrl);
                    if (firebaseHelper != null) {
                        firebaseHelper.addDownloadedComic(mangaId, "demo-chap", latestChapter, "/storage/emulated/0/Download/comic.zip", title, coverUrl);
                    }
                    Toast.makeText(getContext(), "Đã tải xuống " + title, Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            android.util.Log.e("ComicInfoFragment", "Error initializing views: " + e.getMessage());
            Toast.makeText(getContext(), "Lỗi khởi tạo giao diện", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadMangaInfo() {
        android.util.Log.d("CHECK_API", "Gửi API chi tiết với ID: " + mangaId);

        mangaRepository.getMangaDetail(mangaId, new MangaRepository.RepositoryCallback<Comic>() {
            @Override

            public void onSuccess(Comic data) {
                if (data == null) {
                    android.util.Log.e("CHECK_API", "Dữ liệu trả về bị NULL!");
                    return;
                }
                mangaInfo = data;
                checkBookmarkStatus();
                android.util.Log.d("CHECK_API", "Title: " + data.getTitle() + " | Desc: " + data.getDescription());
                // Cập nhật UI với thông tin manga
                if (tvMangaName != null) {
                    tvMangaName.setText(data.getTitle());
                }

                if (tvDescription != null) {
                    tvDescription.setText(data.getDescription());
                }

                if (tvAuthorName != null) {
                    String groupName = data.getTranslationGroupName();
                    if (groupName != null && !groupName.isEmpty()) {
                        tvAuthorName.setText(groupName);
                    } else {
                        tvAuthorName.setText(data.getAuthor() != null ? data.getAuthor() : "Không có thông tin");
                    }
                }

                // Load ảnh bìa (sử dụng placeholder nếu không có library)
                if (imgCover != null && data.getCoverUrl() != null && !data.getCoverUrl().isEmpty()) {
                    com.bumptech.glide.Glide.with(ComicInfoFragment.this).load(data.getCoverUrl()).into(imgCover);
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
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 32, 0);
                        layoutTags.addView(tagView, params);
                    }
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Lỗi tải thông tin: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGroupDetail() {
        if (mangaInfo == null) {
            Toast.makeText(getContext(), "Chưa có thông tin truyện", Toast.LENGTH_SHORT).show();
            return;
        }

        String groupId = mangaInfo.getTranslationGroupId();
        String groupName = mangaInfo.getTranslationGroupName();

        if (groupId != null && !groupId.isEmpty()) {
            // Có groupId → gọi API lấy chi tiết nhóm dịch
            mangaRepository.getGroupDetail(groupId, new MangaRepository.RepositoryCallback<TranslationGroup>() {
                @Override
                public void onSuccess(TranslationGroup group) {
                    if (!isAdded() || getContext() == null) return;
                    launchGroupDetailActivity(group);
                }

                @Override
                public void onError(String message) {
                    if (!isAdded() || getContext() == null) return;
                    // API lỗi → fallback với data có sẵn
                    TranslationGroup fallback = new TranslationGroup(
                            0,
                            groupName != null ? groupName : "Nhóm dịch",
                            "Không có mô tả",
                            R.drawable.placeholder_group,
                            0, 0, 0
                    );
                    fallback.setGroupId(groupId);
                    launchGroupDetailActivity(fallback);
                }
            });
        } else if (groupName != null && !groupName.isEmpty()) {
            // Không có groupId nhưng có tên → mở với data cơ bản
            TranslationGroup fallback = new TranslationGroup(
                    0,
                    groupName,
                    "Không có mô tả",
                    R.drawable.placeholder_group,
                    0, 0, 0
            );
            launchGroupDetailActivity(fallback);
        } else {
            Toast.makeText(getContext(), "Không có thông tin nhóm dịch", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchGroupDetailActivity(TranslationGroup group) {
        Intent intent = new Intent(requireContext(), GroupDetailActivity.class);
        intent.putExtra("group_id", group.getGroupId());
        intent.putExtra("group_name", group.getName());
        intent.putExtra("group_description",
                group.getDescription() != null ? group.getDescription() : "Không có mô tả");
        intent.putExtra("group_comic_count", group.getComicCount());
        intent.putExtra("group_member_count", group.getMemberCount());
        intent.putExtra("group_follower_count", group.getFollowerCount());
        intent.putExtra("group_avatar_res_id", group.getAvatarResId());
        startActivity(intent);
    }

    private String getCurrentUserId() {
        if (authManager == null) return "local_user";
        String userId = authManager.getCurrentUserId();
        return userId != null ? userId : "local_user";
    }

    private void checkBookmarkStatus() {
        if (mangaId == null || mangaId.isEmpty()) return;
        
        String userId = getCurrentUserId();
        
        // 1. Kiểm tra SQLite local trước
        boolean localBookmarked = bookshelfDatabaseHelper.isBookmarked(userId, mangaId);
        updateBookmarkIcon(localBookmarked);
        
        // 2. Nếu đã đăng nhập Firebase, đồng bộ trạng thái từ Firebase về SQLite local nếu có lệch
        if (firebaseHelper != null) {
            firebaseHelper.getBookmarks(new com.example.appdoctruyen.data.firebase.BookshelfFirebaseHelper.BookshelfCallback() {
                @Override
                public void onSuccess(java.util.List<Comic> comics) {
                    if (!isAdded()) return;
                    boolean remoteBookmarked = false;
                    for (Comic c : comics) {
                        if (mangaId.equals(c.getMangaId())) {
                            remoteBookmarked = true;
                            break;
                        }
                    }
                    if (remoteBookmarked && !localBookmarked) {
                        String title = mangaInfo != null ? mangaInfo.getTitle() : mangaTitle;
                        String coverUrl = mangaInfo != null ? mangaInfo.getCoverUrl() : null;
                        bookshelfDatabaseHelper.addBookmark(userId, mangaId, title, coverUrl);
                        updateBookmarkIcon(true);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {}
            });
        }
    }

    private void updateBookmarkIcon(boolean isBookmarked) {
        if (btnBookmark != null && isAdded()) {
            if (isBookmarked) {
                btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                btnBookmark.setTag("bookmarked");
            } else {
                btnBookmark.setImageResource(R.drawable.ic_bookmark);
                btnBookmark.setTag("unbookmarked");
            }
        }
    }
}
