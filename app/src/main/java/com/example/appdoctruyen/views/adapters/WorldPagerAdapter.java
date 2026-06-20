package com.example.appdoctruyen.views.adapters;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appdoctruyen.views.fragments.CommentFragment;
import com.example.appdoctruyen.views.fragments.GroupFragment;
import com.example.appdoctruyen.views.fragments.PostFragment;

public class WorldPagerAdapter extends FragmentStateAdapter {

    public WorldPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PostFragment();
            case 1:
                return new CommentFragment();
            case 2:
                return new GroupFragment();
            default:
                return new PostFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}