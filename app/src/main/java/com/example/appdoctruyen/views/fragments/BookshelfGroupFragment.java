package com.example.appdoctruyen.views.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.views.activities.GroupDetailActivity;
import com.example.appdoctruyen.views.adapters.GroupRankingAdapter;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.TranslationGroup;
import com.example.appdoctruyen.views.adapters.BookshelfGroupAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookshelfGroupFragment extends Fragment {

    private TextView tabAll, tabFamous, tabComicCount;

    private RecyclerView recyclerViewGrid;
    private RecyclerView recyclerViewRanking;

    private TextView tvEmpty;

    private BookshelfGroupAdapter gridAdapter;
    private GroupRankingAdapter rankingAdapter;

    // Tab hiện tại (0 = Tất Cả, 1 = Nổi Tiếng, 2 = Số Truyện)
    private int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        tabAll = view.findViewById(R.id.tabAll);
        tabFamous = view.findViewById(R.id.tabFamous);
//        tabComicCount = view.findViewById(R.id.tabComicCount);
        recyclerViewGrid = view.findViewById(R.id.recyclerViewGroup2);
        recyclerViewRanking = view.findViewById(R.id.recyclerViewRanking);
        tvEmpty = view.findViewById(R.id.tvEmptyGroup);

        recyclerViewGrid.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        recyclerViewRanking.setLayoutManager(new LinearLayoutManager(requireContext()));

        gridAdapter = new BookshelfGroupAdapter(requireContext(), createSampleGroups(),
                (group, position) -> openGroupDetail(group));
        recyclerViewGrid.setAdapter(gridAdapter);

        rankingAdapter = new GroupRankingAdapter(requireContext(), createSampleRankingGroups(),
                (group, position) -> openGroupDetail(group));
        recyclerViewRanking.setAdapter(rankingAdapter);

        setupTabListeners();

        selectTab(0);

        return view;
    }

    private void openGroupDetail(TranslationGroup group) {
        Intent intent = new Intent(requireContext(), GroupDetailActivity.class);
        intent.putExtra("group_id", group.getId());
        intent.putExtra("group_name", group.getName());
        intent.putExtra("group_description", group.getDescription() != null ? group.getDescription() : "Nhóm dịch truyện tranh");
        intent.putExtra("group_comic_count", group.getComicCount());
        intent.putExtra("group_member_count", group.getMemberCount());
        intent.putExtra("group_follower_count", group.getFollowerCount());
        intent.putExtra("group_avatar_res_id", group.getAvatarResId());
        startActivity(intent);
    }

    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> selectTab(0));
        tabFamous.setOnClickListener(v -> selectTab(1));
//        tabComicCount.setOnClickListener(v -> selectTab(2));
    }

    private void selectTab(int tabIndex) {
        currentTab = tabIndex;

        // Reset tất cả tab
        resetAllTabs();

        switch (tabIndex) {
            case 0: // Tất Cả - grid
                tabAll.setBackgroundResource(R.drawable.bg_tab_selected);
                tabAll.setTextColor(getResources().getColor(R.color.white, null));
                tabAll.setTypeface(null, android.graphics.Typeface.BOLD);

                recyclerViewGrid.setVisibility(View.VISIBLE);
                recyclerViewRanking.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.GONE);

                gridAdapter.updateList(createSampleGroups());
                break;

            case 1: // Nổi Tiếng - ranking list
                tabFamous.setBackgroundResource(R.drawable.bg_tab_selected);
                tabFamous.setTextColor(getResources().getColor(R.color.white, null));
                tabFamous.setTypeface(null, android.graphics.Typeface.BOLD);

                recyclerViewGrid.setVisibility(View.GONE);
                recyclerViewRanking.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);

                rankingAdapter.updateList(createSampleRankingGroups());
                break;

//            case 2: // Số Truyện - grid sorted
//                tabComicCount.setBackgroundResource(R.drawable.bg_tab_selected);
//                tabComicCount.setTextColor(getResources().getColor(R.color.white, null));
//                tabComicCount.setTypeface(null, android.graphics.Typeface.BOLD);
//
//                recyclerViewGrid.setVisibility(View.VISIBLE);
//                recyclerViewRanking.setVisibility(View.GONE);
//                tvEmpty.setVisibility(View.GONE);
//
//                // Sort theo số truyện giảm dần
//                List<TranslationGroup> sorted = createSampleGroups();
//                Collections.sort(sorted, (g1, g2) -> g2.getComicCount() - g1.getComicCount());
//                gridAdapter.updateList(sorted);
//                break;
        }
    }

    private void resetAllTabs() {
        tabAll.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabAll.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabAll.setTypeface(null, android.graphics.Typeface.NORMAL);

        tabFamous.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabFamous.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabFamous.setTypeface(null, android.graphics.Typeface.NORMAL);

//        tabComicCount.setBackgroundResource(R.drawable.bg_tab_unselected);
//        tabComicCount.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
//        tabComicCount.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    // DỮ LIỆU MẪU
    private List<TranslationGroup> createSampleGroups() {
        List<TranslationGroup> list = new ArrayList<>();
        list.add(new TranslationGroup(1, "Hoa Hạ", R.drawable.placeholder_group, 2, 142));
        list.add(new TranslationGroup(2, "Máy Ke", R.drawable.placeholder_group, 12, 424));
        list.add(new TranslationGroup(3, "Kom Be", R.drawable.placeholder_group, 32, 952));
        list.add(new TranslationGroup(4, "Chip Chip", R.drawable.placeholder_group, 25, 524));
        list.add(new TranslationGroup(5, "Hou Hou", R.drawable.placeholder_group, 52, 865));
        list.add(new TranslationGroup(6, "Phei Pha", R.drawable.placeholder_group, 23, 950));
        list.add(new TranslationGroup(7, "Tin Tin", R.drawable.placeholder_group, 45, 874));
        list.add(new TranslationGroup(8, "Von Von", R.drawable.placeholder_group, 43, 976));
        list.add(new TranslationGroup(9, "Darkin", R.drawable.placeholder_group, 26, 546));
        list.add(new TranslationGroup(10, "Ghế Bàn", R.drawable.placeholder_group, 21, 425));
        list.add(new TranslationGroup(11, "Lá Cây", R.drawable.placeholder_group, 29, 654));
        list.add(new TranslationGroup(12, "Lửa Ko", R.drawable.placeholder_group, 4, 42));
        return list;
    }

    private List<TranslationGroup> createSampleRankingGroups() {
        List<TranslationGroup> list = createSampleGroups();
        // Sort theo số thành viên giảm dần
        Collections.sort(list, (g1, g2) -> g2.getMemberCount() - g1.getMemberCount());
        return list;
    }
}
