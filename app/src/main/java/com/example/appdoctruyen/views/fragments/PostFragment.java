package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Post;
import com.example.appdoctruyen.views.adapters.PostAdapter;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {
    //    private RecyclerView recyclerView;
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_world_feed, container, false);

        rvPosts = view.findViewById(R.id.recyclerViewPost);
        setupPostList();

//        recyclerView = view.findViewById(R.id.recyclerViewPost);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        postList = new ArrayList<>();
//        postList.add(new Post("Sơn Tùng M-TP", "2 giờ trước", "Nắng ấm xa dần rồi...",
//                "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg",
//                "https://i.pinimg.com/originals/c9/4c/bb/c94cbbf16b3fcf58be79d50c95029e9f.jpg", 15000, 3200));
//        postList.add(new Post("Sơn Tùng M-TP", "2 giờ trước", "Nắng ấm xa dần rồi...",
//                "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg",
//                "https://i.pinimg.com/originals/c9/4c/bb/c94cbbf16b3fcf58be79d50c95029e9f.jpg", 15000, 3200));
//
//        postAdapter = new PostAdapter(requireContext(), postList);
//        recyclerView.setAdapter(postAdapter);
        return view;
    }

    private void setupPostList() {
        postList = new ArrayList<>();

        postList.add(new Post("Lục Thao", "10 phút trước", "Mọi người ơi cho mình hỏi bộ truyện Tiên Nghịch này main có mấy vợ thế? Nghe bảo kết thảm lắm phải không?", "https://picsum.photos", "https://picsum.photos", 15000, 3200));
        postList.add(new Post("Hàn Lập", "2 giờ trước", "Nhóm dịch làm việc năng nổ quá, vừa ủng hộ nhóm 500 xu để dịch tiếp bộ Phàm Nhân Tu Tiên nhé, cố lên anh em!", "https://picsum.photos", "https://picsum.photos", 15000, 3200));
        postList.add(new Post("Bạch Tiểu Thuần", " Hôm qua", "Góc thảo luận: Theo các đạo hữu thì hệ thống tu vi bên truyện Chấp Ma và Cầu Ma bên nào có sức mạnh bá đạo hơn?", "https://picsum.photos", "https://picsum.photos", 15000, 3200));
        postList.add(new Post("Khương Tử Nha", "ngày trước", "Tuyển thành viên vào nhóm dịch truyện chữ sang truyện tranh, yêu cầu biết Photoshop cơ bản, ai quan tâm ib mình.", "https://picsum.photos", "https://picsum.photos", 15000, 32002));

        rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));

        rvPosts.setNestedScrollingEnabled(false);

        postAdapter = new PostAdapter(requireContext(), postList, (post, position) -> {
        });

        rvPosts.setAdapter(postAdapter);
    }

    public void addPost(Post post) {
        postList.add(0, post);
        postAdapter.notifyItemInserted(0);
        rvPosts.scrollToPosition(0);
    }
}
