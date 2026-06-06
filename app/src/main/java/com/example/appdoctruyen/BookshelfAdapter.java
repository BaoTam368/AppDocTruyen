package com.example.appdoctruyen;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.appdoctruyen.models.Comic;
import java.util.List;

public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.ComicViewHolder> {

    private Context context;
    private List<Comic> comicList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Comic comic, int position);
    }

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

        // Hiển thị tên truyện
        holder.tvComicTitle.setText(comic.getTitle());

        // Hiển thị thông tin chapter
        if (comic.getLastReadChapter() != null && !comic.getLastReadChapter().isEmpty()) {
            holder.tvComicChapter.setText("Vừa đọc: " + comic.getLastReadChapter());
        } else if (comic.getLatestChapter() != null) {
            holder.tvComicChapter.setText(comic.getLatestChapter());
        }

        // Hiển thị ảnh bìa: ưu tiên URL, nếu không có dùng resource ID
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

        // Xử lý click vào item
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
