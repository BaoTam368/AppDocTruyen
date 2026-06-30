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

public class BookshelfDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookshelf.db";
    private static final int DATABASE_VERSION = 3;

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
    private static final String COL_DESCRIPTION_CACHE = "description_cache";
    private static final String COL_STATUS_CACHE = "status_cache";
    private static final String COL_YEAR_CACHE = "year_cache";
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
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS followed_comics");
        createTables(db);
        ensureComicCacheColumns(db, TABLE_BOOKMARKS);
        ensureComicCacheColumns(db, TABLE_HISTORY);
        ensureComicCacheColumns(db, TABLE_DOWNLOADED);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BOOKMARKS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " TEXT NOT NULL, " +
                COL_MANGA_ID + " TEXT NOT NULL, " +
                COL_CREATED_AT + " INTEGER NOT NULL, " +
                COL_TITLE_CACHE + " TEXT, " +
                COL_COVER_URL_CACHE + " TEXT, " +
                COL_DESCRIPTION_CACHE + " TEXT, " +
                COL_STATUS_CACHE + " TEXT, " +
                COL_YEAR_CACHE + " INTEGER, " +
                "UNIQUE(" + COL_USER_ID + ", " + COL_MANGA_ID + "))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " TEXT NOT NULL, " +
                COL_MANGA_ID + " TEXT NOT NULL, " +
                COL_CHAPTER_ID + " TEXT, " +
                COL_CHAPTER_NAME + " TEXT, " +
                COL_LAST_READ_TIME + " INTEGER NOT NULL, " +
                COL_TITLE_CACHE + " TEXT, " +
                COL_COVER_URL_CACHE + " TEXT, " +
                COL_DESCRIPTION_CACHE + " TEXT, " +
                COL_STATUS_CACHE + " TEXT, " +
                COL_YEAR_CACHE + " INTEGER, " +
                "UNIQUE(" + COL_USER_ID + ", " + COL_MANGA_ID + "))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DOWNLOADED + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " TEXT NOT NULL, " +
                COL_MANGA_ID + " TEXT NOT NULL, " +
                COL_CHAPTER_ID + " TEXT, " +
                COL_CHAPTER_NAME + " TEXT, " +
                COL_LOCAL_PATH + " TEXT, " +
                COL_DOWNLOADED_TIME + " INTEGER NOT NULL, " +
                COL_TITLE_CACHE + " TEXT, " +
                COL_COVER_URL_CACHE + " TEXT, " +
                COL_DESCRIPTION_CACHE + " TEXT, " +
                COL_STATUS_CACHE + " TEXT, " +
                COL_YEAR_CACHE + " INTEGER, " +
                "UNIQUE(" + COL_USER_ID + ", " + COL_MANGA_ID + ", " + COL_CHAPTER_ID + "))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COMMENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " TEXT NOT NULL, " +
                COL_MANGA_ID + " TEXT NOT NULL, " +
                COL_COMMENT_CONTENT + " TEXT NOT NULL, " +
                COL_CREATED_AT + " INTEGER NOT NULL, " +
                COL_UPDATED_AT + " INTEGER NOT NULL)");
    }

    public long addBookmark(String userId, String mangaId, String titleCache, String coverUrlCache) {
        return addBookmark(userId, mangaId, titleCache, coverUrlCache, null, null, null);
    }

    public long addBookmark(String userId, Comic comic) {
        if (comic == null) return -1;
        return addBookmark(
                userId,
                comic.getMangaId(),
                comic.getTitle(),
                comic.getCoverUrl(),
                comic.getDescription(),
                comic.getStatus(),
                comic.getYear()
        );
    }

    public long saveBookmark(String userId, Comic comic) {
        return addBookmark(userId, comic);
    }

    public long addBookmark(String userId, String mangaId, String titleCache, String coverUrlCache,
                            String descriptionCache, String statusCache, Integer yearCache) {
        if (isBlank(userId) || isBlank(mangaId)) return -1;

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_MANGA_ID, mangaId);
        values.put(COL_CREATED_AT, System.currentTimeMillis());
        putCacheValues(values, titleCache, coverUrlCache, descriptionCache, statusCache, yearCache, true);

        SQLiteDatabase db = getWritableDatabase();
        long result = db.insertWithOnConflict(TABLE_BOOKMARKS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (result == -1) {
            ContentValues cacheValues = new ContentValues();
            putCacheValues(cacheValues, titleCache, coverUrlCache, descriptionCache, statusCache, yearCache, false);
            if (cacheValues.size() > 0) {
                db.update(TABLE_BOOKMARKS, cacheValues, userMangaWhere(), new String[]{userId, mangaId});
            }
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

    public void saveReadingHistory(String userId, String mangaId, String chapterId, String chapterName,
                                   String titleCache, String coverUrlCache) {
        saveReadingHistory(userId, mangaId, chapterId, chapterName, titleCache, coverUrlCache,
                null, null, null);
    }

    public void saveReadingHistory(String userId, Comic comic) {
        if (comic == null) return;
        saveReadingHistory(
                userId,
                comic.getMangaId(),
                comic.getChapterId(),
                comic.getChapterName(),
                comic.getTitle(),
                comic.getCoverUrl(),
                comic.getDescription(),
                comic.getStatus(),
                comic.getYear()
        );
    }

    public void saveReadingHistory(String userId, String mangaId, String chapterId, String chapterName,
                                   String titleCache, String coverUrlCache, String descriptionCache,
                                   String statusCache, Integer yearCache) {
        if (isBlank(userId) || isBlank(mangaId)) return;

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_MANGA_ID, mangaId);
        values.put(COL_CHAPTER_ID, chapterId);
        values.put(COL_CHAPTER_NAME, chapterName);
        values.put(COL_LAST_READ_TIME, System.currentTimeMillis());
        putCacheValues(values, titleCache, coverUrlCache, descriptionCache, statusCache, yearCache, true);

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
        return addDownloadedComic(userId, mangaId, chapterId, chapterName, localPath, titleCache,
                coverUrlCache, null, null, null);
    }

    public long addDownloadedComic(String userId, Comic comic) {
        if (comic == null) return -1;
        return addDownloadedComic(
                userId,
                comic.getMangaId(),
                comic.getChapterId(),
                comic.getChapterName(),
                comic.getLocalPath(),
                comic.getTitle(),
                comic.getCoverUrl(),
                comic.getDescription(),
                comic.getStatus(),
                comic.getYear()
        );
    }

    public long saveDownloadedComic(String userId, Comic comic) {
        return addDownloadedComic(userId, comic);
    }

    public long addDownloadedComic(String userId, String mangaId, String chapterId, String chapterName,
                                   String localPath, String titleCache, String coverUrlCache,
                                   String descriptionCache, String statusCache, Integer yearCache) {
        if (isBlank(userId) || isBlank(mangaId) || isBlank(chapterId)) return -1;

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_MANGA_ID, mangaId);
        values.put(COL_CHAPTER_ID, chapterId);
        values.put(COL_CHAPTER_NAME, chapterName);
        values.put(COL_LOCAL_PATH, localPath);
        values.put(COL_DOWNLOADED_TIME, System.currentTimeMillis());
        putCacheValues(values, titleCache, coverUrlCache, descriptionCache, statusCache, yearCache, true);

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

    public boolean isDownloaded(String userId, String mangaId) {
        if (isBlank(userId) || isBlank(mangaId)) return false;

        Cursor cursor = getReadableDatabase().query(
                TABLE_DOWNLOADED,
                new String[]{COL_ID},
                COL_USER_ID + " = ? AND " + COL_MANGA_ID + " = ?",
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

    public int removeDownloadedComic(String userId, String mangaId) {
        if (isBlank(userId) || isBlank(mangaId)) return 0;
        return getWritableDatabase().delete(
                TABLE_DOWNLOADED,
                COL_USER_ID + " = ? AND " + COL_MANGA_ID + " = ?",
                new String[]{userId, mangaId}
        );
    }

    public void updateComicCache(String userId, Comic comic) {
        if (isBlank(userId) || comic == null || isBlank(comic.getMangaId())) return;

        ContentValues values = new ContentValues();
        putCacheValues(values, comic.getTitle(), comic.getCoverUrl(), comic.getDescription(),
                comic.getStatus(), comic.getYear(), false);
        if (values.size() == 0) return;

        SQLiteDatabase db = getWritableDatabase();
        String[] args = new String[]{userId, comic.getMangaId()};
        db.update(TABLE_BOOKMARKS, values, userMangaWhere(), args);
        db.update(TABLE_HISTORY, values, userMangaWhere(), args);
        db.update(TABLE_DOWNLOADED, values, userMangaWhere(), args);
    }

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
        Comic comic = createComicFromCache(cursor);
        comic.setFollowed(true);
        return comic;
    }

    private Comic readHistoryComic(Cursor cursor) {
        Comic comic = createComicFromCache(cursor);
        comic.setChapterId(readString(cursor, COL_CHAPTER_ID));
        comic.setChapterName(readString(cursor, COL_CHAPTER_NAME));
        comic.setLastReadChapter(readString(cursor, COL_CHAPTER_NAME));
        comic.setLastReadTime(readLong(cursor, COL_LAST_READ_TIME));
        return comic;
    }

    private Comic readDownloadedComic(Cursor cursor) {
        Comic comic = createComicFromCache(cursor);
        comic.setChapterId(readString(cursor, COL_CHAPTER_ID));
        comic.setChapterName(readString(cursor, COL_CHAPTER_NAME));
        comic.setLatestChapter(readString(cursor, COL_CHAPTER_NAME));
        comic.setLocalPath(readString(cursor, COL_LOCAL_PATH));
        comic.setDownloaded(true);
        return comic;
    }

    private Comic createComicFromCache(Cursor cursor) {
        String mangaId = readString(cursor, COL_MANGA_ID);
        Comic comic = new Comic(
                numericIdFromMangaId(mangaId),
                readString(cursor, COL_TITLE_CACHE),
                R.drawable.placeholder_comic,
                readString(cursor, COL_COVER_URL_CACHE),
                null,
                null,
                null,
                readString(cursor, COL_DESCRIPTION_CACHE),
                false,
                false
        );
        comic.setMangaId(mangaId);
        comic.setStatus(readString(cursor, COL_STATUS_CACHE));
        comic.setYear(readInteger(cursor, COL_YEAR_CACHE));
        return comic;
    }

    private void putCacheValues(ContentValues values, String title, String coverUrl, String description,
                                String status, Integer year, boolean includeNulls) {
        putText(values, COL_TITLE_CACHE, title, includeNulls);
        putText(values, COL_COVER_URL_CACHE, coverUrl, includeNulls);
        putText(values, COL_DESCRIPTION_CACHE, description, includeNulls);
        putText(values, COL_STATUS_CACHE, status, includeNulls);
        if (year != null) {
            values.put(COL_YEAR_CACHE, year);
        } else if (includeNulls) {
            values.putNull(COL_YEAR_CACHE);
        }
    }

    private void putText(ContentValues values, String key, String value, boolean includeNulls) {
        if (!isBlank(value)) {
            values.put(key, value);
        } else if (includeNulls) {
            values.putNull(key);
        }
    }

    private int numericIdFromMangaId(String mangaId) {
        if (isBlank(mangaId)) return 0;
        try {
            return Integer.parseInt(mangaId);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private String readString(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0 || cursor.isNull(index)) return null;
        return cursor.getString(index);
    }

    private Integer readInteger(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0 || cursor.isNull(index)) return null;
        return cursor.getInt(index);
    }

    private long readLong(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0 || cursor.isNull(index)) return 0L;
        return cursor.getLong(index);
    }

    private void ensureComicCacheColumns(SQLiteDatabase db, String tableName) {
        ensureColumn(db, tableName, COL_DESCRIPTION_CACHE, "TEXT");
        ensureColumn(db, tableName, COL_STATUS_CACHE, "TEXT");
        ensureColumn(db, tableName, COL_YEAR_CACHE, "INTEGER");
    }

    private void ensureColumn(SQLiteDatabase db, String tableName, String columnName, String definition) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        try {
            while (cursor.moveToNext()) {
                if (columnName.equals(cursor.getString(cursor.getColumnIndexOrThrow("name")))) {
                    return;
                }
            }
        } finally {
            cursor.close();
        }
        db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
    }

    private String userMangaWhere() {
        return COL_USER_ID + " = ? AND " + COL_MANGA_ID + " = ?";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
