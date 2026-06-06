package com.example.appdoctruyen.World;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Post;
import com.bumptech.glide.Glide;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
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
        holder.tvUsername.setText(post.getUsername());
        holder.tvTime.setText(post.getTime());
        holder.tvCaption.setText(post.getCaption());

        // Đổi số thành chuỗi để hiển thị
        holder.tvNumFavourites.setText(String.valueOf(post.getNumLikes()));
        holder.tvNumComments.setText(String.valueOf(post.getNumComments()));

        // Load Avatar
        Glide.with(context)
                .load(post.getAvatarUrl())
                .placeholder(android.R.drawable.ic_menu_myplaces) // Ảnh chờ
                .into(holder.imgAvatar);

        // Load Ảnh bài đăng
        Glide.with(context)
                .load(post.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgPost);
    }

    @Override
    public int getItemCount() {
        if (postList != null) {
            return postList.size();
        }
        return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar, imgPost, iconFavourite, iconComment;
        TextView tvUsername, tvTime, tvCaption, tvNumFavourites, tvNumComments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.avatar_post);
            imgPost = itemView.findViewById(R.id.image_post);
            iconFavourite = itemView.findViewById(R.id.icon_favourite);
            iconComment = itemView.findViewById(R.id.icon_comment);

            tvUsername = itemView.findViewById(R.id.username_post);
            tvTime = itemView.findViewById(R.id.time_post);
            tvCaption = itemView.findViewById(R.id.caption);
            tvNumFavourites = itemView.findViewById(R.id.num_favourites);
            tvNumComments = itemView.findViewById(R.id.num_comment);
        }
    }
}
