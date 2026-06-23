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

public class FeaturedComicAdapter extends RecyclerView.Adapter<FeaturedComicAdapter.FeaturedComicViewHolder> {

    private Context context;
    private List<Comic> comicList;
    private OnFeaturedComicClickListener listener;

    public FeaturedComicAdapter(Context context,
                                List<Comic> comicList,
                                OnFeaturedComicClickListener listener) {
        this.context = context;
        this.comicList = comicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeaturedComicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comic_large, parent, false);
        return new FeaturedComicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedComicViewHolder holder, int position) {
        Comic comic = comicList.get(position);

        holder.tvComicLargeTitle.setText(comic.getTitle());

        if (comic.getCoverImageResId() != 0) {
            holder.ivComicLargeCover.setImageResource(comic.getCoverImageResId());
        } else {
            holder.ivComicLargeCover.setImageResource(R.drawable.placeholder_comic);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFeaturedComicClick(comic, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comicList != null ? comicList.size() : 0;
    }

    public interface OnFeaturedComicClickListener {
        void onFeaturedComicClick(Comic comic, int position);
    }

    public static class FeaturedComicViewHolder extends RecyclerView.ViewHolder {
        ImageView ivComicLargeCover;
        TextView tvComicLargeTitle;

        public FeaturedComicViewHolder(@NonNull View itemView) {
            super(itemView);
            ivComicLargeCover = itemView.findViewById(R.id.iv_comic_large_cover);
            tvComicLargeTitle = itemView.findViewById(R.id.tv_comic_large_title);
        }
    }
}