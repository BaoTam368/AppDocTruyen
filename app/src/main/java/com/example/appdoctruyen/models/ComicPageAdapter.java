package com.example.appdoctruyen.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdoctruyen.R;

import java.util.List;

public class ComicPageAdapter extends RecyclerView.Adapter<ComicPageAdapter.PageViewHolder> {
    private Context context;
    private List<ComicPage> pageList;

    public ComicPageAdapter(Context context, List<ComicPage> pageList) {
        this.context = context;
        this.pageList = pageList;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comic_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        ComicPage page = pageList.get(position);
        Glide.with(context)
                .load(page.getImageUrl())
                .into(holder.imgPage);
    }

    @Override
    public int getItemCount() {
        return pageList.size();
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPage;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPage = itemView.findViewById(R.id.imgPage);
        }
    }
}