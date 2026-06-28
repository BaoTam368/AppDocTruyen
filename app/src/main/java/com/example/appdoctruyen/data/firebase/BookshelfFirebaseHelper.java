package com.example.appdoctruyen.data.firebase;

import android.util.Log;

import com.example.appdoctruyen.models.Comic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookshelfFirebaseHelper {
    private static final String TAG = "BookshelfFirebaseHelper";
    private DatabaseReference databaseReference;
    private DatabaseReference userBookshelfRef;

    public BookshelfFirebaseHelper(String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        userBookshelfRef = databaseReference.child("users").child(userId).child("bookshelf");
    }

    public void addBookmark(String mangaId, String title, String coverUrl, String latestChapter) {
        Map<String, Object> bookmarkData = new HashMap<>();
        bookmarkData.put("mangaId", mangaId);
        bookmarkData.put("title", title);
        bookmarkData.put("coverUrl", coverUrl);
        bookmarkData.put("latestChapter", latestChapter);
        bookmarkData.put("timestamp", System.currentTimeMillis());
        bookmarkData.put("type", "bookmark");

        userBookshelfRef.child(mangaId).setValue(bookmarkData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Bookmark added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add bookmark", e));
    }

    public void removeBookmark(String mangaId) {
        userBookshelfRef.child(mangaId).removeValue()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Bookmark removed successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to remove bookmark", e));
    }

    public void saveReadingHistory(String mangaId, String chapterId, String chapterName, 
                                   String title, String coverUrl) {
        Map<String, Object> historyData = new HashMap<>();
        historyData.put("mangaId", mangaId);
        historyData.put("chapterId", chapterId);
        historyData.put("chapterName", chapterName);
        historyData.put("title", title);
        historyData.put("coverUrl", coverUrl);
        historyData.put("lastReadTime", System.currentTimeMillis());
        historyData.put("type", "history");

        userBookshelfRef.child(mangaId).setValue(historyData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Reading history saved successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save reading history", e));
    }

    public void addDownloadedComic(String mangaId, String chapterId, String chapterName,
                                   String localPath, String title, String coverUrl) {
        Map<String, Object> downloadedData = new HashMap<>();
        downloadedData.put("mangaId", mangaId);
        downloadedData.put("chapterId", chapterId);
        downloadedData.put("chapterName", chapterName);
        downloadedData.put("localPath", localPath);
        downloadedData.put("title", title);
        downloadedData.put("coverUrl", coverUrl);
        downloadedData.put("downloadedTime", System.currentTimeMillis());
        downloadedData.put("type", "downloaded");

        userBookshelfRef.child(mangaId).setValue(downloadedData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Downloaded comic added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add downloaded comic", e));
    }

    public void getBookmarks(BookshelfCallback callback) {
        userBookshelfRef.orderByChild("type").equalTo("bookmark")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Comic> comics = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Comic comic = snapshotToComic(snapshot);
                            if (comic != null) {
                                comic.setFollowed(true);
                                comics.add(comic);
                            }
                        }
                        callback.onSuccess(comics);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Failed to get bookmarks", error.toException());
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void getReadingHistory(BookshelfCallback callback) {
        userBookshelfRef.orderByChild("type").equalTo("history")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Comic> comics = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Comic comic = snapshotToComic(snapshot);
                            if (comic != null) {
                                comics.add(comic);
                            }
                        }
                        callback.onSuccess(comics);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Failed to get reading history", error.toException());
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void getDownloadedComics(BookshelfCallback callback) {
        userBookshelfRef.orderByChild("type").equalTo("downloaded")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Comic> comics = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Comic comic = snapshotToComic(snapshot);
                            if (comic != null) {
                                comic.setDownloaded(true);
                                comics.add(comic);
                            }
                        }
                        callback.onSuccess(comics);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Failed to get downloaded comics", error.toException());
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    private Comic snapshotToComic(DataSnapshot snapshot) {
        try {
            String mangaId = snapshot.child("mangaId").getValue(String.class);
            String title = snapshot.child("title").getValue(String.class);
            String coverUrl = snapshot.child("coverUrl").getValue(String.class);
            String latestChapter = snapshot.child("latestChapter").getValue(String.class);
            String chapterId = snapshot.child("chapterId").getValue(String.class);
            String chapterName = snapshot.child("chapterName").getValue(String.class);
            String localPath = snapshot.child("localPath").getValue(String.class);
            Long lastReadTime = snapshot.child("lastReadTime").getValue(Long.class);

            int id = mangaId != null ? Math.abs(mangaId.hashCode()) : 0;
            Comic comic = new Comic(id, 
                    title != null ? title : "Unknown", 
                    0, 
                    coverUrl, 
                    latestChapter, 
                    null, 
                    null, 
                    null, 
                    false, 
                    false);
            
            comic.setMangaId(mangaId);
            comic.setChapterId(chapterId);
            comic.setChapterName(chapterName);
            comic.setLastReadChapter(chapterName);
            comic.setLocalPath(localPath);
            
            if (lastReadTime != null) {
                comic.setLastReadTime(lastReadTime);
            }
            
            return comic;
        } catch (Exception e) {
            Log.e(TAG, "Error converting snapshot to comic", e);
            return null;
        }
    }

    public interface BookshelfCallback {
        void onSuccess(List<Comic> comics);
        void onFailure(String errorMessage);
    }
}
