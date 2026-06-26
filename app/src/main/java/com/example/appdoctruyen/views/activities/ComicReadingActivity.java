package com.example.appdoctruyen.views.activities;

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

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.ComicPage;
import com.example.appdoctruyen.views.adapters.ComicPageAdapter;
import com.example.appdoctruyen.models.Chapter;
import com.example.appdoctruyen.views.adapters.ChapterAdapter;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.data.database.BookshelfDatabaseHelper;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.data.firebase.BookshelfFirebaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ComicReadingActivity extends AppCompatActivity {

    private ImageView btnBack, btnChapterList, btnReport, btnReload, btnNextChapter;
    private TextView tvChapterTitle;
    private RecyclerView lvPages;

    private List<ComicPage> pageList;
    private ComicPageAdapter adapter;

    // Quản lý số thứ tự chương hiện tại đang đọc (mặc định vào là Chapter 1)
    private int currentChapter = 1;
    private String mangaId;
    private String mangaTitle;
    private String chapterId;
    private String chapterName;
    private MangaRepository mangaRepository;
    
    private List<Chapter> chapterList;
    private int currentChapterIndex = 0;
    
    private BookshelfDatabaseHelper bookshelfDatabaseHelper;
    private AuthManager authManager;
    private BookshelfFirebaseHelper firebaseHelper;
    
    private String coverUrl = ""; // To store cover URL if available

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_reading);

        // Nhận extras từ intent
        mangaId = getIntent().getStringExtra("mangaId");
        mangaTitle = getIntent().getStringExtra("mangaTitle");
        chapterId = getIntent().getStringExtra("chapterId");
        chapterName = getIntent().getStringExtra("chapterName");
        currentChapter = getIntent().getIntExtra("CHAPTER_NUM", 1);
        coverUrl = getIntent().getStringExtra("comic_cover");

        bookshelfDatabaseHelper = new BookshelfDatabaseHelper(this);
        authManager = new AuthManager();
        String userId = getCurrentUserId();
        if (userId != null && !userId.equals("local_user")) {
            firebaseHelper = new BookshelfFirebaseHelper(userId);
        }

        mangaRepository = new MangaRepository();
        chapterList = new ArrayList<>();

        // Tải danh sách chapters của manga
        if (mangaId != null && !mangaId.isEmpty()) {
            loadChapterList();
            // Nếu không có cover URL từ intent, tự động lấy từ API
            if (coverUrl == null || coverUrl.isEmpty()) {
                loadMangaDetailForCover();
            }
        }

        // 1. Ánh xạ View từ XML
        btnBack = findViewById(R.id.btnBack);
        btnChapterList = findViewById(R.id.btnChapterList);
