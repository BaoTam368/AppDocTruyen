package com.example.appdoctruyen;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GroupDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        ImageView btnBack = findViewById(R.id.btnBackGroupDetail);
        ImageView imgAvatar = findViewById(R.id.imgGroupDetailAvatar);
        TextView tvName = findViewById(R.id.tvGroupDetailName);
        TextView tvDescription = findViewById(R.id.tvGroupDetailDescription);
        TextView tvComicCount = findViewById(R.id.tvGroupDetailComicCount);
        TextView tvMemberCount = findViewById(R.id.tvGroupDetailMemberCount);
        TextView tvFollowerCount = findViewById(R.id.tvGroupDetailFollowerCount);

        // Nhận dữ liệu nhóm dịch từ màn danh sách và hiển thị lên giao diện chi tiết
        String name = getIntent().getStringExtra("group_name");
        String description = getIntent().getStringExtra("group_description");
        int comicCount = getIntent().getIntExtra("group_comic_count", 0);
        int memberCount = getIntent().getIntExtra("group_member_count", 0);
        int followerCount = getIntent().getIntExtra("group_follower_count", 0);
        int avatarResId = getIntent().getIntExtra("group_avatar_res_id", 0);

        tvName.setText(name != null ? name : getString(R.string.group_default_name));
        tvDescription.setText(description != null ? description : getString(R.string.group_default_description));
        tvComicCount.setText(String.valueOf(comicCount));
        tvMemberCount.setText(String.valueOf(memberCount));
        tvFollowerCount.setText(String.valueOf(followerCount));

        if (avatarResId != 0) {
            imgAvatar.setImageResource(avatarResId);
        } else {
            imgAvatar.setImageResource(R.drawable.placeholder_group);
        }

        btnBack.setOnClickListener(v -> finish());
    }
}