package com.example.appdoctruyen.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.views.adapters.NewComicAdapter;

import java.util.ArrayList;
import java.util.List;

public class NewBookshelfActivity extends AppCompatActivity {
    private ImageView btnBack;
    private RecyclerView rv_comic_chapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bookshelf);
        btnBack = findViewById(R.id.btnBack);
        rv_comic_chapters = findViewById(R.id.rv_comic_chapters);
        btnBack.setOnClickListener(v -> finish());
        setupNewComic();

    }

    private void setupNewComic() {

        List<Comic> comicList = new ArrayList<>();

        comicList.add(new Comic(1, "Martial Peak", R.drawable.placeholder_comic, null, "Chapter 2315", null, "Manhua", "19:08 28/12/2024", false, false));
        comicList.add(new Comic(2, "One Piece", R.drawable.placeholder_comic, null, "Chapter 1150", null, "Manga", "20:15 20/06/2026", false, false));

        NewComicAdapter adapter = new NewComicAdapter(this, comicList, (comic, position) -> {
            Intent intent = new Intent(NewBookshelfActivity.this, ComicDetailActivity.class);
            intent.putExtra("comic_id", comic.getId());
            intent.putExtra("comic_title", comic.getTitle());
            startActivity(intent);
        });

        rv_comic_chapters.setLayoutManager(new LinearLayoutManager(this));
        rv_comic_chapters.setAdapter(adapter);
    }
}
