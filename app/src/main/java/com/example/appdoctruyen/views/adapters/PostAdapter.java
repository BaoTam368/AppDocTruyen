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
import com.example.appdoctruyen.models.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private OnLikeClickListener likeClickListener;

    public interface OnLikeClickListener {
        void onLikeClick(Post post, int position, TextView tvLikeCount);
    }
    public PostAdapter(Context context, List<Post> postList, OnLikeClickListener likeClickListener) {
        this.context = context;
        this.postList = postList;
        this.likeClickListener = likeClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_world_feed, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = postList.get(position);
        if (post == null) return;

        holder.tvUsername.setText(post.getDisplayName() != null ? post.getDisplayName() : "Unknown");
        holder.tvTime.setText(post.getCreatedAt() != null ? post.getCreatedAt() : "");
        holder.tvCaption.setText(post.getContent() != null ? post.getContent() : "");
        holder.tvNumFavourites.setText(String.valueOf(post.getLikeCount()));

        Glide.with(context).load(post.getAvatarUrl()).placeholder(android.R.drawable.ic_menu_myplaces).error(android.R.drawable.ic_menu_myplaces).into(holder.imgAvatar);
        Glide.with(context).load(post.getImageUrl()).placeholder(android.R.drawable.ic_menu_gallery).error(android.R.drawable.ic_menu_gallery).into(holder.imgPost);

        holder.imgLike.setOnClickListener(v -> {
            if (likeClickListener != null) {
                likeClickListener.onLikeClick(post, position, holder.tvNumFavourites);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAvatar, imgPost, imgLike;
        TextView tvUsername, tvTime, tvCaption, tvNumFavourites, tvNumComments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.avatar_post);
            imgPost = itemView.findViewById(R.id.image_post);

            tvUsername = itemView.findViewById(R.id.username_post);
            tvTime = itemView.findViewById(R.id.time_post);
            tvCaption = itemView.findViewById(R.id.caption);

            imgLike = itemView.findViewById(R.id.icon_favourite);
            tvNumFavourites = itemView.findViewById(R.id.num_favourites);

        }
    }
}