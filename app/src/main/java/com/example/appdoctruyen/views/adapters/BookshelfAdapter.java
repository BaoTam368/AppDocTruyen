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
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Comic;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.ComicViewHolder> {

    private Context context;
    private List<Comic> comicList;
    private OnItemClickListener listener;

    public BookshelfAdapter(Context context, List<Comic> comicList, OnItemClickListener listener) {
        this.context = context;
        this.comicList = comicList;
        this.listener = listener;
    }

    public void updateList(List<Comic> newList) {
        this.comicList = newList;
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

        // Bind thông tin truyện lên từng item trong danh sách tủ sách
        holder.tvComicTitle.setText(comic.getTitle());

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
        } else if (comic.getLatestChapter() != null && !comic.getLatestChapter().isEmpty()) {
            holder.tvComicChapter.setText(comic.getLatestChapter());
        } else {
            holder.tvComicChapter.setText("");
        }

        // Ưu tiên tải ảnh bìa từ URL, nếu không có thì dùng ảnh placeholder local
        if (comic.getCoverUrl() != null && !comic.getCoverUrl().isEmpty()) {
            Glide.with(context)
                    .load(comic.getCoverUrl())
                    .placeholder(R.drawable.placeholder_comic)
                    .error(R.drawable.placeholder_comic)
                    .into(holder.imgComicCover);
        } else if (comic.getCoverImageResId() != 0) {
            holder.imgComicCover.setImageResource(comic.getCoverImageResId());
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
