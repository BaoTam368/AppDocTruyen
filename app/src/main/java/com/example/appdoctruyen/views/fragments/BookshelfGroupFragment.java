package com.example.appdoctruyen.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    private static final long SEARCH_DELAY_MS = 450;

    private TextView tabAll, tabFamous, tabComicCount;
    private EditText edtGroupSearch;
    private RecyclerView recyclerViewGrid;
    private RecyclerView recyclerViewRanking;
    private TextView tvEmpty, tvStatsNote;
    private BookshelfGroupAdapter gridAdapter;
    private GroupRankingAdapter rankingAdapter;
    private MangaRepository mangaRepository;
    private List<TranslationGroup> groupSource = new ArrayList<>();
    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    private int currentTab = 0;
    private String currentQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        tabAll = view.findViewById(R.id.tabAll);
        tabFamous = view.findViewById(R.id.tabFamous);
        tabComicCount = view.findViewById(R.id.tabComicCount);
        edtGroupSearch = view.findViewById(R.id.edtGroupSearch);
        recyclerViewGrid = view.findViewById(R.id.recyclerViewGroup2);
        recyclerViewRanking = view.findViewById(R.id.recyclerViewRanking);
        tvEmpty = view.findViewById(R.id.tvEmptyGroup);
        tvStatsNote = view.findViewById(R.id.tvGroupStatsNote);
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
        setupSearchBox();
        selectTab(0);
        loadGroupsFromApi();

        return view;
    }

    @Override
    public void onDestroyView() {
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroyView();
    }

    private void openGroupDetail(TranslationGroup group) {
        if (group == null) return;

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

    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> selectTab(0));
        tabFamous.setOnClickListener(v -> selectTab(1));
        tabComicCount.setOnClickListener(v -> selectTab(2));
    }

    private void setupSearchBox() {
        if (edtGroupSearch == null) return;
        edtGroupSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s != null ? s.toString().trim() : "";
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    if (query.isEmpty()) {
                        loadGroupsFromApi();
                    } else if (query.length() >= 2) {
                        searchGroupsFromApi(query);
                    } else {
                        currentQuery = query;
                        groupSource = new ArrayList<>();
                        showGroupState(getString(R.string.group_search_hint));
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }
        });
    }

    private void loadGroupsFromApi() {
        currentQuery = "";
        final String requestQuery = "";
        showGroupState(getString(R.string.group_loading));
        mangaRepository.getGroups(50, 0, new MangaRepository.RepositoryCallback<List<TranslationGroup>>() {
            @Override
            public void onSuccess(List<TranslationGroup> groups) {
                if (!isAdded() || !requestQuery.equals(currentQuery)) return;
                groupSource = groups != null ? groups : new ArrayList<>();
                selectTab(currentTab);
            }

            @Override
            public void onError(String message) {
                if (!isAdded() || !requestQuery.equals(currentQuery)) return;
                groupSource = new ArrayList<>();
                showGroupState(getString(R.string.group_error));
            }
        });
    }

    private void searchGroupsFromApi(String query) {
        currentQuery = query;
        final String requestQuery = query;
        showGroupState(getString(R.string.group_loading));
        mangaRepository.searchGroups(query, 50, 0, new MangaRepository.RepositoryCallback<List<TranslationGroup>>() {
            @Override
            public void onSuccess(List<TranslationGroup> groups) {
                if (!isAdded() || !requestQuery.equals(currentQuery)) return;
                groupSource = groups != null ? groups : new ArrayList<>();
                selectTab(currentTab);
            }

            @Override
            public void onError(String message) {
                if (!isAdded() || !requestQuery.equals(currentQuery)) return;
                groupSource = new ArrayList<>();
                showGroupState(getString(R.string.group_error));
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
                showStatsNote(false);
                showGridGroups(new ArrayList<>(groupSource));
                break;
            case 1:
                tabFamous.setBackgroundResource(R.drawable.bg_tab_selected);
                tabFamous.setTextColor(getResources().getColor(R.color.white, null));
                tabFamous.setTypeface(null, android.graphics.Typeface.BOLD);
                showStatsNote(!groupSource.isEmpty() && !hasPositiveMemberCount(groupSource));
                showRankingGroups(groupsForFamousTab());
                break;
            case 2:
                tabComicCount.setBackgroundResource(R.drawable.bg_tab_selected);
                tabComicCount.setTextColor(getResources().getColor(R.color.white, null));
                tabComicCount.setTypeface(null, android.graphics.Typeface.BOLD);
                showStatsNote(!groupSource.isEmpty() && !hasPositiveComicCount(groupSource));
                showGridGroups(groupsForComicCountTab());
                break;
            default:
                showStatsNote(false);
                showGridGroups(new ArrayList<>());
                break;
        }
    }

    private List<TranslationGroup> groupsForFamousTab() {
        List<TranslationGroup> sorted = new ArrayList<>(groupSource);
        if (hasPositiveMemberCount(sorted)) {
            Collections.sort(sorted, (g1, g2) -> g2.getMemberCount() - g1.getMemberCount());
            assignRanks(sorted);
        } else {
            clearRanks(sorted);
        }
        return sorted;
    }

    private List<TranslationGroup> groupsForComicCountTab() {
        List<TranslationGroup> sorted = new ArrayList<>(groupSource);
        if (hasPositiveComicCount(sorted)) {
            Collections.sort(sorted, (g1, g2) -> g2.getComicCount() - g1.getComicCount());
        }
        clearRanks(sorted);
        return sorted;
    }

    private boolean hasPositiveMemberCount(List<TranslationGroup> groups) {
        for (TranslationGroup group : groups) {
            if (group != null && group.getMemberCount() > 0) return true;
        }
        return false;
    }

    private boolean hasPositiveComicCount(List<TranslationGroup> groups) {
        for (TranslationGroup group : groups) {
            if (group != null && group.getComicCount() > 0) return true;
        }
        return false;
    }

    private void showGridGroups(List<TranslationGroup> groups) {
        List<TranslationGroup> safeGroups = groups != null ? groups : new ArrayList<>();
        recyclerViewRanking.setVisibility(View.GONE);
        rankingAdapter.updateList(new ArrayList<>());

        if (safeGroups.isEmpty()) {
            recyclerViewGrid.setVisibility(View.GONE);
            gridAdapter.updateList(new ArrayList<>());
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(currentQuery.isEmpty() ? R.string.group_empty : R.string.group_empty_search);
        } else {
            recyclerViewGrid.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            gridAdapter.updateList(safeGroups);
        }
    }

    private void showRankingGroups(List<TranslationGroup> groups) {
        List<TranslationGroup> safeGroups = groups != null ? groups : new ArrayList<>();
        recyclerViewGrid.setVisibility(View.GONE);
        gridAdapter.updateList(new ArrayList<>());

        if (safeGroups.isEmpty()) {
            recyclerViewRanking.setVisibility(View.GONE);
            rankingAdapter.updateList(new ArrayList<>());
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(currentQuery.isEmpty() ? R.string.group_empty : R.string.group_empty_search);
        } else {
            recyclerViewRanking.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            rankingAdapter.updateList(safeGroups);
        }
    }

    private void showGroupState(String message) {
        showStatsNote(false);
        recyclerViewGrid.setVisibility(View.GONE);
        recyclerViewRanking.setVisibility(View.GONE);
        gridAdapter.updateList(new ArrayList<>());
        rankingAdapter.updateList(new ArrayList<>());
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText(message);
    }

    private void showStatsNote(boolean visible) {
        if (tvStatsNote != null) {
            tvStatsNote.setVisibility(visible ? View.VISIBLE : View.GONE);
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

    private void assignRanks(List<TranslationGroup> groups) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i) != null) {
                groups.get(i).setRank(i + 1);
            }
        }
    }

    private void clearRanks(List<TranslationGroup> groups) {
        for (TranslationGroup group : groups) {
            if (group != null) {
                group.setRank(0);
            }
        }
    }
}