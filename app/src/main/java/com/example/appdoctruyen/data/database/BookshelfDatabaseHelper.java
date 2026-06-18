package com.example.appdoctruyen.data.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Comic;

import java.util.ArrayList;
import java.util.List;

// SQLite helper dùng để lưu truyện theo dõi, lịch sử đọc và truyện đã tải.
public class BookshelfDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookshelf.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_FOLLOWED = "followed_comics";
    private static final String TABLE_HISTORY = "reading_history";
    private static final String TABLE_DOWNLOADED = "downloaded_comics";

    private static final String COL_ID = "id";
    private static final String COL_COMIC_ID = "comic_id";
    private static final String COL_TITLE = "title";
    private static final String COL_COVER_URL = "cover_url";
    private static final String COL_LATEST_CHAPTER = "latest_chapter";
    private static final String COL_AUTHOR = "author";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_LAST_READ_CHAPTER = "last_read_chapter";
    private static final String COL_LAST_READ_TIME = "last_read_time";
    private static final String COL_DOWNLOADED_CHAPTER = "downloaded_chapter";
    private static final String COL_DOWNLOADED_TIME = "downloaded_time";

    public BookshelfDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_FOLLOWED + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COMIC_ID + " TEXT UNIQUE, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_COVER_URL + " TEXT, " +
                COL_LATEST_CHAPTER + " TEXT, " +
                COL_AUTHOR + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_CREATED_AT + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_HISTORY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COMIC_ID + " TEXT UNIQUE, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_COVER_URL + " TEXT, " +
                COL_LAST_READ_CHAPTER + " TEXT, " +
                COL_LAST_READ_TIME + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_DOWNLOADED + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COMIC_ID + " TEXT UNIQUE, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_COVER_URL + " TEXT, " +
                COL_DOWNLOADED_CHAPTER + " TEXT, " +
                COL_DOWNLOADED_TIME + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLLOWED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADED);
        onCreate(db);
    }

    public void addFollowedComic(Comic comic) {
        if (comic == null) return;
        addFollowedComic(
                String.valueOf(comic.getId()),
                comic.getTitle(),
                comic.getCoverUrl(),
                comic.getLatestChapter(),
                comic.getAuthor(),
                comic.getDescription()
        );
    }

    // Lưu hoặc cập nhật một truyện đang theo dõi.
    public void addFollowedComic(String comicId, String title, String coverUrl,
                                 String latestChapter, String author, String description) {
        if (isBlank(comicId) || isBlank(title)) return;

        ContentValues values = new ContentValues();
        values.put(COL_COMIC_ID, comicId);
        values.put(COL_TITLE, title);
        values.put(COL_COVER_URL, coverUrl);
        values.put(COL_LATEST_CHAPTER, latestChapter);
        values.put(COL_AUTHOR, author);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_CREATED_AT, System.currentTimeMillis());

        getWritableDatabase().insertWithOnConflict(
                TABLE_FOLLOWED,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public void removeFollowedComic(String comicId) {
        if (isBlank(comicId)) return;
        getWritableDatabase().delete(TABLE_FOLLOWED, COL_COMIC_ID + " = ?", new String[]{comicId});
    }

    public List<Comic> getFollowedComics() {
        List<Comic> comics = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(
                TABLE_FOLLOWED,
                null,
                null,
                null,
                null,
                null,
                COL_CREATED_AT + " DESC"
        );

        try {
            while (cursor.moveToNext()) {
                comics.add(readFollowedComic(cursor));
            }
        } finally {
            cursor.close();
        }
        return comics;
    }

    // Hàm này dùng để lưu chapter đọc gần nhất
    // Màn đọc truyện có thể gọi lại khi người dùng mở chapter
    public void saveLastRead(String comicId, String title, String coverUrl, String chapterName) {
        if (isBlank(comicId) || isBlank(title)) return;

        ContentValues values = new ContentValues();
        values.put(COL_COMIC_ID, comicId);
        values.put(COL_TITLE, title);
        values.put(COL_COVER_URL, coverUrl);
        values.put(COL_LAST_READ_CHAPTER, chapterName);
        values.put(COL_LAST_READ_TIME, System.currentTimeMillis());

        getWritableDatabase().insertWithOnConflict(
                TABLE_HISTORY,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public List<Comic> getReadingHistory() {
        List<Comic> comics = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(
                TABLE_HISTORY,
                null,
                null,
                null,
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

    public void addDownloadedComic(Comic comic, String downloadedChapter) {
        if (comic == null) return;
        addDownloadedComic(
                String.valueOf(comic.getId()),
                comic.getTitle(),
                comic.getCoverUrl(),
                downloadedChapter
        );
    }

    public void addDownloadedComic(String comicId, String title, String coverUrl, String downloadedChapter) {
        if (isBlank(comicId) || isBlank(title)) return;

        ContentValues values = new ContentValues();
        values.put(COL_COMIC_ID, comicId);
        values.put(COL_TITLE, title);
        values.put(COL_COVER_URL, coverUrl);
        values.put(COL_DOWNLOADED_CHAPTER, downloadedChapter);
        values.put(COL_DOWNLOADED_TIME, System.currentTimeMillis());

        getWritableDatabase().insertWithOnConflict(
                TABLE_DOWNLOADED,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public List<Comic> getDownloadedComics() {
        List<Comic> comics = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(
                TABLE_DOWNLOADED,
                null,
                null,
                null,
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

    private Comic readFollowedComic(Cursor cursor) {
        Comic comic = new Comic(
                readComicId(cursor),
                readString(cursor, COL_TITLE),
                R.drawable.placeholder_comic,
                readString(cursor, COL_COVER_URL),
                readString(cursor, COL_LATEST_CHAPTER),
                null,
                readString(cursor, COL_AUTHOR),
                readString(cursor, COL_DESCRIPTION),
                true,
                false
        );
        comic.setFollowed(true);
        return comic;
    }

    private Comic readHistoryComic(Cursor cursor) {
        Comic comic = new Comic(
                readComicId(cursor),
                readString(cursor, COL_TITLE),
                R.drawable.placeholder_comic,
                readString(cursor, COL_COVER_URL),
                null,
                readString(cursor, COL_LAST_READ_CHAPTER),
                null,
                null,
                false,
                false
        );
        comic.setLastReadChapter(readString(cursor, COL_LAST_READ_CHAPTER));
        return comic;
    }

    private Comic readDownloadedComic(Cursor cursor) {
        Comic comic = new Comic(
                readComicId(cursor),
                readString(cursor, COL_TITLE),
                R.drawable.placeholder_comic,
                readString(cursor, COL_COVER_URL),
                readString(cursor, COL_DOWNLOADED_CHAPTER),
                null,
                null,
                null,
                false,
                true
        );
        comic.setDownloaded(true);
        return comic;
    }

    private int readComicId(Cursor cursor) {
        String comicId = readString(cursor, COL_COMIC_ID);
        if (isBlank(comicId)) return 0;
        try {
            return Integer.parseInt(comicId);
        } catch (NumberFormatException ignored) {
            return Math.abs(comicId.hashCode());
        }
    }

    private String readString(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0 || cursor.isNull(index)) return null;
        return cursor.getString(index);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}