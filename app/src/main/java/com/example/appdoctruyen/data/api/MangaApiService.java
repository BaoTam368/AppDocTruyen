package com.example.appdoctruyen.data.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PUT;
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

    @GET("manga/{mangaId}/covers")
    Call<CoverListResponse> getMangaCovers(@Path("mangaId") String mangaId);

    @GET("manga/{mangaId}/chapters")
    Call<ChapterListResponse> getMangaChapters(@Path("mangaId") String mangaId);

    @GET("manga/chapter/{chapterId}/pages")
    Call<ChapterPagesResponse> getChapterPages(@Path("chapterId") String chapterId);

    @GET("chapter/{chapterId}/pages")
    Call<ChapterPagesResponse> getChapterPagesAlias(@Path("chapterId") String chapterId);

    @GET("groups")
    Call<GroupListResponse> getGroups(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("groups/search")
    Call<GroupListResponse> searchGroups(
            @Query("name") String name,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("groups/{groupId}")
    Call<GroupDetailResponse> getGroupDetail(@Path("groupId") String groupId);

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

    @GET("local-manga/search-sync")
    Call<MangaListResponse> searchAndSync(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @POST("local-manga/{mangaId}/sync")
    Call<MangaDetailResponse> syncMangaFromMangaDex(@Path("mangaId") String mangaId);

    @POST("local-manga/sync/popular")
    Call<MangaListResponse> syncPopularMangas(@Query("count") int count);

    @POST("local-manga/sync/popular")
    Call<MangaListResponse> syncPopularMangas(
            @Query("total") int total,
            @Query("limit") int limit,
            @Query("pages") int pages
    );

    @GET("users/{userId}")
    Call<UserResponse> getUser(@Path("userId") String userId);

    @POST("users")
    Call<UserResponse> createOrUpdateUser(@Body UserDto user);

    @PUT("users/{userId}")
    Call<UserResponse> updateUser(@Path("userId") String userId, @Body UserDto user);

    @GET("comments")
    Call<CommentListResponse> getComments(
            @Query("mangaId") String mangaId,
            @Query("chapterId") String chapterId
    );

    @POST("comments")
    Call<CommentResponse> createComment(@Body CreateCommentRequest request);

    @PUT("comments/{commentId}")
    Call<CommentResponse> updateComment(
            @Path("commentId") int commentId,
            @Body CreateCommentRequest request
    );

    @DELETE("comments/{commentId}")
    Call<EmptyResponse> deleteComment(@Path("commentId") int commentId);

    @GET("posts")
    Call<PostListResponse> getPosts(
            @Query("userId") String userId
    );

    @GET("posts/{postId}")
    Call<PostResponse> getPost(@Path("postId") int postId);

    @POST("posts")
    Call<PostResponse> createPost(@Body CreatePostRequest request);

    @PUT("posts/{postId}")
    Call<PostResponse> updatePost(
            @Path("postId") int postId,
            @Body CreatePostRequest request
    );

    @DELETE("posts/{postId}")
    Call<EmptyResponse> deletePost(@Path("postId") int postId);

    @POST("posts/{postId}/like")
    Call<LikeResponse> toggleLikePost(
            @Path("postId") int postId,
            @Body LikeDto request
    );
}

class MangaListResponse extends ApiResponse<List<MangaDto>> {
}

class MangaDetailResponse extends ApiResponse<MangaDto> {
}

class CoverListResponse extends ApiResponse<List<CoverDto>> {
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
    @SerializedName(value = "coverUrl", alternate = {"cover_url", "thumbnailUrl", "thumbnail_url"})
    String coverUrl;
    String latestChapter;
    String status;
    Integer year;
    List<String> tags;
    String contentRating;
    List<String> availableTranslatedLanguages;
    int views;
    int likes;
}

class CoverDto {
    String coverId;
    String mangaId;
    String fileName;
    @SerializedName(value = "coverUrl", alternate = {"cover_url"})
    String coverUrl;
    @SerializedName(value = "thumbnailUrl", alternate = {"thumbnail_url"})
    String thumbnailUrl;
    String volume;
    String locale;
    String createdAt;
    String updatedAt;
}

class ChapterDto {
    String chapterId;
    String mangaId;
    String title;
    String chapterName;
    String chapter;
    String chapterNumber;
    String volume;
    String translatedLanguage;
    String language;
    String publishAt;
    String readableAt;
    String createdAt;
    String updatedAt;
}
class ChapterPagesDto {
    String chapterId;
    List<String> pages;
}

class GroupDto {
    String groupId;
    String name;
    String website;
    String description;
    String avatarUrl;
    int mangaCount;
    int comicCount;
    int memberCount;
    int followerCount;
    int rank;
}