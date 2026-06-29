package com.example.appdoctruyen.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.api.CreateCommentRequest;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.models.Comment;
import com.example.appdoctruyen.views.adapters.CommentAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment {

    private static final String ARG_MANGA_ID = "mangaId";
    private static final String ARG_CHAPTER_ID = "chapterId";

    private RecyclerView rvComments;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private EditText edtComment;
    private ImageButton btnSendComment;
    private TextView tvCommentState;

    private MangaRepository repository;
    private AuthManager authManager;
    private String mangaId;
    private String chapterId;

    public static CommentFragment newInstance(String mangaId, String chapterId) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MANGA_ID, mangaId);
        args.putString(ARG_CHAPTER_ID, chapterId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new MangaRepository();
        if (getArguments() != null) {
            mangaId = getArguments().getString(ARG_MANGA_ID);
            chapterId = getArguments().getString(ARG_CHAPTER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_world_comment, container, false);

        rvComments = view.findViewById(R.id.recyclerViewComment);
        edtComment = view.findViewById(R.id.edt_comment);
        btnSendComment = view.findViewById(R.id.btn_send_comment);
        tvCommentState = view.findViewById(R.id.tv_comment_state);

        authManager = new AuthManager(requireContext());

        setupCommentList();
        setupSendButton();
        loadComments();

        return view;
    }

    private void setupCommentList() {
        commentList = new ArrayList<>();
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));

        commentAdapter = new CommentAdapter(requireContext(), commentList, (comment, position) -> {
            if (comment.getUserId() != null && comment.getUserId().equals(authManager.getCurrentUserId())) {
                showDeleteDialog(comment.getId(), position);
            }
        });

        rvComments.setAdapter(commentAdapter);
    }

    private void setupSendButton() {
        btnSendComment.setOnClickListener(v -> {
            String content = edtComment.getText().toString().trim();
            if (content.isEmpty()) {
                edtComment.setError("Write a comment");
                return;
            }

            String currentUserId = authManager.getCurrentUserId();
            if (currentUserId == null) {
                Toast.makeText(requireContext(), "Please log in to comment", Toast.LENGTH_SHORT).show();
                return;
            }

            CreateCommentRequest request = new CreateCommentRequest(mangaId, chapterId, currentUserId, content);

            repository.createComment(request, new MangaRepository.RepositoryCallback<Comment>() {
                @Override
                public void onSuccess(Comment newComment) {
                    if (!isAdded() || getActivity() == null) return;

                    addComment(newComment);
                    edtComment.setText("");
                    Toast.makeText(requireContext(), "Comment posted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) return;
                    Log.e("COMMENT_FRAGMENT", "Create comment failed: " + message);
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadComments() {
        showState("Loading comments...");
        repository.getComments(mangaId, chapterId, new MangaRepository.RepositoryCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
                if (!isAdded() || getActivity() == null) return;

                commentList.clear();
                if (data != null) {
                    commentList.addAll(data);
                }
                commentAdapter.notifyDataSetChanged();

                if (commentList.isEmpty()) {
                    showState("No comments yet.");
                } else {
                    hideState();
                }
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Log.e("COMMENT_FRAGMENT", "Load comments failed: " + message);
                commentList.clear();
                commentAdapter.notifyDataSetChanged();
                showState("Unable to load comments.");
                Toast.makeText(requireContext(), "Unable to load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addComment(Comment comment) {
        commentList.add(0, comment);
        commentAdapter.notifyItemInserted(0);
        rvComments.scrollToPosition(0);
        hideState();
    }

    private void showDeleteDialog(int commentId, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    repository.deleteComment(commentId, new MangaRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            commentList.remove(position);
                            commentAdapter.notifyItemRemoved(position);
                            if (commentList.isEmpty()) {
                                showState("No comments yet.");
                            }
                            Toast.makeText(requireContext(), "Comment deleted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showState(String message) {
        if (tvCommentState == null) return;
        tvCommentState.setText(message);
        tvCommentState.setVisibility(View.VISIBLE);
    }

    private void hideState() {
        if (tvCommentState != null) {
            tvCommentState.setVisibility(View.GONE);
        }
    }
}