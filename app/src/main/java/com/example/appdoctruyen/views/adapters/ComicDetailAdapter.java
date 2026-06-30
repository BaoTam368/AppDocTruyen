package com.example.appdoctruyen.views.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appdoctruyen.views.fragments.CommentFragment;
import com.example.appdoctruyen.views.fragments.ComicChaptersFragment;
import com.example.appdoctruyen.views.fragments.ComicInfoFragment;

public class ComicDetailAdapter extends FragmentStateAdapter {
    private String mangaId;
    private String mangaTitle;

    public ComicDetailAdapter(@NonNull FragmentActivity fragmentActivity, String mangaId, String mangaTitle) {
        super(fragmentActivity);
        this.mangaId = mangaId;
        this.mangaTitle = mangaTitle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return ComicInfoFragment.newInstance(mangaId, mangaTitle);
        } else if (position == 1) {
            return ComicChaptersFragment.newInstance(mangaId, mangaTitle);
        } else {
            return CommentFragment.newInstance(mangaId, null);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}