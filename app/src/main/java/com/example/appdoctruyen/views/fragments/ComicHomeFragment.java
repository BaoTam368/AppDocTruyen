package com.example.appdoctruyen.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.views.activities.FilterActivity;
import com.example.appdoctruyen.views.activities.MainActivity;
import com.example.appdoctruyen.views.activities.NewBookshelfActivity;
import com.example.appdoctruyen.views.activities.NotificationActivity;
import com.example.appdoctruyen.views.activities.RankingActivity;
import com.example.appdoctruyen.views.activities.SearchActivity;
import com.example.appdoctruyen.views.adapters.FeaturedComicAdapter;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.views.activities.ComicDetailActivity;
import com.example.appdoctruyen.views.adapters.BookshelfAdapter;

import java.util.ArrayList;
import java.util.List;

public class ComicHomeFragment extends Fragment {

    private RecyclerView rvFeaturedComics;
    private RecyclerView rvRecentlyUpdated;
    private List<Comic> featuredList;
    private List<Comic> recentList;
    private FeaturedComicAdapter featuredAdapter;
    private BookshelfAdapter recentAdapter;
    private ImageView ivAvatar, ivNotification, ivNewComic;
    private LinearLayout layoutSearchBar;
//    private LinearLayout layoutCatGenres, layoutCatTopUser, layoutCatNew, layoutCatHot;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comic_home, container, false);

        rvFeaturedComics = view.findViewById(R.id.rv_featured_comics);
        rvRecentlyUpdated = view.findViewById(R.id.rv_recently_updated);

        ivAvatar = view.findViewById(R.id.iv_avatar);
        layoutSearchBar = view.findViewById(R.id.iv_search_bar);
        ivNotification = view.findViewById(R.id.iv_notification);
        ivNewComic = view.findViewById(R.id.iv_new_comic);

//        layoutCatGenres = view.findViewById(R.id.layout_cat_genres);
//        layoutCatTopUser = view.findViewById(R.id.layout_cat_top_user);
//        layoutCatNew = view.findViewById(R.id.layout_cat_new);
//        layoutCatHot = view.findViewById(R.id.layout_cat_hot);

        // Click Avatar
        ivAvatar.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToProfileTab();
            }
        });

        // Click ô Tìm Kiếm -> Mở SearchActivity [INDEX]
        layoutSearchBar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            startActivity(intent);
        });

        // Click Chuông thông báo
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificationActivity.class);
            startActivity(intent);
        });

//        // Click danh mục Thể Loại
//        layoutCatGenres.setOnClickListener(v -> {
//            Intent intent = new Intent(requireContext(), FilterActivity.class);
//            startActivity(intent);
//        });

//        // Click danh mục Top User
//        layoutCatTopUser.setOnClickListener(v -> {
//            Intent intent = new Intent(requireContext(), RankingActivity.class);
//            startActivity(intent);
//        });
//
        // Click danh mục Truyện Mới
        ivNewComic.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NewBookshelfActivity.class);
            startActivity(intent);
        });

//        // Click danh mục HOT
//        layoutCatHot.setOnClickListener(v -> {
//            Toast.makeText(requireContext(), "Xem danh sách Truyện HOT", Toast.LENGTH_SHORT).show();
//        });

        setupFeaturedComics();
        setupRecentlyUpdatedComics();

        return view;
    }

    private void setupFeaturedComics() {
        featuredList = new ArrayList<>();

        featuredList.add(new Comic(1, "One Piece", R.drawable.placeholder_comic, "Chapter 1100"));
        featuredList.add(new Comic(2, "Doraemon", R.drawable.placeholder_comic, "Chapter 200"));
        featuredList.add(new Comic(3, "Conan", R.drawable.placeholder_comic, "Chapter 1050"));

        featuredAdapter = new FeaturedComicAdapter(requireContext(), featuredList,
                (comic, position) -> openComicDetail(comic));

        rvFeaturedComics.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        rvFeaturedComics.setNestedScrollingEnabled(false);
        rvFeaturedComics.setAdapter(featuredAdapter);
    }

    private void setupRecentlyUpdatedComics() {
        recentList = new ArrayList<>();

        recentList.add(new Comic(4, "Naruto", R.drawable.placeholder_comic, "Chapter 700"));
        recentList.add(new Comic(5, "Bleach", R.drawable.placeholder_comic, "Chapter 686"));
        recentList.add(new Comic(6, "Dragon Ball", R.drawable.placeholder_comic, "Chapter 519"));
        recentList.add(new Comic(7, "Jujutsu Kaisen", R.drawable.placeholder_comic, "Chapter 260"));
        recentList.add(new Comic(8, "Solo Leveling", R.drawable.placeholder_comic, "Chapter 179"));
        recentList.add(new Comic(9, "Kimetsu no Yaiba", R.drawable.placeholder_comic, "Chapter 205"));

        recentAdapter = new BookshelfAdapter(requireContext(), recentList,
                (comic, position) -> openComicDetail(comic));

        rvRecentlyUpdated.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvRecentlyUpdated.setNestedScrollingEnabled(false);
        rvRecentlyUpdated.setAdapter(recentAdapter);
    }

    private void openComicDetail(Comic comic) {
        Intent intent = new Intent(requireContext(), ComicDetailActivity.class);
        intent.putExtra("comic_id", comic.getId());
        intent.putExtra("comic_title", comic.getTitle());
        startActivity(intent);
    }
}