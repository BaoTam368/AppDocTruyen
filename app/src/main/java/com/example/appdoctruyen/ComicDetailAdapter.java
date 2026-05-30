package com.example.appdoctruyen;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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
