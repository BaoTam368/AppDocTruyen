package com.example.appdoctruyen.views.fragments;

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
import com.example.appdoctruyen.models.Group;
import com.example.appdoctruyen.views.adapters.GroupAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_world_group, container, false);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView = view.findViewById(R.id.recyclerViewGroup);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<Group> list = new ArrayList<>();
        list.add(new Group("Skyholic - Sơn Tùng M-TP", "...", "cap", "https://i.pinimg.com/originals/a0/0b/ce/a00bce1efb2fcd1b9d44359d95f0fc88.jpg", 1500, 450));
        list.add(new Group("Cộng đồng Đạp xe Xuyên Việt", "...", "cap", "https://i.pinimg.com/originals/c9/4c/bb/c94cbbf16b3fcf58be79d50c95029e9f.jpg", 2000, 120));

        GroupAdapter adapter = new GroupAdapter(requireContext(), list);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
