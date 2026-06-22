package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class ComicInfoFragment extends Fragment {
    private TextView tvAuthorName;
    private ImageView imgAuthorAvatar ;
    private ConstraintLayout tvRating;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic_info, container, false);

        tvAuthorName = view.findViewById(R.id.tvAuthorName);
        imgAuthorAvatar = view.findViewById(R.id.imgAuthorAvatar);
        tvRating = view.findViewById(R.id.tvRating);

        tvRating.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("open_tab", "world");
            intent.putExtra("world_page", "comment");
            startActivity(intent);
        });

        tvAuthorName.setOnClickListener(v -> {
            openGroupDetail();
        });

        imgAuthorAvatar.setOnClickListener(v -> {
            openGroupDetail();
        });

        return view;
    }

    private void openGroupDetail() {
        TranslationGroup group = new TranslationGroup(
                1,
                "Hoa Hạ Group",
                R.drawable.placeholder_group,
                25,
                1200
        );

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
