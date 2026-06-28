package com.example.appdoctruyen.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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

    private RecyclerView rvComments;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private EditText edtComment;
    private ImageButton btnSendComment;

    private MangaRepository repository;
    private AuthManager authManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new MangaRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_world_comment, container, false);

        rvComments = view.findViewById(R.id.recyclerViewComment);
        edtComment = view.findViewById(R.id.edt_comment);
        btnSendComment = view.findViewById(R.id.btn_send_comment);

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
                edtComment.setError("Enter a comment");
                return;
            }

            String currentUserId = authManager.getCurrentUserId();
            if (currentUserId == null) {
                Toast.makeText(requireContext(), "Please log in to comment!", Toast.LENGTH_SHORT).show();
                return;
            }

            CreateCommentRequest request = new CreateCommentRequest(currentUserId, content);

            repository.createComment(request, new MangaRepository.RepositoryCallback<Comment>() {
                @Override
                public void onSuccess(Comment newComment) {
                    if (!isAdded() || getActivity() == null) return;

                    addComment(newComment);
                    edtComment.setText("");
                    Toast.makeText(requireContext(), "Comment sent!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) return;
                    Log.e("COMMENT_FRAGMENT", "Lỗi tạo bình luận: " + message);
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadComments() {
        repository.getComments(new MangaRepository.RepositoryCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
                if (!isAdded() || getActivity() == null) return;
                if (data != null && !data.isEmpty()) {
                    android.util.Log.d("TIME_DEBUG", "First comment time from server: " + data.get(0).getTime());
                }

                commentList.clear();
                commentList.addAll(data);
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Log.e("COMMENT_FRAGMENT", "Lỗi tải bình luận: " + message);
                Toast.makeText(requireContext(), "Unable to load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addComment(Comment comment) {
        commentList.add(0, comment);
        commentAdapter.notifyItemInserted(0);
        rvComments.scrollToPosition(0);
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
}
