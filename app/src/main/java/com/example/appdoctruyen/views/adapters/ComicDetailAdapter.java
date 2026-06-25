package com.example.appdoctruyen.views.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appdoctruyen.views.fragments.ComicInfoFragment;
import com.example.appdoctruyen.views.fragments.ComicChaptersFragment;

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
        // Position 0 là Tab đầu tiên, Position 1 là Tab thứ hai
        if (position == 0) {
            return ComicInfoFragment.newInstance(mangaId, mangaTitle); // Trả về Fragment Giới thiệu
        } else {
            return ComicChaptersFragment.newInstance(mangaId, mangaTitle); // Trả về Fragment Danh sách chương
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Số lượng Tab là 2
    }
}
