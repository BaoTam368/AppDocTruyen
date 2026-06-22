package com.example.appdoctruyen.views.fragments;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appdoctruyen.R;
import java.util.ArrayList;
import java.util.List;

public class WorldFragment extends Fragment {

//    private TabLayout tabLayout;
    private View worldFragmentContainer;
    private TextView tabPost, tabComment;
    private int currentTab = 0;
//    private ViewPager2 viewPager2;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);

        tabPost = view.findViewById(R.id.tabPost);
        tabComment = view.findViewById(R.id.tabComment);
//        tabGroup = view.findViewById(R.id.tabGroup);

        worldFragmentContainer = view.findViewById(R.id.worldFragmentContainer);

//        tabLayout = view.findViewById(R.id.tabLayout_world);
//        viewPager2 = view.findViewById(R.id.viewPager_world);

//        WorldPagerAdapter adapter = new WorldPagerAdapter(this);
//        viewPager2.setAdapter(adapter);
//        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
//            @Override
//            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                switch (position) {
//                    case 0:
//                        tab.setText("Bài viết");
//                        break;
//                    case 1:
//                        tab.setText("Bình luận");
//                        break;
//                    case 2:
//                        tab.setText("Nhóm Dịch");
//                        break;
//                }
//            }
//        }).attach();

        setupTabListeners();

        String page = null;

        if (getArguments() != null) {
            page = getArguments().getString("world_page");
        }

        if ("comment".equals(page)) {
            selectTab(1);
        } else {
            selectTab(0);
        }

//        if (savedInstanceState == null) {
//            selectTab(0);
//        }

        return view;
    }

    private void setupTabListeners() {
        tabPost.setOnClickListener(v -> selectTab(0));
        tabComment.setOnClickListener(v -> selectTab(1));
//        tabGroup.setOnClickListener(v -> selectTab(2));
    }

    private void selectTab(int tabIndex) {
        currentTab = tabIndex;

        resetAllTabs();

        Fragment selectedFragment;
        switch (tabIndex) {
            case 0:
                tabPost.setBackgroundResource(R.drawable.bg_tab_selected);
                tabPost.setTextColor(getResources().getColor(R.color.white, null));
                tabPost.setTypeface(null, Typeface.BOLD);
                selectedFragment = new PostFragment();
                break;
            case 1:
                tabComment.setBackgroundResource(R.drawable.bg_tab_selected);
                tabComment.setTextColor(getResources().getColor(R.color.white, null));
                tabComment.setTypeface(null, Typeface.BOLD);
                selectedFragment = new CommentFragment();
                break;
//            case 2:
//                tabGroup.setBackgroundResource(R.drawable.bg_tab_selected);
//                tabGroup.setTextColor(getResources().getColor(R.color.white, null));
//                tabGroup.setTypeface(null, Typeface.BOLD);
//                selectedFragment = new BookshelfGroupFragment();
//                break;
            default:
                selectedFragment = new PostFragment();
        }

        if (selectedFragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.worldFragmentContainer, selectedFragment)
                    .commit();
        }
    }

    private void resetAllTabs() {
        tabPost.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabPost.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabPost.setTypeface(null, android.graphics.Typeface.NORMAL);

        tabComment.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabComment.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabComment.setTypeface(null, android.graphics.Typeface.NORMAL);

//        tabGroup.setBackgroundResource(R.drawable.bg_tab_unselected);
//        tabGroup.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
//        tabGroup.setTypeface(null, android.graphics.Typeface.NORMAL);
    }
}