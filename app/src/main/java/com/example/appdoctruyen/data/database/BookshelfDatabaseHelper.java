package com.example.appdoctruyen.data.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Comic;
import com.example.appdoctruyen.models.LocalComment;

import java.util.ArrayList;
import java.util.List;

// SQLite helper dùng để lưu bookmark, lịch sử đọc, truyện đã tải và comment local
public class BookshelfDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookshelf.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_BOOKMARKS = "bookmarks";
    private static final String TABLE_HISTORY = "reading_history";
    private static final String TABLE_DOWNLOADED = "downloaded_comics";
    private static final String TABLE_COMMENTS = "local_comments";

    private static final String COL_ID = "id";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_MANGA_ID = "manga_id";
    private static final String COL_CHAPTER_ID = "chapter_id";
    private static final String COL_CHAPTER_NAME = "chapter_name";
    private static final String COL_LOCAL_PATH = "local_path";
    private static final String COL_TITLE_CACHE = "title_cache";
    private static final String COL_COVER_URL_CACHE = "cover_url_cache";
    private static final String COL_COMMENT_CONTENT = "comment_content";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_UPDATED_AT = "updated_at";
    private static final String COL_LAST_READ_TIME = "last_read_time";
    private static final String COL_DOWNLOADED_TIME = "downloaded_time";

    public BookshelfDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_BOOKMARKS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " TEXT NOT NULL, " +
                COL_MANGA_ID + " TEXT NOT NULL, " +
                COL_CREATED_AT + " INTEGER NOT NULL, " +
                COL_TITLE_CACHE + " TEXT, " +
                COL_COVER_URL_CACHE + " TEXT, " +
                "UNIQUE(" + COL_USER_ID + ", " + COL_MANGA_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_HISTORY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " TEXT NOT NULL, " +
                COL_MANGA_ID + " TEXT NOT NULL, " +
                COL_CHAPTER_ID + " TEXT, " +
                COL_CHAPTER_NAME + " TEXT, " +
                COL_LAST_READ_TIME + " INTEGER NOT NULL, " +
                COL_TITLE_CACHE + " TEXT, " +
                COL_COVER_URL_CACHE + " TEXT, " +
                "UNIQUE(" + COL_USER_ID + ", " + COL_MANGA_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_DOWNLOADED + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " TEXT NOT NULL, " +
                COL_MANGA_ID + " TEXT NOT NULL, " +
                COL_CHAPTER_ID + " TEXT, " +
                COL_CHAPTER_NAME + " TEXT, " +
                COL_LOCAL_PATH + " TEXT, " +
                COL_DOWNLOADED_TIME + " INTEGER NOT NULL, " +
                COL_TITLE_CACHE + " TEXT, " +
                COL_COVER_URL_CACHE + " TEXT, " +
                "UNIQUE(" + COL_USER_ID + ", " + COL_MANGA_ID + ", " + COL_CHAPTER_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_COMMENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " TEXT NOT NULL, " +
                COL_MANGA_ID + " TEXT NOT NULL, " +
                COL_COMMENT_CONTENT + " TEXT NOT NULL, " +
                COL_CREATED_AT + " INTEGER NOT NULL, " +
                COL_UPDATED_AT + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS followed_comics");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long addBookmark(String userId, String mangaId, String titleCache, String coverUrlCache) {
        if (isBlank(userId) || isBlank(mangaId)) return -1;

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_MANGA_ID, mangaId);
        values.put(COL_TITLE_CACHE, titleCache);
        values.put(COL_COVER_URL_CACHE, coverUrlCache);
        values.put(COL_CREATED_AT, System.currentTimeMillis());

        SQLiteDatabase db = getWritableDatabase();
        long result = db.insertWithOnConflict(TABLE_BOOKMARKS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (result == -1) {
            ContentValues cacheValues = new ContentValues();
            cacheValues.put(COL_TITLE_CACHE, titleCache);
            cacheValues.put(COL_COVER_URL_CACHE, coverUrlCache);
            db.update(TABLE_BOOKMARKS, cacheValues, userMangaWhere(), new String[]{userId, mangaId});
        }
        return result;
    }

    public int removeBookmark(String userId, String mangaId) {
        if (isBlank(userId) || isBlank(mangaId)) return 0;
        return getWritableDatabase().delete(TABLE_BOOKMARKS, userMangaWhere(), new String[]{userId, mangaId});
    }

    public boolean isBookmarked(String userId, String mangaId) {
        if (isBlank(userId) || isBlank(mangaId)) return false;

        Cursor cursor = getReadableDatabase().query(
                TABLE_BOOKMARKS,
                new String[]{COL_ID},
                userMangaWhere(),
                new String[]{userId, mangaId},
                null,
                null,
                null
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    public List<Comic> getBookmarks(String userId) {
        List<Comic> comics = new ArrayList<>();
        if (isBlank(userId)) return comics;

        Cursor cursor = getReadableDatabase().query(
                TABLE_BOOKMARKS,
                null,
                COL_USER_ID + " = ?",
                new String[]{userId},
                null,
                null,
                COL_CREATED_AT + " DESC"
        );

        try {
            while (cursor.moveToNext()) {
                comics.add(readBookmarkComic(cursor));
            }
        } finally {
            cursor.close();
        }
        return comics;
    }

    // Lưu hoặc cập nhật chapter đọc gần nhất của một truyện.
    // Màn đọc truyện có thể gọi hàm này khi người dùng mở chapter để lưu lịch sử đọc.
    public void saveReadingHistory(String userId, String mangaId, String chapterId, String chapterName,
                                   String titleCache, String coverUrlCache) {
        if (isBlank(userId) || isBlank(mangaId)) return;

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_MANGA_ID, mangaId);
        values.put(COL_CHAPTER_ID, chapterId);
        values.put(COL_CHAPTER_NAME, chapterName);
        values.put(COL_LAST_READ_TIME, System.currentTimeMillis());
        values.put(COL_TITLE_CACHE, titleCache);
        values.put(COL_COVER_URL_CACHE, coverUrlCache);

        SQLiteDatabase db = getWritableDatabase();
        int updated = db.update(TABLE_HISTORY, values, userMangaWhere(), new String[]{userId, mangaId});
        if (updated == 0) {
            db.insert(TABLE_HISTORY, null, values);
        }
    }

    public List<Comic> getReadingHistory(String userId) {
        List<Comic> comics = new ArrayList<>();
        if (isBlank(userId)) return comics;

        Cursor cursor = getReadableDatabase().query(
                TABLE_HISTORY,
                null,
                COL_USER_ID + " = ?",
                new String[]{userId},
                null,
                null,
                COL_LAST_READ_TIME + " DESC"
        );

        try {
            while (cursor.moveToNext()) {
                comics.add(readHistoryComic(cursor));
            }
        } finally {
            cursor.close();
        }
        return comics;
    }

    public Comic getReadingHistoryForManga(String userId, String mangaId) {
        if (isBlank(userId) || isBlank(mangaId)) return null;

        Cursor cursor = getReadableDatabase().query(
                TABLE_HISTORY,
                null,
                userMangaWhere(),
                new String[]{userId, mangaId},
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return readHistoryComic(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public long addDownloadedComic(String userId, String mangaId, String chapterId, String chapterName,
                                   String localPath, String titleCache, String coverUrlCache) {
        if (isBlank(userId) || isBlank(mangaId)) return -1;

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_MANGA_ID, mangaId);
        values.put(COL_CHAPTER_ID, chapterId);
        values.put(COL_CHAPTER_NAME, chapterName);
        values.put(COL_LOCAL_PATH, localPath);
        values.put(COL_DOWNLOADED_TIME, System.currentTimeMillis());
        values.put(COL_TITLE_CACHE, titleCache);
        values.put(COL_COVER_URL_CACHE, coverUrlCache);

        return getWritableDatabase().insertWithOnConflict(
                TABLE_DOWNLOADED,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public List<Comic> getDownloadedComics(String userId) {
        List<Comic> comics = new ArrayList<>();
        if (isBlank(userId)) return comics;

        Cursor cursor = getReadableDatabase().query(
                TABLE_DOWNLOADED,
                null,
                COL_USER_ID + " = ?",
                new String[]{userId},
                null,
                null,
                COL_DOWNLOADED_TIME + " DESC"
        );

        try {
            while (cursor.moveToNext()) {
                comics.add(readDownloadedComic(cursor));
            }
        } finally {
            cursor.close();
        }
        return comics;
    }

    // Comment chỉ được lưu local trên máy, không gửi lên server
    public long addLocalComment(String userId, String mangaId, String content) {
        if (isBlank(userId) || isBlank(mangaId) || isBlank(content)) return -1;

        long now = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_MANGA_ID, mangaId);
        values.put(COL_COMMENT_CONTENT, content.trim());
        values.put(COL_CREATED_AT, now);
        values.put(COL_UPDATED_AT, now);
        return getWritableDatabase().insert(TABLE_COMMENTS, null, values);
    }

    public int updateLocalComment(long commentId, String content) {
        if (commentId <= 0 || isBlank(content)) return 0;

        ContentValues values = new ContentValues();
        values.put(COL_COMMENT_CONTENT, content.trim());
        values.put(COL_UPDATED_AT, System.currentTimeMillis());
        return getWritableDatabase().update(TABLE_COMMENTS, values, COL_ID + " = ?",
                new String[]{String.valueOf(commentId)});
    }

    public int deleteLocalComment(long commentId) {
        if (commentId <= 0) return 0;
        return getWritableDatabase().delete(TABLE_COMMENTS, COL_ID + " = ?",
                new String[]{String.valueOf(commentId)});
    }

    public List<LocalComment> getLocalComments(String userId, String mangaId) {
        List<LocalComment> comments = new ArrayList<>();
        if (isBlank(userId) || isBlank(mangaId)) return comments;

        Cursor cursor = getReadableDatabase().query(
                TABLE_COMMENTS,
                null,
                userMangaWhere(),
                new String[]{userId, mangaId},
                null,
                null,
                COL_CREATED_AT + " DESC"
        );

        try {
            while (cursor.moveToNext()) {
                comments.add(new LocalComment(
                        readLong(cursor, COL_ID),
                        readString(cursor, COL_USER_ID),
                        readString(cursor, COL_MANGA_ID),
                        readString(cursor, COL_COMMENT_CONTENT),
                        readLong(cursor, COL_CREATED_AT),
                        readLong(cursor, COL_UPDATED_AT)
                ));
            }
        } finally {
            cursor.close();
        }
        return comments;
    }

    private Comic readBookmarkComic(Cursor cursor) {
        Comic comic = createComicFromCache(
                readString(cursor, COL_MANGA_ID),
                readString(cursor, COL_TITLE_CACHE),
                readString(cursor, COL_COVER_URL_CACHE)
        );
        comic.setFollowed(true);
        return comic;
    }

    private Comic readHistoryComic(Cursor cursor) {
        Comic comic = createComicFromCache(
                readString(cursor, COL_MANGA_ID),
                readString(cursor, COL_TITLE_CACHE),
                readString(cursor, COL_COVER_URL_CACHE)
        );
        comic.setChapterId(readString(cursor, COL_CHAPTER_ID));
        comic.setChapterName(readString(cursor, COL_CHAPTER_NAME));
        comic.setLastReadChapter(readString(cursor, COL_CHAPTER_NAME));
        comic.setLastReadTime(readLong(cursor, COL_LAST_READ_TIME));
        return comic;
    }

    private Comic readDownloadedComic(Cursor cursor) {
        Comic comic = createComicFromCache(
                readString(cursor, COL_MANGA_ID),
                readString(cursor, COL_TITLE_CACHE),
                readString(cursor, COL_COVER_URL_CACHE)
        );
        comic.setChapterId(readString(cursor, COL_CHAPTER_ID));
        comic.setChapterName(readString(cursor, COL_CHAPTER_NAME));
        comic.setLatestChapter(readString(cursor, COL_CHAPTER_NAME));
        comic.setLocalPath(readString(cursor, COL_LOCAL_PATH));
        comic.setDownloaded(true);
        return comic;
    }

    private Comic createComicFromCache(String mangaId, String titleCache, String coverUrlCache) {
        Comic comic = new Comic(
                numericIdFromMangaId(mangaId),
                isBlank(titleCache) ? "Manga " + mangaId : titleCache,
                R.drawable.placeholder_comic,
                coverUrlCache,
                null,
                null,
                null,
                null,
                false,
                false
        );
        comic.setMangaId(mangaId);
        return comic;
    }

    private int numericIdFromMangaId(String mangaId) {
        if (isBlank(mangaId)) return 0;
        try {
            return Integer.parseInt(mangaId);
        } catch (NumberFormatException ignored) {
            return Math.abs(mangaId.hashCode());
        }
    }

    private String readString(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0 || cursor.isNull(index)) return null;
        return cursor.getString(index);
    }

    private long readLong(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0 || cursor.isNull(index)) return 0L;
        return cursor.getLong(index);
    }

    private String userMangaWhere() {
        return COL_USER_ID + " = ? AND " + COL_MANGA_ID + " = ?";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}