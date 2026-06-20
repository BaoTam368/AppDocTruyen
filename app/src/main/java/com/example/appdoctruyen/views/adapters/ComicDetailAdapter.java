package com.example.appdoctruyen.views.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appdoctruyen.views.fragments.ComicInfoFragment;
import com.example.appdoctruyen.views.fragments.ComicChaptersFragment;

public class ComicDetailAdapter extends FragmentStateAdapter {

    public ComicDetailAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Position 0 là Tab đầu tiên, Position 1 là Tab thứ hai
        if (position == 0) {
            return new ComicInfoFragment(); // Trả về Fragment Giới thiệu
        } else {
            return new ComicChaptersFragment(); // Trả về Fragment Danh sách chương
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Số lượng Tab là 2
    }
}
