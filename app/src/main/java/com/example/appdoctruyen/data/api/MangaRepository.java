package com.example.appdoctruyen.data.api;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Chapter;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.models.ComicPage;
import com.example.appdoctruyen.models.Cover;
import com.example.appdoctruyen.models.Comment;
import com.example.appdoctruyen.models.TranslationGroup;
import com.example.appdoctruyen.models.Post;
import com.example.appdoctruyen.models.User;

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

    public void searchManga(String title, int limit, int offset, RepositoryCallback<List<Comic>> callback) {
        apiService.searchManga(title, limit, offset).enqueue(new Callback<MangaListResponse>() {
            @Override
            public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                MangaListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapMangaList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to load manga list"));
                }
            }

            @Override
            public void onFailure(Call<MangaListResponse> call, Throwable throwable) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void getMangaDetail(String mangaId, final RepositoryCallback<Comic> callback) {
        if (isBlank(mangaId)) {
            callback.onError("Missing mangaId");
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
                            callback.onError(body.message != null ? body.message : "Node.js server reported a failure");
                        }
                    } else {
                        String errorStr = response.errorBody() != null ? response.errorBody().string() : "Empty";
                        android.util.Log.e("CHECK_API", "Lỗi HTTP hoặc Endpoint: " + errorStr);
                        callback.onError("HTTP error: " + response.code());
                    }
                } catch (Exception e) {
                    android.util.Log.e("CHECK_API", "Lỗi crash khi parse data: " + e.getMessage());
                    callback.onError("Data processing error");
                }
            }

            @Override
            public void onFailure(Call<MangaDetailResponse> call, Throwable throwable) {
                android.util.Log.e("CHECK_API", "Thất bại mạng (onFailure): " + throwable.getMessage());
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void getMangaCovers(String mangaId, RepositoryCallback<List<Cover>> callback) {
        if (isBlank(mangaId)) {
            callback.onError("Missing mangaId");
            return;
        }

        apiService.getMangaCovers(mangaId).enqueue(new Callback<CoverListResponse>() {
            @Override
            public void onResponse(Call<CoverListResponse> call, Response<CoverListResponse> response) {
                CoverListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapCoverList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to load manga covers"));
                }
            }

            @Override
            public void onFailure(Call<CoverListResponse> call, Throwable throwable) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void getMangaChapters(String mangaId, RepositoryCallback<List<Chapter>> callback) {
        if (isBlank(mangaId)) {
            callback.onError("Missing mangaId");
            return;
        }

        apiService.getMangaChapters(mangaId).enqueue(new Callback<ChapterListResponse>() {
            @Override
            public void onResponse(Call<ChapterListResponse> call, Response<ChapterListResponse> response) {
                ChapterListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapChapterList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to load chapter list"));
                }
            }

            @Override
            public void onFailure(Call<ChapterListResponse> call, Throwable throwable) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void getChapterPages(String chapterId, RepositoryCallback<List<ComicPage>> callback) {
        if (isBlank(chapterId)) {
            callback.onError("Missing chapterId");
            return;
        }

        apiService.getChapterPages(chapterId).enqueue(new Callback<ChapterPagesResponse>() {
            @Override
            public void onResponse(Call<ChapterPagesResponse> call, Response<ChapterPagesResponse> response) {
                ChapterPagesResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success && body.data != null) {
                    callback.onSuccess(mapPageList(body.data.pages));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to load reader pages"));
                }
            }

            @Override
            public void onFailure(Call<ChapterPagesResponse> call, Throwable throwable) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void getGroups(RepositoryCallback<List<TranslationGroup>> callback) {
        getGroups(50, 0, callback);
    }

    public void getGroups(int limit, int offset, RepositoryCallback<List<TranslationGroup>> callback) {
        apiService.getGroups(limit, offset).enqueue(new Callback<GroupListResponse>() {
            @Override
            public void onResponse(Call<GroupListResponse> call, Response<GroupListResponse> response) {
                GroupListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapGroupList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to load translation teams"));
                }
            }

            @Override
            public void onFailure(Call<GroupListResponse> call, Throwable throwable) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void searchGroups(String name, int limit, int offset, RepositoryCallback<List<TranslationGroup>> callback) {
        apiService.searchGroups(name, limit, offset).enqueue(new Callback<GroupListResponse>() {
            @Override
            public void onResponse(Call<GroupListResponse> call, Response<GroupListResponse> response) {
                GroupListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapGroupList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Translation team not found"));
                }
            }

            @Override
            public void onFailure(Call<GroupListResponse> call, Throwable throwable) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void getGroupDetail(String groupId, RepositoryCallback<TranslationGroup> callback) {
        if (isBlank(groupId)) {
            callback.onError("Missing groupId");
            return;
        }

        apiService.getGroupDetail(groupId).enqueue(new Callback<GroupDetailResponse>() {
            @Override
            public void onResponse(Call<GroupDetailResponse> call, Response<GroupDetailResponse> response) {
                GroupDetailResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success && body.data != null) {
                    callback.onSuccess(mapGroup(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to load translation team details"));
                }
            }

            @Override
            public void onFailure(Call<GroupDetailResponse> call, Throwable throwable) {
                callback.onError("Unable to connect to the Node.js backend");
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
                    callback.onError(readErrorMessage(body, "Unable to load manga list from SQLite"));
                }
            }

            @Override
            public void onFailure(Call<MangaListResponse> call, Throwable throwable) {
                callback.onError("Unable to connect to the Node.js backend");
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
                    callback.onError(readErrorMessage(body, "Manga not found"));
                }
            }

            @Override
            public void onFailure(Call<MangaListResponse> call, Throwable throwable) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void searchAndSync(String query, int limit, int offset, RepositoryCallback<List<Comic>> callback) {
        apiService.searchAndSync(query, limit, offset).enqueue(new Callback<MangaListResponse>() {
            @Override
            public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                MangaListResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapMangaList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Manga not found"));
                }
            }

            @Override
            public void onFailure(Call<MangaListResponse> call, Throwable throwable) {
                callback.onError("Unable to connect to the Node.js backend");
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
                    callback.onError(readErrorMessage(body, "Unable to sync popular manga"));
                }
            }

            @Override
            public void onFailure(Call<MangaListResponse> call, Throwable throwable) {
                callback.onError("Unable to connect to the Node.js backend");
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

    private List<Cover> mapCoverList(List<CoverDto> dtoList) {
        List<Cover> covers = new ArrayList<>();
        if (dtoList == null) return covers;

        for (CoverDto dto : dtoList) {
            covers.add(mapCover(dto));
        }
        return covers;
    }

    private Cover mapCover(CoverDto dto) {
        return new Cover(
                dto.coverId,
                dto.mangaId,
                dto.fileName,
                dto.coverUrl,
                dto.thumbnailUrl,
                dto.volume,
                dto.locale,
                dto.createdAt,
                dto.updatedAt
        );
    }

    private List<Chapter> mapChapterList(List<ChapterDto> dtoList) {
        List<Chapter> chapters = new ArrayList<>();
        if (dtoList == null) return chapters;

        for (ChapterDto dto : dtoList) {
            String chapterName = isBlank(dto.chapterName) ? "Chapter " + (isBlank(dto.chapterNumber) ? "" : dto.chapterNumber) : dto.chapterName;
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
        Comic comic = new Comic(numericIdFromString(mangaId), isBlank(dto.title) ? "Manga " + mangaId : dto.title, R.drawable.placeholder_comic, dto.coverUrl, dto.latestChapter, null, null, dto.description, false, false);
        comic.setMangaId(mangaId);
        comic.setTags(dto.tags);
        comic.setStatus(dto.status);
        comic.setYear(dto.year);
        comic.setContentRating(dto.contentRating);
        comic.setAvailableTranslatedLanguages(dto.availableTranslatedLanguages);
        comic.setViews(dto.views);
        comic.setLikes(dto.likes);
        return comic;
    }

    private List<TranslationGroup> mapGroupList(List<GroupDto> dtoList) {
        List<TranslationGroup> groups = new ArrayList<>();
        if (dtoList == null) return groups;

        for (GroupDto dto : dtoList) {
            int comicCount = dto.comicCount > 0 ? dto.comicCount : dto.mangaCount;
            TranslationGroup group = new TranslationGroup(numericIdFromString(dto.groupId), isBlank(dto.name) ? "Translation Team" : dto.name, dto.description, R.drawable.placeholder_group, comicCount, dto.memberCount, dto.followerCount);
            group.setGroupId(dto.groupId);
            group.setWebsite(dto.website);
            group.setAvatarUrl(dto.avatarUrl);
            group.setRank(dto.rank);
            groups.add(group);
        }
        return groups;
    }

    private TranslationGroup mapGroup(GroupDto dto) {
        int comicCount = dto.comicCount > 0 ? dto.comicCount : dto.mangaCount;
        TranslationGroup group = new TranslationGroup(numericIdFromString(dto.groupId), isBlank(dto.name) ? "Translation Team" : dto.name, dto.description, R.drawable.placeholder_group, comicCount, dto.memberCount, dto.followerCount);
        group.setGroupId(dto.groupId);
        group.setWebsite(dto.website);
        group.setAvatarUrl(dto.avatarUrl);
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

    private String normalizeQueryParam(String value) {
        return isBlank(value) ? null : value;
    }

    private String firstNonBlank(String... values) {
        if (values == null) return "";
        for (String value : values) {
            if (!isBlank(value)) return value;
        }
        return "";
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T data);

        void onError(String message);
    }

    //Dành cho post
    public void getPosts(RepositoryCallback<List<Post>> callback) {
        apiService.getPosts(null).enqueue(new Callback<PostListResponse>() {
            @Override
            public void onResponse(Call<PostListResponse> call, Response<PostListResponse> response) {
                PostListResponse body = response.body();

                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapPostList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to load posts"));
                }
            }

            @Override
            public void onFailure(Call<PostListResponse> call, Throwable t) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void getPostsByUser(String userId, RepositoryCallback<List<Post>> callback) {
        apiService.getPosts(userId).enqueue(new Callback<PostListResponse>() {
            @Override
            public void onResponse(Call<PostListResponse> call, Response<PostListResponse> response) {
                PostListResponse body = response.body();

                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(mapPostList(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to load posts"));
                }
            }

            @Override
            public void onFailure(Call<PostListResponse> call, Throwable t) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void createPost(CreatePostRequest request, RepositoryCallback<Post> callback) {
        apiService.createPost(request).enqueue(new Callback<PostResponse>() {

            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse body = response.body();
                android.util.Log.e("API_STATUS", "Mã phản hồi từ Server: " + response.code());
                if (response.isSuccessful() && body != null && body.success && body.data != null) {
                    callback.onSuccess(mapPost(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to create post"));
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void updatePost(int postId, CreatePostRequest request, RepositoryCallback<Post> callback) {
        apiService.updatePost(postId, request).enqueue(new Callback<PostResponse>() {

            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse body = response.body();

                if (response.isSuccessful() && body != null && body.success && body.data != null) {
                    callback.onSuccess(mapPost(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to update post"));
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void deletePost(int postId, RepositoryCallback<Void> callback) {
        apiService.deletePost(postId).enqueue(new Callback<EmptyResponse>() {

            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {

                EmptyResponse body = response.body();
                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(readErrorMessage(body, "Unable to delete post"));
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    private List<Post> mapPostList(List<PostDto> dtoList) {
        List<Post> posts = new ArrayList<>();
        if (dtoList == null) return posts;
        for (PostDto dto : dtoList) {
            posts.add(mapPost(dto));
        }
        return posts;
    }

    private Post mapPost(PostDto dto) {
        return new Post(dto.id, dto.userId, dto.displayName, dto.avatarUrl, dto.content, dto.imageUrl, dto.likeCount, dto.createdAt);
    }

    private User mapUser(UserDto dto) {
        return new User(dto.id, dto.userId, dto.displayName, dto.email, dto.avatarUrl, dto.createdAt, dto.updatedAt);
    }

    private Comment mapComment(CommentDto dto) {
        String displayName = firstNonBlank(dto.displayName, dto.userId, "Unknown");
        Comment comment = new Comment(dto.id, dto.userId, displayName, dto.createdAt, dto.content, dto.avatarUrl);
        comment.setMangaId(dto.mangaId);
        comment.setChapterId(dto.chapterId);
        comment.setUpdatedAt(dto.updatedAt);
        return comment;
    }

    public void getComments(final RepositoryCallback<List<Comment>> callback) {
        getComments(null, null, callback);
    }

    public void getComments(String mangaId, String chapterId, final RepositoryCallback<List<Comment>> callback) {
        apiService.getComments(normalizeQueryParam(mangaId), normalizeQueryParam(chapterId)).enqueue(new Callback<CommentListResponse>() {
            @Override
            public void onResponse(Call<CommentListResponse> call, Response<CommentListResponse> response) {
                CommentListResponse body = response.body();

                if (response.isSuccessful() && body != null && body.isSuccess()) {
                    callback.onSuccess(mapCommentList(body.getData()));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to load comments"));
                }
            }

            @Override
            public void onFailure(Call<CommentListResponse> call, Throwable t) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }
    public void createComment(CreateCommentRequest request, final RepositoryCallback<Comment> callback) {
        apiService.createComment(request).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse body = response.body();

                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    callback.onSuccess(mapComment(body.getData()));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to create comment"));
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void updateComment(int commentId, CreateCommentRequest request, final RepositoryCallback<Comment> callback) {
        apiService.updateComment(commentId, request).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse body = response.body();

                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    callback.onSuccess(mapComment(body.getData()));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to update comment"));
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void deleteComment(int commentId, final RepositoryCallback<Void> callback) {
        apiService.deleteComment(commentId).enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                EmptyResponse body = response.body();

                if (response.isSuccessful() && body != null && body.success) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(readErrorMessage(body, "Unable to delete comment"));
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    private List<Comment> mapCommentList(List<CommentDto> dtoList) {
        List<Comment> comments = new ArrayList<>();
        if (dtoList == null) return comments;
        for (CommentDto dto : dtoList) {
            comments.add(mapComment(dto));
        }
        return comments;
    }

    public void getUserProfile(String userId, RepositoryCallback<com.example.appdoctruyen.models.User> callback) {
        apiService.getUser(userId).enqueue(new retrofit2.Callback<UserResponse>() {

            @Override
            public void onResponse(retrofit2.Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                UserResponse body = response.body();

                if (response.isSuccessful() && body != null && body.success && body.data != null) {
                    callback.onSuccess(mapUser(body.data));
                } else {
                    callback.onError(readErrorMessage(body, "Unable to load user"));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UserResponse> call, Throwable t) {
                callback.onError("Unable to connect to the Node.js backend");
            }
        });
    }

    public void toggleLikePost(int postId, String userId, final RepositoryCallback<LikeResponse> callback) {
        LikeDto request = new LikeDto(userId);

        apiService.toggleLikePost(postId, request).enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Unable to update like");
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                callback.onError("Connection error: " + t.getLocalizedMessage());
            }
        });
    }

}
