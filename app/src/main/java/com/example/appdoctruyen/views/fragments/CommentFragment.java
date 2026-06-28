package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Comment;
import com.example.appdoctruyen.views.adapters.CommentAdapter;
import com.example.appdoctruyen.views.adapters.PostAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment {

    private RecyclerView rvComments;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private Spinner spinnerComic;
    private EditText edtComment;
    private ImageButton btnSendComment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_world_comment, container, false);
        rvComments = view.findViewById(R.id.recyclerViewComment);
        spinnerComic = view.findViewById(R.id.spinner_comic);
        edtComment = view.findViewById(R.id.edt_comment);
        btnSendComment = view.findViewById(R.id.btn_send_comment);
        setupSpinner();
        setupCommentList();
        setupSendButton();

//        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewComment);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        List<Comment> list = new ArrayList<>();
//        list.add(new Comment("Hải Tú", "10 phút trước", "Tuyệt vời quá sếp ơi!!!", "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg", "..."));
//        list.add(new Comment("Mono", "30 phút trước", "Đỉnh của chóp anh trai ơi ", "https://i.pinimg.com/736x/d8/50/be/d850bede84c97ea84d93f7cb49c3bde7.jpg", "..."));
//
//        CommentAdapter adapter = new CommentAdapter(requireContext(), list);
//        recyclerView.setAdapter(adapter);

        return view;
    }

    private void setupSendButton() {

        btnSendComment.setOnClickListener(v -> {
            String content = edtComment.getText().toString().trim();
            if (content.isEmpty()) {
                edtComment.setError("Enter comment content");
                return;
            }

            String comicName = spinnerComic.getSelectedItem().toString();
            Comment comment = new Comment("Linh Nguyen", "Just now", content, "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg", comicName);
            addComment(comment);
            edtComment.setText("");
        });
    }

    private void setupCommentList() {
        commentList = new ArrayList<>();
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvComments.setNestedScrollingEnabled(false);

        commentAdapter = new CommentAdapter(requireContext(), commentList, (comment, position) -> {
        });

        rvComments.setAdapter(commentAdapter);

        addComment(new Comment("Chen Dong", "10 minutes ago", "Renegade Immortal just got a new chapter, and it is really good!", "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg", "..."));
        addComment(new Comment("Xiao Ding", "20 minutes ago", "How many coins will later VIP chapters cost to unlock?", "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg", "..."));
        addComment(new Comment("I Eat Tomatoes", "30 minutes ago", "Great art and smooth translation. This team deserves five stars!", "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg", "..."));
        addComment(new Comment("Tang Jia San Shao", "120 minutes ago", "Does anyone know the exact release schedule for this series?", "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg", "..."));
        addComment(new Comment("Er Gen", "300 minutes ago", "I just topped up 50k and unlocked a long reading session.", "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg", "..."));

    }

    public void addComment(Comment comment) {
        commentList.add(0, comment);

        commentAdapter.notifyItemInserted(0);

        rvComments.scrollToPosition(0);
    }

    private void setupSpinner() {

        List<String> comics = new ArrayList<>();

        comics.add("Renegade Immortal");
        comics.add("Battle Through the Heavens");
        comics.add("A Will Eternal");
        comics.add("A Record of a Mortal's Journey to Immortality");
        comics.add("The Eternal Supreme");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, comics);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerComic.setAdapter(adapter);
    }
}
