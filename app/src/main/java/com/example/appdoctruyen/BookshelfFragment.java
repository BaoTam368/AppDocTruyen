package com.example.appdoctruyen;

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
import androidx.recyclerview.widget.RecyclerView;
import com.example.appdoctruyen.models.Comic;
import java.util.ArrayList;
import java.util.List;

public class BookshelfFragment extends Fragment {

    private TextView tabFollowing, tabRecentlyRead, tabDownloaded;

    private RecyclerView recyclerView;
    private TextView tvEmpty;

    private BookshelfAdapter adapter;

    // Tab hiện tại (0 = Theo Dõi, 1 = Vừa Xem, 2 = Đã Tải)
    private int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);

        tabFollowing = view.findViewById(R.id.tabFollowing);
        tabRecentlyRead = view.findViewById(R.id.tabRecentlyRead);
        tabDownloaded = view.findViewById(R.id.tabDownloaded);
        recyclerView = view.findViewById(R.id.recyclerViewBookshelf);
        tvEmpty = view.findViewById(R.id.tvEmptyBookshelf);

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        adapter = new BookshelfAdapter(requireContext(), createSampleFollowedComics(),
                (comic, position) -> {
                    Intent intent = new Intent(requireContext(), ComicDetailActivity.class);
                    intent.putExtra("comic_id", comic.getId());
                    intent.putExtra("comic_title", comic.getTitle());
                    startActivity(intent);
                });
        recyclerView.setAdapter(adapter);

        setupTabListeners();

        // Mặc định chọn tab Theo Dõi
        selectTab(0);

        return view;
    }


    private void setupTabListeners() {
        tabFollowing.setOnClickListener(v -> selectTab(0));
        tabRecentlyRead.setOnClickListener(v -> selectTab(1));
        tabDownloaded.setOnClickListener(v -> selectTab(2));
    }


    private void selectTab(int tabIndex) {
        currentTab = tabIndex;

        resetAllTabs();

        List<Comic> data;
        switch (tabIndex) {
            case 0:
                tabFollowing.setBackgroundResource(R.drawable.bg_tab_selected);
                tabFollowing.setTextColor(getResources().getColor(R.color.white, null));
                tabFollowing.setTypeface(null, android.graphics.Typeface.BOLD);
                data = createSampleFollowedComics();
                break;
            case 1:
                tabRecentlyRead.setBackgroundResource(R.drawable.bg_tab_selected);
                tabRecentlyRead.setTextColor(getResources().getColor(R.color.white, null));
                tabRecentlyRead.setTypeface(null, android.graphics.Typeface.BOLD);
                data = createSampleRecentComics();
                break;
            case 2:
                tabDownloaded.setBackgroundResource(R.drawable.bg_tab_selected);
                tabDownloaded.setTextColor(getResources().getColor(R.color.white, null));
                tabDownloaded.setTypeface(null, android.graphics.Typeface.BOLD);
                data = createSampleDownloadedComics();
                break;
            default:
                data = new ArrayList<>();
        }

        adapter.updateList(data);
        if (data.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            if (tabIndex == 2) {
                tvEmpty.setText("Không có truyện đã tải");
            } else if (tabIndex == 1) {
                tvEmpty.setText("Chưa đọc truyện nào");
            } else {
                tvEmpty.setText("Chưa theo dõi truyện nào");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void resetAllTabs() {
        tabFollowing.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabFollowing.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabFollowing.setTypeface(null, android.graphics.Typeface.NORMAL);

        tabRecentlyRead.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabRecentlyRead.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabRecentlyRead.setTypeface(null, android.graphics.Typeface.NORMAL);

        tabDownloaded.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabDownloaded.setTextColor(getResources().getColor(R.color.tab_unselected_text, null));
        tabDownloaded.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    // DỮ LIỆU MẪU

    private List<Comic> createSampleFollowedComics() {
        List<Comic> list = new ArrayList<>();
        list.add(new Comic(1, "Bất bại chân ma", R.drawable.placeholder_comic, "Chapter 312"));
        list.add(new Comic(2, "Ta không muốn...", R.drawable.placeholder_comic, "Chapter 251"));
        list.add(new Comic(3, "Vạn cổ chí tôn", R.drawable.placeholder_comic, "Chapter 541"));
        list.add(new Comic(4, "Người chơi khô...", R.drawable.placeholder_comic, "Chapter 95"));
        list.add(new Comic(5, "Hảo đồ nhi hãy...", R.drawable.placeholder_comic, "Chapter 214"));
        list.add(new Comic(6, "Cung quỷ kiếm...", R.drawable.placeholder_comic, "Chapter 251"));
        return list;
    }

    private List<Comic> createSampleRecentComics() {
        List<Comic> list = new ArrayList<>();
        Comic c1 = new Comic(4, "Người chơi khô...", R.drawable.placeholder_comic, "Chapter 95");
        c1.setLastReadChapter("Chapter 4");
        list.add(c1);

        Comic c2 = new Comic(3, "Vạn cổ chí tôn", R.drawable.placeholder_comic, "Chapter 541");
        c2.setLastReadChapter("Chapter 1");
        list.add(c2);

        Comic c3 = new Comic(1, "Bất bại chân ma", R.drawable.placeholder_comic, "Chapter 312");
        c3.setLastReadChapter("Chapter 4");
        list.add(c3);

        Comic c4 = new Comic(7, "Tứ kỵ sĩ khải huyền", R.drawable.placeholder_comic, "Chapter 50");
        c4.setLastReadChapter("Chapter 14");
        list.add(c4);

        Comic c5 = new Comic(6, "Cung quỷ kiếm thần", R.drawable.placeholder_comic, "Chapter 251");
        c5.setLastReadChapter("Chapter 1");
        list.add(c5);

        Comic c6 = new Comic(8, "Thám tử Kindaichi", R.drawable.placeholder_comic, "Chapter 100");
        c6.setLastReadChapter("Chapter 9");
        list.add(c6);

        return list;
    }

    private List<Comic> createSampleDownloadedComics() {
        // Trả về rỗng để hiển thị "Không có truyện đã tải"
        return new ArrayList<>();
    }
}
