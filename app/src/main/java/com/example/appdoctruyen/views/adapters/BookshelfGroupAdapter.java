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
import com.example.appdoctruyen.models.TranslationGroup;

import java.util.ArrayList;
import java.util.List;

public class BookshelfGroupAdapter extends RecyclerView.Adapter<BookshelfGroupAdapter.GroupViewHolder> {

    private final Context context;
    private List<TranslationGroup> groupList;
    private final OnGroupClickListener listener;

    public BookshelfGroupAdapter(Context context, List<TranslationGroup> groupList,
                                 OnGroupClickListener listener) {
        this.context = context;
        this.groupList = groupList != null ? groupList : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<TranslationGroup> newList) {
        this.groupList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        TranslationGroup group = groupList.get(position);
        if (group == null) {
            holder.tvGroupName.setText(R.string.group_default_name);
            holder.tvGroupComicCount.setText(context.getString(R.string.group_comic_count_format, 0));
            holder.tvGroupMemberCount.setText(context.getString(R.string.group_members_count_format, 0));
            holder.imgGroupAvatar.setImageResource(R.drawable.placeholder_group);
            holder.itemView.setOnClickListener(null);
            return;
        }

        holder.tvGroupName.setText(isBlank(group.getName())
                ? context.getString(R.string.group_default_name)
                : group.getName());
        holder.tvGroupComicCount.setText(context.getString(
                R.string.group_comic_count_format,
                group.getComicCount()
        ));
        holder.tvGroupMemberCount.setText(context.getString(
                R.string.group_members_count_format,
                group.getMemberCount()
        ));

        Glide.with(holder.itemView.getContext())
                .load(group.getAvatarUrl())
                .placeholder(R.drawable.placeholder_group)
                .error(R.drawable.placeholder_group)
                .into(holder.imgGroupAvatar);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGroupClick(group, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList != null ? groupList.size() : 0;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public interface OnGroupClickListener {
        void onGroupClick(TranslationGroup group, int position);
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGroupAvatar;
        TextView tvGroupName;
        TextView tvGroupComicCount;
        TextView tvGroupMemberCount;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGroupAvatar = itemView.findViewById(R.id.imgGroupAvatar);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvGroupComicCount = itemView.findViewById(R.id.tvGroupComicCount);
            tvGroupMemberCount = itemView.findViewById(R.id.tvGroupMemberCount);
        }
    }
}
