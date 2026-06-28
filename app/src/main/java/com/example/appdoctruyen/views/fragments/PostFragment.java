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

        postList.add(new Post("Alex Tran", "10 minutes ago", "Does anyone know how many love interests the main character has in Renegade Immortal? I heard the ending is rough.", "https://picsum.photos", "https://picsum.photos", 15000, 3200));
        postList.add(new Post("Han Li", "2 hours ago", "The translation team is working hard. I just donated 500 coins to support more chapters of A Record of a Mortal's Journey to Immortality.", "https://picsum.photos", "https://picsum.photos", 15000, 3200));
        postList.add(new Post("Bai Xiaochun", "Yesterday", "Discussion: which cultivation system feels stronger, Demon's Diary or Pursuit of the Truth?", "https://picsum.photos", "https://picsum.photos", 15000, 3200));
        postList.add(new Post("Mason Jiang", "A day ago", "Recruiting members for a scanlation team. Basic Photoshop skill required. DM me if interested.", "https://picsum.photos", "https://picsum.photos", 15000, 32002));

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
