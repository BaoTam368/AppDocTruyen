package com.example.appdoctruyen.World;

import android.annotation.SuppressLint;
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
import com.example.appdoctruyen.models.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_world_comment, container, false);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView = view.findViewById(R.id.recyclerViewComment);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<Comment> list = new ArrayList<>();
        list.add(new Comment("Hải Tú", "10 phút trước", "Tuyệt vời quá sếp ơi!!!", "https://i.pinimg.com/736x/87/9b/a9/879ba9d3f1cc4821a37c92b0c369fc48.jpg", "..."));
        list.add(new Comment("Mono", "30 phút trước", "Đỉnh của chóp anh trai ơi ", "https://i.pinimg.com/736x/d8/50/be/d850bede84c97ea84d93f7cb49c3bde7.jpg", "..."));

        CommentAdapter adapter = new CommentAdapter(requireContext(), list);
        recyclerView.setAdapter(adapter);

        return view;
    }
}