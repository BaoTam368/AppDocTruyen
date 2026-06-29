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
import com.example.appdoctruyen.models.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Comment comment, int position);
    }

    public CommentAdapter(Context context, List<Comment> commentList, OnItemClickListener listener) {
        this.context = context;
        this.commentList = commentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_world_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        if (comment == null) return;

        holder.tvUsername.setText(comment.getUsername() != null ? comment.getUsername() : "Unknown");
        holder.tvTime.setText(comment.getTime() != null ? comment.getTime() : "");
        holder.tvContent.setText(comment.getContent() != null ? comment.getContent() : "");

        Glide.with(context)
                .load(comment.getAvatarUrl())
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .into(holder.imgAvatar);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(comment, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvUsername, tvTime, tvContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.avatar_cmt);
            tvUsername = itemView.findViewById(R.id.username_cmt);
            tvContent = itemView.findViewById(R.id.content_cmt);
            tvTime = itemView.findViewById(R.id.time_cmt);
        }
    }
}
