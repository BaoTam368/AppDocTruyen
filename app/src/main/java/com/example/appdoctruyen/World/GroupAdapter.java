package com.example.appdoctruyen.World;
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
import com.example.appdoctruyen.models.Group;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private Context context;
    private List<Group> groupList;

    public GroupAdapter(Context context, List<Group> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_world_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.tvGroupName.setText(group.getGroupName());
        holder.tvTime.setText(group.getTime());
        holder.tvCaption.setText(group.getCaption());
        holder.tvNumFavourites.setText(String.valueOf(group.getNumLikes()));
        holder.tvNumComments.setText(String.valueOf(group.getNumComments()));

        Glide.with(context)
                .load(group.getAvatarUrl())
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .into(holder.imgGroupAvatar);
    }

    @Override
    public int getItemCount() {
        return groupList != null ? groupList.size() : 0;
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGroupAvatar, iconFavourite, iconComment;
        TextView tvGroupName, tvTime, tvCaption, tvNumFavourites, tvNumComments;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGroupAvatar = itemView.findViewById(R.id.avatar_post_group);
            tvGroupName = itemView.findViewById(R.id.username_post_group);
            tvTime = itemView.findViewById(R.id.time_post_group);
            tvCaption = itemView.findViewById(R.id.caption_group);
            iconFavourite = itemView.findViewById(R.id.icon_favourite);
            iconComment = itemView.findViewById(R.id.icon_comment);
            tvNumFavourites = itemView.findViewById(R.id.num_favourites);
            tvNumComments = itemView.findViewById(R.id.num_comment);
        }
    }
}
