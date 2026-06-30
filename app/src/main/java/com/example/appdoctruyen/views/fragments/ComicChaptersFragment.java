package com.example.appdoctruyen.views.fragments;

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

import com.example.appdoctruyen.views.activities.ComicDetailActivity;
import com.example.appdoctruyen.views.activities.ComicReadingActivity;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Chapter;
import com.example.appdoctruyen.views.adapters.ChapterAdapter;
import com.example.appdoctruyen.data.api.MangaRepository;

import java.util.ArrayList;
import java.util.List;

public class ComicChaptersFragment extends Fragment {

    private static final String ARG_MANGA_ID = "mangaId";
    private static final String ARG_MANGA_TITLE = "mangaTitle";

    private ListView lvChapters;
    private TextView tvTotalChapters;
    private ChapterAdapter chapterAdapter;
    private List<Chapter> chapterList;
    private String mangaId;
    private String mangaTitle;
    private MangaRepository mangaRepository;

    public static ComicChaptersFragment newInstance(String mangaId, String mangaTitle) {
        ComicChaptersFragment fragment = new ComicChaptersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MANGA_ID, mangaId);
        args.putString(ARG_MANGA_TITLE, mangaTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mangaId = getArguments().getString(ARG_MANGA_ID);
            mangaTitle = getArguments().getString(ARG_MANGA_TITLE);
        }
        mangaRepository = new MangaRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic_chapters, container, false);

        lvChapters = view.findViewById(R.id.lvChapters);
        tvTotalChapters = view.findViewById(R.id.tvTotalChapters);

        // 1. Khởi tạo danh sách và adapter
        chapterList = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(getContext(), chapterList);
        lvChapters.setAdapter(chapterAdapter);

        // 2. Tải chapters từ API
        loadChaptersFromApi();

        // 3. XỬ LÝ SỰ KIỆN CLICK CHỌN CHƯƠNG
        lvChapters.setOnItemClickListener((parent, view1, position, id) -> {
            // Lấy ra chương mà người dùng vừa bấm vào
            if (position < 0 || position >= chapterList.size()) return;
            Chapter clickedChapter = chapterList.get(position);
            if (clickedChapter.getChapterId() == null || clickedChapter.getChapterId().trim().isEmpty()) {
                Toast.makeText(getContext(), "This chapter is not available for reading.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo Intent để chuyển sang màn hình Đọc truyện
            Intent intent = new Intent(getActivity(), ComicReadingActivity.class);

            // Gửi kèm mangaId, mangaTitle và chapterId
            intent.putExtra("mangaId", mangaId);
            intent.putExtra("mangaTitle", mangaTitle);
            intent.putExtra("chapterId", clickedChapter.getChapterId());
            intent.putExtra("chapterName", clickedChapter.getName());
            if (getActivity() instanceof ComicDetailActivity) {
                intent.putExtra("comic_cover", ((ComicDetailActivity) getActivity()).getCoverUrl());
            }
            startActivity(intent);
        });

        return view;
    }

    private void loadChaptersFromApi() {
        if (mangaId == null || mangaId.isEmpty()) {
            Toast.makeText(getContext(), "Missing mangaId", Toast.LENGTH_SHORT).show();
            return;
        }

        mangaRepository.getMangaChapters(mangaId, new MangaRepository.RepositoryCallback<List<Chapter>>() {
            @Override
            public void onSuccess(List<Chapter> data) {
                chapterList.clear();
                if (data != null) {
                    chapterList.addAll(data);
                }
                if (chapterList.isEmpty()) {
                    tvTotalChapters.setText("No chapters available");
                } else {
                    tvTotalChapters.setText("Total " + chapterList.size() + " chapters");
                }
                chapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                chapterList.clear();
                chapterAdapter.notifyDataSetChanged();
                tvTotalChapters.setText("Unable to load chapters");
                Toast.makeText(getContext(), "Unable to load chapters", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
