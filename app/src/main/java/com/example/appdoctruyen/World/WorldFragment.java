package com.example.appdoctruyen.World;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appdoctruyen.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class WorldFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private FloatingActionButton fabAddPost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);

        tabLayout = view.findViewById(R.id.tabLayout_world);
        viewPager2 = view.findViewById(R.id.viewPager_world);
        fabAddPost = view.findViewById(R.id.fab_add_post);
        WorldPagerAdapter adapter = new WorldPagerAdapter(this);
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Bài viết");
                        break;
                    case 1:
                        tab.setText("Bình luận");
                        break;
                    case 2:
                        tab.setText("Nhóm Dịch");
                        break;
                }
            }
        }).attach();

        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Đăng lên bảng tin bai viet", "Đăng lên bảng tin nhóm dịch"};
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Bạn muốn đăng bài ở đâu?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Mở trang CreatePostActivity và truyền tín hiệu "CÁ NHÂN"
                            android.content.Intent intent = new android.content.Intent(requireContext(), CreatePostActivity.class);
                            intent.putExtra("POST_TYPE", "PERSONAL");
                            startActivity(intent);

                        } else if (which == 1) {
                            // Mở trang CreatePostActivity và truyền tín hiệu "NHÓM"
                            android.content.Intent intent = new android.content.Intent(requireContext(), CreatePostActivity.class);
                            intent.putExtra("POST_TYPE", "GROUP");
                            startActivity(intent);
                        }
                    }
                });
                builder.show();
            }
        });

        return view;
    }
}