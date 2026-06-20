package com.example.appdoctruyen.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Comic;

import java.util.List;

public class NewComicAdapter extends RecyclerView.Adapter<NewComicAdapter.ViewHolder> {
    private Context context;
    private List<Comic> comicList;

    private OnComicClickListener listener;

    public interface OnComicClickListener {
        void onComicClick(Comic comic, int position);
    }

    public NewComicAdapter(Context context, List<Comic> comicList, OnComicClickListener listener) {
        this.context = context;
        this.comicList = comicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_comic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Comic comic = comicList.get(position);
        holder.ivComic.setImageResource(comic.getCoverImageResId());
        holder.tvTitle.setText(comic.getTitle());
        holder.tvChapter.setText(comic.getLatestChapter());
        holder.tvTime.setText("Cập nhật lúc: " + comic.getDescription());
        holder.tvType.setText(comic.getAuthor());

        holder.itemView.setOnClickListener(v -> {
            listener.onComicClick(comic, position);
        });
    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivComic;
        TextView tvTitle, tvChapter, tvTime, tvType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivComic = itemView.findViewById(R.id.iv_comic_row_image);
            tvTitle = itemView.findViewById(R.id.tv_comic_row_title);
            tvChapter = itemView.findViewById(R.id.tv_comic_row_chapter);
            tvTime = itemView.findViewById(R.id.tv_comic_row_time);
            tvType = itemView.findViewById(R.id.tv_comic_row_type);
        }
    }
}