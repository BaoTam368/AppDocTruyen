package com.example.appdoctruyen.World;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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