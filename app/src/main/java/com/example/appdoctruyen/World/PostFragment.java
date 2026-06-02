package com.example.appdoctruyen.World;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Post;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_world_feed, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPost);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        postList = new ArrayList<>();
        postList.add(new Post("Sơn Tùng M-TP", "2 giờ trước", "Nắng ấm xa dần rồi...",
                "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg",
                "https://i.pinimg.com/originals/c9/4c/bb/c94cbbf16b3fcf58be79d50c95029e9f.jpg", 15000, 3200));
        postList.add(new Post("Sơn Tùng M-TP", "2 giờ trước", "Nắng ấm xa dần rồi...",
                "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg",
                "https://i.pinimg.com/originals/c9/4c/bb/c94cbbf16b3fcf58be79d50c95029e9f.jpg", 15000, 3200));

        postAdapter = new PostAdapter(requireContext(), postList);
        recyclerView.setAdapter(postAdapter);
        return view;
    }
}
