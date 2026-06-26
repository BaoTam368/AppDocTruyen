package com.example.appdoctruyen.data.api;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Chapter;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.models.ComicPage;
import com.example.appdoctruyen.models.TranslationGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MangaRepository {

    private final MangaApiService apiService;

    public MangaRepository() {
        apiService = ApiClient.getMangaApiService();
    }

    public void searchManga(String title, int limit, int offset,
                            RepositoryCallback<List<Comic>> callback) {
        apiService.searchManga(title, limit, offset).enqueue(new Callback<MangaListResponse>() {
            @Override
            public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                MangaListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapMangaList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Không lấy được danh sách truyện"));
                }
            }

            @Override
            public void onFailure(Call<MangaListResponse> call, Throwable throwable) {
                callback.onError("Không kết nối được Node.js backend");
            }
        });
    }

    public void getMangaDetail(String mangaId, final RepositoryCallback<Comic> callback) {
        if (isBlank(mangaId)) {
            callback.onError("Thiếu mangaId");
            return;
        }

        android.util.Log.d("CHECK_API", "Bắt đầu gọi mạng qua Retrofit...");

        apiService.getMangaDetail(mangaId).enqueue(new Callback<MangaDetailResponse>() {
            @Override
            public void onResponse(Call<MangaDetailResponse> call, Response<MangaDetailResponse> response) {
                try {
                    android.util.Log.d("CHECK_API", "Đã nhận phản hồi HTTP từ Node.js: " + response.code());

                    MangaDetailResponse body = response.body();
                    if (response.isSuccessful() && body != null) {
                        android.util.Log.d("CHECK_API", "JSON success: " + body.success);

                        if (body.success && body.data != null) {
                            Comic comic = mapManga(body.data);
                            callback.onSuccess(comic);
                        } else {
                            callback.onError(body.message != null ? body.message : "Server Node.js báo thất bại");
                        }
                    } else {
                        String errorStr = response.errorBody() != null ? response.errorBody().string() : "Rỗng";
                        android.util.Log.e("CHECK_API", "Lỗi HTTP hoặc Endpoint: " + errorStr);
                        callback.onError("Lỗi HTTP: " + response.code());
                    }
                } catch (Exception e) {
                    android.util.Log.e("CHECK_API", "Lỗi crash khi parse data: " + e.getMessage());
                    callback.onError("Lỗi xử lý dữ liệu");
                }
            }

            @Override
            public void onFailure(Call<MangaDetailResponse> call, Throwable throwable) {
                android.util.Log.e("CHECK_API", "Thất bại mạng (onFailure): " + throwable.getMessage());
                callback.onError("Không kết nối được Node.js backend");
            }
        });
    }

    public void getMangaChapters(String mangaId, RepositoryCallback<List<Chapter>> callback) {
        if (isBlank(mangaId)) {
            callback.onError("Thiếu mangaId");
            return;
        }

        apiService.getMangaChapters(mangaId).enqueue(new Callback<ChapterListResponse>() {
            @Override
            public void onResponse(Call<ChapterListResponse> call, Response<ChapterListResponse> response) {
                ChapterListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapChapterList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Không lấy được danh sách chapter"));
                }
            }

            @Override
            public void onFailure(Call<ChapterListResponse> call, Throwable throwable) {
                callback.onError("Không kết nối được Node.js backend");
            }
        });
    }

    public void getChapterPages(String chapterId, RepositoryCallback<List<ComicPage>> callback) {
        if (isBlank(chapterId)) {
            callback.onError("Thiếu chapterId");
            return;
        }

        apiService.getChapterPages(chapterId).enqueue(new Callback<ChapterPagesResponse>() {
            @Override
            public void onResponse(Call<ChapterPagesResponse> call, Response<ChapterPagesResponse> response) {
                ChapterPagesResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success && body.data != null) {
                    callback.onSuccess(mapPageList(body.data.pages));
                } else {
                    callback.onError(readErrorMessage(body, "Không lấy được ảnh đọc truyện"));
                }
            }

            @Override
            public void onFailure(Call<ChapterPagesResponse> call, Throwable throwable) {
                callback.onError("Không kết nối được Node.js backend");
            }
        });
    }

    public void getGroups(RepositoryCallback<List<TranslationGroup>> callback) {
        apiService.getGroups().enqueue(new Callback<GroupListResponse>() {
            @Override
            public void onResponse(Call<GroupListResponse> call, Response<GroupListResponse> response) {
                GroupListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapGroupList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Không lấy được nhóm dịch"));
                }
            }

            @Override
            public void onFailure(Call<GroupListResponse> call, Throwable throwable) {
                callback.onError("Không kết nối được Node.js backend");
            }
        });
    }

    public void getGroupDetail(String groupId, RepositoryCallback<TranslationGroup> callback) {
        if (isBlank(groupId)) {
            callback.onError("Thiếu groupId");
            return;
        }

        apiService.getGroupDetail(groupId).enqueue(new Callback<GroupDetailResponse>() {
            @Override
            public void onResponse(Call<GroupDetailResponse> call, Response<GroupDetailResponse> response) {
                GroupDetailResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success && body.data != null) {
                    callback.onSuccess(mapGroup(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Không lấy được chi tiết nhóm dịch"));
                }
            }

            @Override
            public void onFailure(Call<GroupDetailResponse> call, Throwable throwable) {
                callback.onError("Không kết nối được Node.js backend");
            }
        });
    }

    // Local SQLite API methods
    public void getLocalMangaList(int limit, int offset, RepositoryCallback<List<Comic>> callback) {
        apiService.getLocalMangaList(limit, offset).enqueue(new Callback<MangaListResponse>() {
            @Override
            public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                MangaListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapMangaList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Không lấy được danh sách truyện từ SQLite"));
                }
            }

            @Override
            public void onFailure(Call<MangaListResponse> call, Throwable throwable) {
                callback.onError("Không kết nối được Node.js backend");
            }
        });
    }

    public void searchLocalMangas(String query, int limit, int offset, RepositoryCallback<List<Comic>> callback) {
        apiService.searchLocalMangas(query, limit, offset).enqueue(new Callback<MangaListResponse>() {
            @Override
            public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                MangaListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapMangaList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Không tìm thấy truyện"));
                }
            }

            @Override
            public void onFailure(Call<MangaListResponse> call, Throwable throwable) {
                callback.onError("Không kết nối được Node.js backend");
            }
        });
    }

    public void syncPopularMangas(int count, RepositoryCallback<List<Comic>> callback) {
        int total = Math.max(count, 1);
        int limit = Math.min(total, 100);
        int pages = (int) Math.ceil((double) total / limit);
        apiService.syncPopularMangas(total, limit, pages).enqueue(new Callback<MangaListResponse>() {
            @Override
            public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                MangaListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapMangaList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Không đồng bộ được truyện phổ biến"));
                }
            }

            @Override
            public void onFailure(Call<MangaListResponse> call, Throwable throwable) {
                callback.onError("Không kết nối được Node.js backend");
            }
        });
    }

    private List<Comic> mapMangaList(List<MangaDto> dtoList) {
        List<Comic> comics = new ArrayList<>();
        if (dtoList == null) return comics;

        for (MangaDto dto : dtoList) {
            comics.add(mapManga(dto));
        }
        return comics;
    }

    private List<Chapter> mapChapterList(List<ChapterDto> dtoList) {
        List<Chapter> chapters = new ArrayList<>();
        if (dtoList == null) return chapters;

        for (ChapterDto dto : dtoList) {
            String chapterName = isBlank(dto.chapterName)
                    ? "Chapter " + (isBlank(dto.chapterNumber) ? "" : dto.chapterNumber)
                    : dto.chapterName;
            chapters.add(new Chapter(dto.chapterId, chapterName.trim(), dto.createdAt, true));
        }
        return chapters;
    }

    private List<ComicPage> mapPageList(List<String> pageUrls) {
        List<ComicPage> pages = new ArrayList<>();
        if (pageUrls == null) return pages;

        for (String pageUrl : pageUrls) {
            if (!isBlank(pageUrl)) {
                pages.add(new ComicPage(pageUrl));
            }
        }
        return pages;
    }

    private Comic mapManga(MangaDto dto) {
        String mangaId = dto.mangaId;
        Comic comic = new Comic(
                numericIdFromString(mangaId),
                isBlank(dto.title) ? "Manga " + mangaId : dto.title,
                R.drawable.placeholder_comic,
                dto.coverUrl,
                dto.latestChapter,
                null,
                null,
                dto.description,
                false,
                false
        );
        comic.setMangaId(mangaId);
        comic.setTags(dto.tags);
        comic.setStatus(dto.status);
        comic.setYear(dto.year);
        comic.setContentRating(dto.contentRating);
        comic.setAvailableTranslatedLanguages(dto.availableTranslatedLanguages);
        return comic;
    }

    private List<TranslationGroup> mapGroupList(List<GroupDto> dtoList) {
        List<TranslationGroup> groups = new ArrayList<>();
        if (dtoList == null) return groups;

        for (GroupDto dto : dtoList) {
            TranslationGroup group = new TranslationGroup(
                    numericIdFromString(dto.groupId),
                    isBlank(dto.name) ? "Nhóm dịch" : dto.name,
                    dto.description,
                    R.drawable.placeholder_group,
                    dto.comicCount,
                    dto.memberCount,
                    dto.followerCount
            );
            group.setGroupId(dto.groupId);
            group.setRank(dto.rank);
            groups.add(group);
        }
        return groups;
    }

    private TranslationGroup mapGroup(GroupDto dto) {
        TranslationGroup group = new TranslationGroup(
                numericIdFromString(dto.groupId),
                isBlank(dto.name) ? "Nhóm dịch" : dto.name,
                dto.description,
                R.drawable.placeholder_group,
                dto.comicCount,
                dto.memberCount,
                dto.followerCount
        );
        group.setGroupId(dto.groupId);
        group.setRank(dto.rank);
        return group;
    }

    private String readErrorMessage(ApiResponse<?> body, String fallback) {
        if (body != null && !isBlank(body.message)) {
            return body.message;
        }
        return fallback;
    }

    private int numericIdFromString(String value) {
        if (isBlank(value)) return 0;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return Math.abs(value.hashCode());
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T data);

        void onError(String message);
    }
}
