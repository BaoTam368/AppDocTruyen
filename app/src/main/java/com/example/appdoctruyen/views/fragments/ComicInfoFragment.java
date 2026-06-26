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

    private ImageView imgCover, imgAuthorAvatar;
    private TextView tvMangaName, tvAuthorName, tvDescription, tvViews, tvLikes, tvReadMore;
    private LinearLayout layoutTags;
    private ConstraintLayout layoutRating;
    private String mangaId;
    private String mangaTitle;
    private MangaRepository mangaRepository;
    private Comic mangaInfo;
    private boolean isDescriptionExpanded = false;

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
            tvMangaName = view.findViewById(R.id.tvMangaName);
            tvAuthorName = view.findViewById(R.id.tvAuthorName);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvViews = view.findViewById(R.id.tvViews);
            tvLikes = view.findViewById(R.id.tvLikes);
            tvReadMore = view.findViewById(R.id.tvReadMore);
            layoutTags = view.findViewById(R.id.layoutTags);
    //        layoutRating = view.findViewById(R.id.layoutRating);

            // Load manga info từ API
            if (mangaId != null && !mangaId.isEmpty()) {
                loadMangaInfo();
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
                android.util.Log.d("CHECK_API", "Title: " + data.getTitle() + " | Desc: " + data.getDescription());
                // Cập nhật UI với thông tin manga
                if (tvMangaName != null) {
                    tvMangaName.setText(data.getTitle());
                }

                if (tvDescription != null) {
                    tvDescription.setText(data.getDescription());
                }

                if (tvAuthorName != null) {
                    tvAuthorName.setText(data.getAuthor() != null ? data.getAuthor() : "Không có thông tin");
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
        TranslationGroup group = new TranslationGroup(1, "Hoa Hạ Group", R.drawable.placeholder_group, 25, 1200);

        group.setDescription("Nhóm dịch truyện tranh chất lượng cao");

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
