package com.example.appdoctruyen.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Comic;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.ComicViewHolder> {

    private static final String IMAGE_USER_AGENT = "AppDocTruyenAndroid/1.0";

    private final Context context;
    private List<Comic> comicList;
    private final OnItemClickListener listener;

    public BookshelfAdapter(Context context, List<Comic> comicList, OnItemClickListener listener) {
        this.context = context;
        this.comicList = comicList != null ? comicList : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<Comic> newList) {
        this.comicList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comic, parent, false);
        return new ComicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicViewHolder holder, int position) {
        Comic comic = comicList.get(position);
        if (comic == null) {
            holder.tvComicTitle.setText(R.string.comic_title_placeholder);
            holder.tvComicChapter.setText("");
            holder.imgComicCover.setImageResource(R.drawable.placeholder_comic);
            holder.itemView.setOnClickListener(null);
            return;
        }

        holder.tvComicTitle.setText(isBlank(comic.getTitle())
                ? context.getString(R.string.comic_title_placeholder)
                : comic.getTitle());

        String lastReadChapter = !isBlank(comic.getLastReadChapter())
                ? comic.getLastReadChapter()
                : comic.getChapterName();

        if (!isBlank(lastReadChapter) && comic.getLastReadTime() > 0) {
            holder.tvComicChapter.setText(context.getString(
                    R.string.bookshelf_last_read_with_time_format,
                    lastReadChapter,
                    formatTime(comic.getLastReadTime())
            ));
        } else if (!isBlank(lastReadChapter)) {
            holder.tvComicChapter.setText(context.getString(
                    R.string.bookshelf_last_read_format,
                    lastReadChapter
            ));
        } else if (!isBlank(comic.getLatestChapter())) {
            holder.tvComicChapter.setText(comic.getLatestChapter());
        } else {
            holder.tvComicChapter.setText("");
        }

        String coverUrl = comic.getCoverUrl() != null ? comic.getCoverUrl().trim() : "";
        if (!coverUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(buildImageRequest(coverUrl))
                    .placeholder(R.drawable.placeholder_comic)
                    .error(R.drawable.placeholder_comic)
                    .centerCrop()
                    .into(holder.imgComicCover);
        } else {
            holder.imgComicCover.setImageResource(R.drawable.placeholder_comic);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(comic, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comicList != null ? comicList.size() : 0;
    }

    private GlideUrl buildImageRequest(String coverUrl) {
        return new GlideUrl(coverUrl, new LazyHeaders.Builder()
                .addHeader("User-Agent", IMAGE_USER_AGENT)
                .build());
    }

    private String formatTime(long timeMillis) {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(new Date(timeMillis));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public interface OnItemClickListener {
        void onItemClick(Comic comic, int position);
    }

    public static class ComicViewHolder extends RecyclerView.ViewHolder {
        ImageView imgComicCover;
        TextView tvComicTitle;
        TextView tvComicChapter;

        public ComicViewHolder(@NonNull View itemView) {
            super(itemView);
            imgComicCover = itemView.findViewById(R.id.imgComicCover);
            tvComicTitle = itemView.findViewById(R.id.tvComicTitle);
            tvComicChapter = itemView.findViewById(R.id.tvComicChapter);
        }
    }
}
