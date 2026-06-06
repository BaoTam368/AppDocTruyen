package com.example.appdoctruyen.World;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appdoctruyen.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class WorldFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);

        tabLayout = view.findViewById(R.id.tabLayout_world);
        viewPager2 = view.findViewById(R.id.viewPager_world);
        WorldPagerAdapter adapter = new WorldPagerAdapter(this);
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Bài viết");
                        break;
                    case 1:
                        tab.setText("Bình luận");
                        break;
                    case 2:
                        tab.setText("Nhóm Dịch");
                        break;
                }
            }
        }).attach();

        return view;
    }
}