package com.example.appdoctruyen.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;

import java.util.List;

public class TopSearchTagAdapter extends RecyclerView.Adapter<TopSearchTagAdapter.TagViewHolder> {

    public interface OnTagClickListener {
        void onTagClick(String tag);
    }

    private final List<String> list;
    private final OnTagClickListener listener;

    public TopSearchTagAdapter(List<String> list) {
        this.list = list;
        this.listener = null;
    }

    public TopSearchTagAdapter(List<String> list, OnTagClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_keyword, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        String tag = list.get(position);
        holder.tvTagName.setText(tag);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTagClick(tag);
            } else {
                Toast.makeText(v.getContext(), "Search: " + tag, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {

        TextView tvTagName;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTagName = itemView.findViewById(R.id.tv_tag_name);
        }
    }
}
