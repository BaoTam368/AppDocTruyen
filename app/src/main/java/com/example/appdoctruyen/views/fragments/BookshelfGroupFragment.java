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

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.models.TranslationGroup;
import com.example.appdoctruyen.views.activities.GroupDetailActivity;
import com.example.appdoctruyen.views.adapters.BookshelfGroupAdapter;
import com.example.appdoctruyen.views.adapters.GroupRankingAdapter;

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
    private MangaRepository mangaRepository;
    private List<TranslationGroup> groupSource = new ArrayList<>();

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
        mangaRepository = new MangaRepository();

        recyclerViewGrid.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerViewRanking.setLayoutManager(new LinearLayoutManager(requireContext()));

        gridAdapter = new BookshelfGroupAdapter(requireContext(), new ArrayList<>(),
                (group, position) -> openGroupDetail(group));
        recyclerViewGrid.setAdapter(gridAdapter);

        rankingAdapter = new GroupRankingAdapter(requireContext(), new ArrayList<>(),
                (group, position) -> openGroupDetail(group));
        recyclerViewRanking.setAdapter(rankingAdapter);

        setupTabListeners();
        groupSource = createSampleGroups();
        selectTab(0);
        loadGroupsFromApi();

        return view;
    }

    // Truyền dữ liệu nhóm dịch sang màn chi tiết bằng Intent
    private void openGroupDetail(TranslationGroup group) {
        Intent intent = new Intent(requireContext(), GroupDetailActivity.class);
        intent.putExtra("group_id", group.getGroupId());
        intent.putExtra("group_name", group.getName());
        intent.putExtra("group_description",
                group.getDescription() != null ? group.getDescription() : getString(R.string.group_default_description));
        intent.putExtra("group_comic_count", group.getComicCount());
        intent.putExtra("group_member_count", group.getMemberCount());
        intent.putExtra("group_follower_count", group.getFollowerCount());
        intent.putExtra("group_rank", group.getRank());
        intent.putExtra("group_avatar_res_id", group.getAvatarResId());
        startActivity(intent);
    }

    // Gắn sự kiện cho các tab nhóm dịch: Tất cả, Nổi tiếng, Số truyện
    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> selectTab(0));
        tabFamous.setOnClickListener(v -> selectTab(1));
        tabComicCount.setOnClickListener(v -> selectTab(2));
    }

    private void loadGroupsFromApi() {
        mangaRepository.getGroups(new MangaRepository.RepositoryCallback<List<TranslationGroup>>() {
            @Override
            public void onSuccess(List<TranslationGroup> groups) {
                if (!isAdded() || groups == null || groups.isEmpty()) {
                    return;
                }
                groupSource = groups;
                selectTab(currentTab);
            }

            @Override
            public void onError(String message) {
                // Nếu backend chưa chạy hoặc /api/groups lỗi thì giữ dữ liệu demo local để demo.
            }
        });
    }

    private void selectTab(int tabIndex) {
        currentTab = tabIndex;
        resetAllTabs();

        switch (tabIndex) {
            case 0:
                tabAll.setBackgroundResource(R.drawable.bg_tab_selected);
                tabAll.setTextColor(getResources().getColor(R.color.white, null));
                tabAll.setTypeface(null, android.graphics.Typeface.BOLD);
                showGridGroups(new ArrayList<>(groupSource));
                break;
            case 1:
                tabFamous.setBackgroundResource(R.drawable.bg_tab_selected);
                tabFamous.setTextColor(getResources().getColor(R.color.white, null));
                tabFamous.setTypeface(null, android.graphics.Typeface.BOLD);
                showRankingGroups(sortGroupsForTab(1));
                break;
            case 2:
                tabComicCount.setBackgroundResource(R.drawable.bg_tab_selected);
                tabComicCount.setTextColor(getResources().getColor(R.color.white, null));
                tabComicCount.setTypeface(null, android.graphics.Typeface.BOLD);
                showGridGroups(sortGroupsForTab(2));
                break;
            default:
                showGridGroups(new ArrayList<>());
                break;
        }
    }

    private List<TranslationGroup> sortGroupsForTab(int tabIndex) {
        // Sắp xếp danh sách nhóm dịch theo tab đang chọn.
        List<TranslationGroup> sorted = new ArrayList<>(groupSource);
        if (tabIndex == 1) {
            Collections.sort(sorted, (g1, g2) -> g2.getFollowerCount() - g1.getFollowerCount());
        } else if (tabIndex == 2) {
            Collections.sort(sorted, (g1, g2) -> g2.getComicCount() - g1.getComicCount());
        }
        assignRanks(sorted);
        return sorted;
    }

    private void showGridGroups(List<TranslationGroup> groups) {
        recyclerViewRanking.setVisibility(View.GONE);
        rankingAdapter.updateList(new ArrayList<>());

        if (groups.isEmpty()) {
            recyclerViewGrid.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(R.string.group_empty);
        } else {
            recyclerViewGrid.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            gridAdapter.updateList(groups);
        }
    }

    private void showRankingGroups(List<TranslationGroup> groups) {
        recyclerViewGrid.setVisibility(View.GONE);
        gridAdapter.updateList(new ArrayList<>());

        if (groups.isEmpty()) {
            recyclerViewRanking.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(R.string.group_empty);
        } else {
            recyclerViewRanking.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            rankingAdapter.updateList(groups);
        }
    }

    private void resetAllTabs() {
        tabAll.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabAll.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabAll.setTypeface(null, android.graphics.Typeface.NORMAL);

        tabFamous.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabFamous.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabFamous.setTypeface(null, android.graphics.Typeface.NORMAL);

        tabComicCount.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabComicCount.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabComicCount.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    // DỮ LIỆU MẪU
    private List<TranslationGroup> createSampleGroups() {
        List<TranslationGroup> list = new ArrayList<>();
        list.add(new TranslationGroup(1, "Ánh Dương Team", "Nhóm dịch truyện phiêu lưu và hành động.", R.drawable.placeholder_group, 18, 860, 1450));
        list.add(new TranslationGroup(2, "Hikari Scan", "Nhóm dịch truyện học đường và đời thường.", R.drawable.placeholder_group, 24, 1240, 2310));
        list.add(new TranslationGroup(3, "Manga Việt Group", "Nhóm cộng tác dịch nhiều thể loại truyện mới.", R.drawable.placeholder_group, 31, 1750, 2680));
        list.add(new TranslationGroup(4, "Lam Ngọc Team", "Nhóm dịch truyện lãng mạn và giả tưởng.", R.drawable.placeholder_group, 15, 620, 980));
        list.add(new TranslationGroup(5, "Sakura Scan", "Nhóm dịch truyện Nhật Bản cập nhật hằng tuần.", R.drawable.placeholder_group, 28, 1325, 2040));
        list.add(new TranslationGroup(6, "Trăng Non Team", "Nhóm dịch nhỏ tập trung vào truyện mới nổi.", R.drawable.placeholder_group, 9, 410, 720));
        list.add(new TranslationGroup(7, "Bút Mực Team", "Nhóm dịch truyện hài và đời sống học đường.", R.drawable.placeholder_group, 12, 540, 800));
        list.add(new TranslationGroup(8, "Lá Xanh Scan", "Nhóm dịch truyện nhẹ nhàng, dễ đọc.", R.drawable.placeholder_group, 20, 930, 1190));
        list.add(new TranslationGroup(9, "Aster Scan", "Nhóm dịch truyện fantasy và siêu nhiên.", R.drawable.placeholder_group, 26, 1600, 2500));
        list.add(new TranslationGroup(10, "Vầng Trăng Team", "Nhóm dịch truyện romance và drama.", R.drawable.placeholder_group, 17, 780, 1340));
        list.add(new TranslationGroup(11, "Nova Comics", "Nhóm dịch truyện hành động và võ thuật.", R.drawable.placeholder_group, 34, 1890, 3100));
        list.add(new TranslationGroup(12, "Mộc Truyện", "Nhóm dịch truyện ngắn, cập nhật ổn định.", R.drawable.placeholder_group, 11, 360, 640));
        return list;
    }

    private void assignRanks(List<TranslationGroup> groups) {
        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).setRank(i + 1);
        }
    }
}
