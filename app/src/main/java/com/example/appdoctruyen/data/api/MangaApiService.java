package com.example.appdoctruyen.data.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MangaApiService {

    @GET("manga")
    Call<MangaListResponse> searchManga(
            @Query("title") String title,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("manga/{mangaId}")
    Call<MangaDetailResponse> getMangaDetail(@Path("mangaId") String mangaId);

    @GET("manga/{mangaId}/chapters")
    Call<ChapterListResponse> getMangaChapters(@Path("mangaId") String mangaId);

    @GET("manga/chapter/{chapterId}/pages")
    Call<ChapterPagesResponse> getChapterPages(@Path("chapterId") String chapterId);

    @GET("groups")
    Call<GroupListResponse> getGroups();

    @GET("groups/{groupId}")
    Call<GroupDetailResponse> getGroupDetail(@Path("groupId") String groupId);

    // Local SQLite API endpoints
    @GET("local-manga")
    Call<MangaListResponse> getLocalMangaList(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("local-manga/search")
    Call<MangaListResponse> searchLocalMangas(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @POST("local-manga/{mangaId}/sync")
    Call<MangaDetailResponse> syncMangaFromMangaDex(@Path("mangaId") String mangaId);

    @POST("local-manga/sync/popular")
    Call<MangaListResponse> syncPopularMangas(@Query("count") int count);
}

class MangaListResponse extends ApiResponse<List<MangaDto>> {
}

class MangaDetailResponse extends ApiResponse<MangaDto> {
}

class ChapterListResponse extends ApiResponse<List<ChapterDto>> {
}

class ChapterPagesResponse extends ApiResponse<ChapterPagesDto> {
}

class GroupListResponse extends ApiResponse<List<GroupDto>> {
}

class GroupDetailResponse extends ApiResponse<GroupDto> {
}

class ApiResponse<T> {
    boolean success;
    String message;
    T data;
}

class MangaDto {
    String mangaId;
    String title;
    String description;
    String coverUrl;
    String latestChapter;
    String status;
    Integer year;
    List<String> tags;
}

class ChapterDto {
    String chapterId;
    String chapterName;
    String chapterNumber;
    String language;
    String createdAt;
}

class ChapterPagesDto {
    String chapterId;
    List<String> pages;
}

class GroupDto {
    String groupId;
    String name;
    String description;
    int comicCount;
    int memberCount;
    int followerCount;
    int rank;
}
