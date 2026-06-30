package com.example.appdoctruyen.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.api.LikeResponse;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.models.Post;
import com.example.appdoctruyen.views.activities.CreatePostActivity;
import com.example.appdoctruyen.views.activities.LoginActivity;
import com.example.appdoctruyen.views.adapters.PostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    private RecyclerView rvPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FloatingActionButton fabAddPost;
    private AuthManager authManager;
    private MangaRepository repository;

    private ActivityResultLauncher<Intent> createPostLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new MangaRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_world_feed, container, false);

        rvPosts = view.findViewById(R.id.recyclerViewPost);
        fabAddPost = view.findViewById(R.id.fab_add_post);
        authManager = new AuthManager(requireContext());

        setupRecycler();
        setupLauncher();
        loadPosts();

        fabAddPost.setOnClickListener(v -> {
            if (!requireLogin("Please log in to use this feature.")) return;
            Intent intent = new Intent(requireContext(), CreatePostActivity.class);
            createPostLauncher.launch(intent);
        });

        return view;
    }

    private void setupRecycler() {
        postList = new ArrayList<>();

        rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPosts.setNestedScrollingEnabled(false);

        postAdapter = new PostAdapter(requireContext(), postList, (post, position, tvLikeCount) -> {
            if (post == null) return;
            String currentUserId = authManager != null ? authManager.getCurrentUserId() : null;

            if (currentUserId == null || currentUserId.trim().isEmpty()) {
                requireLogin("Please log in to use this feature.");
                return;
            }

            repository.toggleLikePost(post.getId(), currentUserId, new MangaRepository.RepositoryCallback<LikeResponse>() {
                @Override
                public void onSuccess(LikeResponse result) {
                    if (!isAdded() || getActivity() == null) return;

                    post.setLikeCount(result.getLikeCount());
                    tvLikeCount.setText(String.valueOf(result.getLikeCount()));
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Interaction error: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });
        rvPosts.setAdapter(postAdapter);
    }

    private void setupLauncher() {
        createPostLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (isAdded() && result.getResultCode() == android.app.Activity.RESULT_OK) {
                        loadPosts();
                    }
                }
        );
    }

    private boolean requireLogin(String message) {
        if (!isAdded()) return false;
        if (authManager == null) {
            authManager = new AuthManager(requireContext());
        }
        if (!authManager.isLoggedIn()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            return false;
        }
        return true;
    }

    private void loadPosts() {
        repository.getPosts(new MangaRepository.RepositoryCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> data) {
                if (!isAdded() || getActivity() == null) return;

                postList.clear();
                if (data != null) {
                    postList.addAll(data);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}