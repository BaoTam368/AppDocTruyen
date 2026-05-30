package com.example.appdoctruyen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.models.ComicPage;
import com.example.appdoctruyen.models.ComicPageAdapter;
import com.example.appdoctruyen.models.Chapter;
import com.example.appdoctruyen.models.ChapterAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ComicReadingActivity extends AppCompatActivity {

    private ImageView btnBack, btnChapterList, btnReport, btnReload, btnNextChapter;
    private LinearLayout layoutLike;
    private TextView tvLikeCount, tvChapterTitle;
    private RecyclerView lvPages;

    private List<ComicPage> pageList;
    private ComicPageAdapter adapter;
    private boolean isLiked = false;
    private int likeCount = 128;

    // Quản lý số thứ tự chương hiện tại đang đọc (mặc định vào là Chapter 1)
    private int currentChapter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_reading);

        currentChapter = getIntent().getIntExtra("CHAPTER_NUM", 1);

        // 1. Ánh xạ View từ XML
        btnBack = findViewById(R.id.btnBack);
        btnChapterList = findViewById(R.id.btnChapterList);
        btnReport = findViewById(R.id.btnReport);
        btnReload = findViewById(R.id.btnReload);
        btnNextChapter = findViewById(R.id.btnNextChapter);
        layoutLike = findViewById(R.id.layoutLike);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        lvPages = findViewById(R.id.lvPages);
        lvPages.setLayoutManager(new LinearLayoutManager(this));

        // 2. Khởi tạo danh sách trang truyện và gắn Adapter cho RecyclerView truyện
        pageList = new ArrayList<>();
        adapter = new ComicPageAdapter(this, pageList);
        lvPages.setAdapter(adapter);

        // Tải dữ liệu mặc định ban đầu cho Chapter 1
        loadChapterData(currentChapter);

        // 3. Xử lý các sự kiện nút bấm

        // Nút Quay lại màn hình trước
        btnBack.setOnClickListener(v -> finish());

        // Nút mở danh sách chương trượt từ dưới lên
        btnChapterList.setOnClickListener(v -> {
            showBottomSheetChapters();
        });

        // Nút Tải lại trang (Reload)
        btnReload.setOnClickListener(v -> {
            loadChapterData(currentChapter); // Gọi lại hàm nạp dữ liệu để cập nhật lại UI
        });

        // Nút Thích truyện
        layoutLike.setOnClickListener(v -> {
            if (!isLiked) {
                likeCount++;
                isLiked = true;
                Toast.makeText(this, "Đã thích chương này!", Toast.LENGTH_SHORT).show();
            } else {
                likeCount--;
                isLiked = false;
                Toast.makeText(this, "Đã bỏ thích chương này!", Toast.LENGTH_SHORT).show();
            }
            tvLikeCount.setText(String.valueOf(likeCount));
        });

        // Nút chuyển sang chương tiếp theo
        btnNextChapter.setOnClickListener(v -> {
            currentChapter++; // Tăng số chương lên 1
            loadChapterData(currentChapter); // Nạp data của chương mới
        });
    }

    /**
     * Hàm giả lập tải dữ liệu hình ảnh tương ứng theo từng chương truyện.
     * Khi có WebService RESTful API (MangaDex) thay thế phần add link giả này bằng hàm gọi Retrofit.
     */
    private void loadChapterData(int chapterNum) {
        // Cập nhật lại số tiêu đề trên thanh Top Bar
        tvChapterTitle.setText("Chapter " + chapterNum);

        // Xóa sạch các trang truyện cũ đang hiển thị
        pageList.clear();

        // Giả lập thay đổi ảnh dựa trên số chương chẵn/lẻ để thấy sự khác biệt khi chuyển chương
        if (chapterNum % 2 == 0) {
            pageList.add(new ComicPage("https://loremflickr.com/600/800/manga"));
            pageList.add(new ComicPage("https://loremflickr.com/600/800/comic"));
        } else {
            pageList.add(new ComicPage("https://loremflickr.com/600/800/comic"));
            pageList.add(new ComicPage("https://loremflickr.com/600/800/manga"));
            pageList.add(new ComicPage("https://loremflickr.com/600/900/action"));
        }

        // Báo cho Adapter biết dữ liệu đã thay đổi để vẽ lại danh sách hình ảnh truyện
        adapter.notifyDataSetChanged();

        // Lệnh cuộn trang của RecyclerView
        lvPages.scrollToPosition(0);
    }

    /**
     * Hàm khởi tạo và hiển thị bảng danh sách chương
     */
    private void showBottomSheetChapters() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Nạp layout BottomSheet
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_chapters, null);

        ListView lvBottomSheetChapters = bottomSheetView.findViewById(R.id.lvBottomSheetChapters);

        // Tạo dữ liệu giả cho danh sách chương trong BottomSheet
        List<Chapter> mockChapters = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            mockChapters.add(new Chapter("Chapter " + i, "12:15 12/1/2021", i <= 5));
        }

        // Sử dụng ChapterAdapter để đổ dữ liệu vào ListView của BottomSheet
        ChapterAdapter chapterAdapter = new ChapterAdapter(this, mockChapters);
        lvBottomSheetChapters.setAdapter(chapterAdapter);

        // Bắt sự kiện khi click chọn nhanh một chương bất kỳ trong BottomSheet
        lvBottomSheetChapters.setOnItemClickListener((parent, view, position, id) -> {
            currentChapter = position + 1; // Cập nhật số chương hiện hành dựa trên vị trí click (index chạy từ 0)
            loadChapterData(currentChapter); // Gọi hàm nạp truyện để thay đổi nội dung trang đọc
            bottomSheetDialog.dismiss(); // Đóng bảng trượt sau khi chọn xong chương
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show(); // Hiển thị bảng lên màn hình
    }
}
