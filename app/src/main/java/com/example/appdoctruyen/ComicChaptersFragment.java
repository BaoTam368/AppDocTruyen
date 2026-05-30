package com.example.appdoctruyen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appdoctruyen.models.Chapter;
import com.example.appdoctruyen.models.ChapterAdapter;

import java.util.ArrayList;
import java.util.List;

public class ComicChaptersFragment extends Fragment {

    private ListView lvChapters;
    private TextView tvTotalChapters;
    private ChapterAdapter chapterAdapter;
    private List<Chapter> chapterList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic_chapters, container, false);

        lvChapters = view.findViewById(R.id.lvChapters);
        tvTotalChapters = view.findViewById(R.id.tvTotalChapters);

        // 1. Tạo dữ liệu giả
        chapterList = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            boolean isFree = (i <= 5); // 5 chương đầu free, còn lại khóa
            chapterList.add(new Chapter("Chapter " + i, "16:38 30/05/2026", isFree));
        }

        tvTotalChapters.setText("Tổng số " + chapterList.size() + " chương");

        // 2. Gắn Adapter
        chapterAdapter = new ChapterAdapter(getContext(), chapterList);
        lvChapters.setAdapter(chapterAdapter);

        // 3. XỬ LÝ SỰ KIỆN CLICK CHỌN CHƯƠNG
        lvChapters.setOnItemClickListener((parent, view1, position, id) -> {
            // Lấy ra chương mà người dùng vừa bấm vào
            Chapter clickedChapter = chapterList.get(position);

            // Kiểm tra trạng thái: Nếu Free (hoặc Unlock) thì mới cho vào đọc
            if (clickedChapter.isFree()) {
                // Tạo Intent để chuyển sang màn hình Đọc truyện
                Intent intent = new Intent(getActivity(), ComicReadingActivity.class);

                // Gửi kèm số thứ tự của chương sang màn hình kia (position bắt đầu từ 0 nên phải + 1)
                intent.putExtra("CHAPTER_NUM", position + 1);

                startActivity(intent);
            } else {
                // Nếu bị khóa thì hiện thông báo
                Toast.makeText(getContext(), "Chương này đang bị khóa! Vui lòng mở khoá để đọc.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
