package com.example.appdoctruyen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.models.TranslationGroup;

import java.util.List;


public class BookshelfGroupAdapter extends RecyclerView.Adapter<BookshelfGroupAdapter.GroupViewHolder> {

    private Context context;
    private List<TranslationGroup> groupList;
    private OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onGroupClick(TranslationGroup group, int position);
    }

    public BookshelfGroupAdapter(Context context, List<TranslationGroup> groupList,
                                 OnGroupClickListener listener) {
        this.context = context;
        this.groupList = groupList;
        this.listener = listener;
    }

    public void updateList(List<TranslationGroup> newList) {
        this.groupList = newList;
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

        holder.tvGroupName.setText(group.getName());
        holder.tvGroupComicCount.setText(group.getComicCount() + " truyện");
        holder.tvGroupMemberCount.setText(String.valueOf(group.getMemberCount()));

        if (group.getAvatarResId() != 0) {
            holder.imgGroupAvatar.setImageResource(group.getAvatarResId());
        } else {
            holder.imgGroupAvatar.setImageResource(R.drawable.placeholder_group);
        }

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