//        btnReport = findViewById(R.id.btnReport);
        btnReload = findViewById(R.id.btnReload);
        btnNextChapter = findViewById(R.id.btnNextChapter);
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

        // Nút chuyển sang chương tiếp theo
        btnNextChapter.setOnClickListener(v -> {
            if (chapterList == null || chapterList.isEmpty()) {
                Toast.makeText(this, "Không có danh sách chapter", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (currentChapterIndex < chapterList.size() - 1) {
                currentChapterIndex++;
                Chapter nextChapter = chapterList.get(currentChapterIndex);
                chapterId = nextChapter.getChapterId();
                chapterName = nextChapter.getName();
                loadChapterData(currentChapterIndex + 1);
            } else {
                Toast.makeText(this, "Đã đến chapter cuối cùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hàm tải dữ liệu hình ảnh từ API
     */
    private void loadChapterData(int chapterNum) {
        // Cập nhật lại tiêu đề trên thanh Top Bar
        if (chapterName != null && !chapterName.isEmpty()) {
            tvChapterTitle.setText(chapterName);
        } else {
            tvChapterTitle.setText("Chapter " + chapterNum);
        }

        // Xóa sạch các trang truyện cũ đang hiển thị
        pageList.clear();

        // Nếu có chapterId, gọi API lấy pages
        if (chapterId != null && !chapterId.isEmpty()) {
            loadChapterPagesFromApi(chapterId);
            saveHistory();
        } else {
            Toast.makeText(this, "Không có chapterId, hiển thị ảnh mẫu", Toast.LENGTH_SHORT).show();
            loadMockPages(chapterNum);
            saveHistory();
        }
    }

    private void saveHistory() {
        String userId = getCurrentUserId();
        String title = mangaTitle != null ? mangaTitle : ("Manga " + mangaId);
        String chName = chapterName != null ? chapterName : ("Chapter " + currentChapter);
        // Dùng coverUrl nếu có, hoặc chuỗi rỗng để tránh null
        String cover = (coverUrl != null && !coverUrl.isEmpty()) ? coverUrl : "";
        
        bookshelfDatabaseHelper.saveReadingHistory(userId, mangaId, chapterId, chName, title, cover);
        if (firebaseHelper != null) {
            firebaseHelper.saveReadingHistory(mangaId, chapterId, chName, title, cover);
        }
    }

    /**
     * Tự động lấy cover URL từ API khi không được truyền qua Intent.
     * Sau khi lấy được, cập nhật lại lịch sử đọc với cover URL mới.
     */
    private void loadMangaDetailForCover() {
        mangaRepository.getMangaDetail(mangaId, new MangaRepository.RepositoryCallback<Comic>() {
            @Override
            public void onSuccess(Comic data) {
                if (data != null && data.getCoverUrl() != null && !data.getCoverUrl().isEmpty()) {
                    coverUrl = data.getCoverUrl();
                    // Cập nhật tiêu đề nếu chưa có
                    if ((mangaTitle == null || mangaTitle.isEmpty()) && data.getTitle() != null) {
                        mangaTitle = data.getTitle();
                    }
                    // Lưu lại lịch sử với cover URL đã có
                    saveHistory();
                }
            }

            @Override
            public void onError(String message) {
                // Bỏ qua lỗi - chỉ là không có ảnh bìa, không ảnh hưởng đến việc đọc truyện
            }
        });
    }

    private String getCurrentUserId() {
        if (authManager == null) return "local_user";
        String userId = authManager.getCurrentUserId();
        return userId != null ? userId : "local_user";
    }

    private void loadChapterPagesFromApi(String chapterId) {
        Toast.makeText(this, "Đang tải trang...", Toast.LENGTH_SHORT).show();
        
        mangaRepository.getChapterPages(chapterId, new MangaRepository.RepositoryCallback<List<ComicPage>>() {
            @Override
            public void onSuccess(List<ComicPage> data) {
                if (data == null || data.isEmpty()) {
                    Toast.makeText(ComicReadingActivity.this, "Không có trang nào cho chapter này", Toast.LENGTH_SHORT).show();
                    loadMockPages(currentChapter);
                    return;
                }
                
                pageList.clear();
                pageList.addAll(data);
                adapter.notifyDataSetChanged();
                lvPages.scrollToPosition(0);
                Toast.makeText(ComicReadingActivity.this, "Đã tải " + data.size() + " trang", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ComicReadingActivity.this, "Lỗi tải trang: " + message, Toast.LENGTH_SHORT).show();
                loadMockPages(currentChapter);
            }
        });
    }

    private void loadMockPages(int chapterNum) {
        // Giả lập thay đổi ảnh dựa trên số chương chẵn/lẻ
        if (chapterNum % 2 == 0) {
            pageList.add(new ComicPage("https://loremflickr.com/600/800/manga"));
            pageList.add(new ComicPage("https://loremflickr.com/600/800/comic"));
        } else {
            pageList.add(new ComicPage("https://loremflickr.com/600/800/comic"));
            pageList.add(new ComicPage("https://loremflickr.com/600/800/manga"));
            pageList.add(new ComicPage("https://loremflickr.com/600/900/action"));
        }
        adapter.notifyDataSetChanged();
        lvPages.scrollToPosition(0);
    }

    private void loadChapterList() {
        mangaRepository.getMangaChapters(mangaId, new MangaRepository.RepositoryCallback<List<Chapter>>() {
            @Override
            public void onSuccess(List<Chapter> data) {
                chapterList.clear();
                chapterList.addAll(data);
                
                // Tìm vị trí của chapter hiện tại
                if (chapterId != null && !chapterId.isEmpty()) {
                    for (int i = 0; i < chapterList.size(); i++) {
                        if (chapterId.equals(chapterList.get(i).getChapterId())) {
                            currentChapterIndex = i;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ComicReadingActivity.this, "Lỗi tải danh sách chapter: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hàm khởi tạo và hiển thị bảng danh sách chương
     */
    private void showBottomSheetChapters() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Nạp layout BottomSheet
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_chapters, null);

        ListView lvBottomSheetChapters = bottomSheetView.findViewById(R.id.lvBottomSheetChapters);

        // Sử dụng danh sách chapter thực tế nếu có,否则 dùng dữ liệu giả
        List<Chapter> displayChapters = chapterList != null && !chapterList.isEmpty() 
                ? chapterList 
                : new ArrayList<>();
        
        if (displayChapters.isEmpty()) {
            for (int i = 1; i <= 20; i++) {
                displayChapters.add(new Chapter("Chapter " + i, "12:15 12/1/2021", i <= 5));
            }
        }

        // Sử dụng ChapterAdapter để đổ dữ liệu vào ListView của BottomSheet
        ChapterAdapter chapterAdapter = new ChapterAdapter(this, displayChapters);
        lvBottomSheetChapters.setAdapter(chapterAdapter);

        // Bắt sự kiện khi click chọn nhanh một chương bất kỳ trong BottomSheet
        lvBottomSheetChapters.setOnItemClickListener((parent, view, position, id) -> {
            Chapter selectedChapter = displayChapters.get(position);
            chapterId = selectedChapter.getChapterId();
            chapterName = selectedChapter.getName();
            currentChapterIndex = position;
            loadChapterData(position + 1); // Gọi hàm nạp truyện để thay đổi nội dung trang đọc
            bottomSheetDialog.dismiss(); // Đóng bảng trượt sau khi chọn xong chương
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show(); // Hiển thị bảng lên màn hình
    }
}
