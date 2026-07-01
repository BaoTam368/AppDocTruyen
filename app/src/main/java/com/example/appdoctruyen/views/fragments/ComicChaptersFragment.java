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

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.models.Chapter;
import com.example.appdoctruyen.views.activities.ComicDetailActivity;
import com.example.appdoctruyen.views.activities.ComicReadingActivity;
import com.example.appdoctruyen.views.adapters.ChapterAdapter;

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

        chapterList = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(getContext(), chapterList);
        lvChapters.setAdapter(chapterAdapter);

        loadChaptersFromApi();

        lvChapters.setOnItemClickListener((parent, view1, position, id) -> {
            if (position < 0 || position >= chapterList.size()) return;
            Chapter clickedChapter = chapterList.get(position);
            if (clickedChapter == null || isBlank(clickedChapter.getChapterId())) {
                Toast.makeText(getContext(), "This chapter is not available for reading.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isBlank(mangaId)) {
                Toast.makeText(getContext(), "Unable to open reader for this manga.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getActivity(), ComicReadingActivity.class);
            intent.putExtra("mangaId", mangaId);
            intent.putExtra("mangaTitle", mangaTitle);
            intent.putExtra("chapterId", clickedChapter.getChapterId());
            intent.putExtra("chapterName", clickedChapter.getName());
            if (getActivity() instanceof ComicDetailActivity) {
                intent.putExtra("comic_cover", ((ComicDetailActivity) getActivity()).getCoverUrl());
            }
            if (isAdded()) {
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadChaptersFromApi() {
        if (isBlank(mangaId)) {
            tvTotalChapters.setText("No chapters available.");
            return;
        }

        mangaRepository.getMangaChapters(mangaId, new MangaRepository.RepositoryCallback<List<Chapter>>() {
            @Override
            public void onSuccess(List<Chapter> data) {
                if (!isAdded()) return;
                chapterList.clear();
                if (data != null) {
                    for (Chapter chapter : data) {
                        if (chapter != null && !isBlank(chapter.getChapterId())) {
                            chapterList.add(chapter);
                        }
                    }
                }
                if (chapterList.isEmpty()) {
                    tvTotalChapters.setText("No chapters available.");
                } else {
                    tvTotalChapters.setText("Total " + chapterList.size() + " chapters");
                }
                chapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                chapterList.clear();
                chapterAdapter.notifyDataSetChanged();
                tvTotalChapters.setText("Unable to load data. Please try again.");
                Toast.makeText(getContext(), "Unable to load chapters", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}