package com.example.appdoctruyen.views.activities;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.models.TranslationGroup;
import com.example.appdoctruyen.views.adapters.BookshelfGroupAdapter;

public class GroupDetailActivity extends AppCompatActivity {

    private ImageView btnBack, imgAvatar;
    private TextView tvInitials;
    private TextView tvName, tvDescription, tvComicCount, tvMemberCount, tvFollowerCount;
    private MangaRepository mangaRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        btnBack = findViewById(R.id.btnBackGroupDetail);
        imgAvatar = findViewById(R.id.imgGroupDetailAvatar);
        tvInitials = findViewById(R.id.tvGroupDetailInitials);
        tvName = findViewById(R.id.tvGroupDetailName);
        tvDescription = findViewById(R.id.tvGroupDetailDescription);
        tvComicCount = findViewById(R.id.tvGroupDetailComicCount);
        tvMemberCount = findViewById(R.id.tvGroupDetailMemberCount);
        tvFollowerCount = findViewById(R.id.tvGroupDetailFollowerCount);

        mangaRepository = new MangaRepository();

        // Hiển thị data từ intent trước (instant)
        String name = getIntent().getStringExtra("group_name");
        String description = getIntent().getStringExtra("group_description");
        int comicCount = getIntent().getIntExtra("group_comic_count", 0);
        int memberCount = getIntent().getIntExtra("group_member_count", 0);
        int followerCount = getIntent().getIntExtra("group_follower_count", 0);
        String groupId = getIntent().getStringExtra("group_id");

        tvName.setText(!isBlank(name) ? name : getString(R.string.group_default_name));
        tvDescription.setText(!isBlank(description) ? description : getString(R.string.group_default_description));
        tvComicCount.setText(formatCount(comicCount));
        tvMemberCount.setText(formatCount(memberCount));
        tvFollowerCount.setText(formatCount(followerCount));

        // Initials avatar
        BookshelfGroupAdapter.bindInitialsAvatar(imgAvatar, tvInitials,
                !isBlank(name) ? name : "T", !isBlank(groupId) ? groupId : "default");

        // Nếu có groupId (UUID) → gọi API lấy data real-time
        if (!isBlank(groupId)) {
            loadGroupFromApi(groupId);
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadGroupFromApi(String groupId) {
        mangaRepository.getGroupDetail(groupId, new MangaRepository.RepositoryCallback<TranslationGroup>() {
            @Override
            public void onSuccess(TranslationGroup group) {
                if (isFinishing() || isDestroyed() || group == null) return;
                // Cập nhật UI với data mới từ API
                tvName.setText(!isBlank(group.getName()) ? group.getName() : getString(R.string.group_default_name));
                if (group.getDescription() != null && !group.getDescription().isEmpty()) {
                    tvDescription.setText(group.getDescription());
                }
                tvComicCount.setText(formatCount(group.getComicCount()));
                tvMemberCount.setText(formatCount(group.getMemberCount()));
                tvFollowerCount.setText(formatCount(group.getFollowerCount()));

                // Update initials avatar with fresh data
                BookshelfGroupAdapter.bindInitialsAvatar(imgAvatar, tvInitials,
                        group.getName(), group.getGroupId());
            }

            @Override
            public void onError(String message) {
                // Giữ data từ intent, không cần báo lỗi
            }
        });
    }

    private String formatCount(int value) {
        return value > 0 ? String.valueOf(value) : getString(R.string.group_stats_unavailable_short);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
