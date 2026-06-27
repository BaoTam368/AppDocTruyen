package com.example.appdoctruyen.views.activities;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.models.TranslationGroup;

public class GroupDetailActivity extends AppCompatActivity {

    private ImageView btnBack, imgAvatar;
    private TextView tvName, tvDescription, tvComicCount, tvMemberCount, tvFollowerCount;
    private MangaRepository mangaRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        btnBack = findViewById(R.id.btnBackGroupDetail);
        imgAvatar = findViewById(R.id.imgGroupDetailAvatar);
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
        int avatarResId = getIntent().getIntExtra("group_avatar_res_id", 0);

        tvName.setText(!isBlank(name) ? name : getString(R.string.group_default_name));
        tvDescription.setText(!isBlank(description) ? description : getString(R.string.group_default_description));
        tvComicCount.setText(String.valueOf(comicCount));
        tvMemberCount.setText(String.valueOf(memberCount));
        tvFollowerCount.setText(String.valueOf(followerCount));

        if (avatarResId != 0) {
            imgAvatar.setImageResource(avatarResId);
        } else {
            imgAvatar.setImageResource(R.drawable.placeholder_group);
        }

        // Nếu có groupId (UUID) → gọi API lấy data real-time
        String groupId = getIntent().getStringExtra("group_id");
        if (groupId != null && !groupId.isEmpty()) {
            loadGroupFromApi(groupId);
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadGroupFromApi(String groupId) {
        mangaRepository.getGroupDetail(groupId, new MangaRepository.RepositoryCallback<TranslationGroup>() {
            @Override
            public void onSuccess(TranslationGroup group) {
                if (isFinishing() || isDestroyed()) return;
                // Cập nhật UI với data mới từ API
                tvName.setText(group.getName());
                if (group.getDescription() != null && !group.getDescription().isEmpty()) {
                    tvDescription.setText(group.getDescription());
                }
                tvComicCount.setText(String.valueOf(group.getComicCount()));
                tvMemberCount.setText(String.valueOf(group.getMemberCount()));
                tvFollowerCount.setText(String.valueOf(group.getFollowerCount()));
            }

            @Override
            public void onError(String message) {
                // Giữ data từ intent, không cần báo lỗi
            }
        });
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
