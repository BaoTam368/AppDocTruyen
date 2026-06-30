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
import com.example.appdoctruyen.models.TranslationGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupRankingAdapter extends RecyclerView.Adapter<GroupRankingAdapter.RankingViewHolder> {

    private final Context context;
    private List<TranslationGroup> groupList;
    private final OnRankingClickListener listener;

    public GroupRankingAdapter(Context context, List<TranslationGroup> groupList,
                               OnRankingClickListener listener) {
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
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        TranslationGroup group = groupList.get(position);
        if (group == null) {
            holder.tvRankNumber.setText("-");
            holder.tvRankGroupName.setText(R.string.group_default_name);
            holder.tvRankMemberCount.setText(R.string.group_stats_unavailable_short);
            holder.imgRankAvatar.setVisibility(View.GONE);
            holder.tvRankInitials.setVisibility(View.VISIBLE);
            holder.tvRankInitials.setText("?");
            holder.imgRankTrophy.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);
            return;
        }

        boolean hasRank = group.getRank() > 0;
        holder.tvRankNumber.setText(hasRank ? String.valueOf(group.getRank()) : "-");
        holder.tvRankGroupName.setText(isBlank(group.getName())
                ? context.getString(R.string.group_default_name)
                : group.getName());
        holder.tvRankMemberCount.setText(formatCount(group.getComicCount(), R.string.group_comic_count_format));

        // Initials avatar
        BookshelfGroupAdapter.bindInitialsAvatar(holder.imgRankAvatar, holder.tvRankInitials,
                group.getName(), group.getGroupId());

        holder.imgRankTrophy.setVisibility(hasRank && position < 3 ? View.VISIBLE : View.GONE);

        if (!hasRank) {
            holder.tvRankNumber.setTextColor(context.getResources().getColor(R.color.text_secondary_light, null));
        } else if (position == 0) {
            holder.tvRankNumber.setTextColor(context.getResources().getColor(R.color.badge_red, null));
        } else if (position < 3) {
            holder.tvRankNumber.setTextColor(context.getResources().getColor(R.color.brand_blue, null));
        } else {
            holder.tvRankNumber.setTextColor(context.getResources().getColor(R.color.text_secondary_light, null));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRankingClick(group, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList != null ? groupList.size() : 0;
    }

    private String formatCount(int value, int formatResId) {
        return value > 0
                ? context.getString(formatResId, value)
                : context.getString(R.string.group_stats_unavailable_short);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public interface OnRankingClickListener {
        void onRankingClick(TranslationGroup group, int position);
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRankNumber;
        ImageView imgRankAvatar;
        TextView tvRankInitials;
        TextView tvRankGroupName;
        TextView tvRankMemberCount;
        ImageView imgRankTrophy;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRankNumber = itemView.findViewById(R.id.tvRankNumber);
            imgRankAvatar = itemView.findViewById(R.id.imgRankAvatar);
            tvRankInitials = itemView.findViewById(R.id.tvRankInitials);
            tvRankGroupName = itemView.findViewById(R.id.tvRankGroupName);
            tvRankMemberCount = itemView.findViewById(R.id.tvRankMemberCount);
            imgRankTrophy = itemView.findViewById(R.id.imgRankTrophy);
        }
    }
}