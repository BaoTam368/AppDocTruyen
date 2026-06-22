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
import java.util.List;

public class GroupRankingAdapter extends RecyclerView.Adapter<GroupRankingAdapter.RankingViewHolder> {

    private Context context;
    private List<TranslationGroup> groupList;
    private OnRankingClickListener listener;

    public GroupRankingAdapter(Context context, List<TranslationGroup> groupList,
                               OnRankingClickListener listener) {
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
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        TranslationGroup group = groupList.get(position);

        int displayRank = group.getRank() > 0 ? group.getRank() : position + 1;
        holder.tvRankNumber.setText(String.valueOf(displayRank));
        holder.tvRankGroupName.setText(group.getName());
        holder.tvRankMemberCount.setText(context.getString(
                R.string.group_followers_count_format,
                group.getFollowerCount()
        ));

        if (group.getAvatarResId() != 0) {
            holder.imgRankAvatar.setImageResource(group.getAvatarResId());
        } else {
            holder.imgRankAvatar.setImageResource(R.drawable.placeholder_group);
        }

        // Hiển thị biểu tượng trophy cho ba nhóm có thứ hạng cao nhất
        if (position < 3) {
            holder.imgRankTrophy.setVisibility(View.VISIBLE);
        } else {
            holder.imgRankTrophy.setVisibility(View.GONE);
        }

        if (position == 0) {
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

    public interface OnRankingClickListener {
        void onRankingClick(TranslationGroup group, int position);
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRankNumber;
        ImageView imgRankAvatar;
        TextView tvRankGroupName;
        TextView tvRankMemberCount;
        ImageView imgRankTrophy;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRankNumber = itemView.findViewById(R.id.tvRankNumber);
            imgRankAvatar = itemView.findViewById(R.id.imgRankAvatar);
            tvRankGroupName = itemView.findViewById(R.id.tvRankGroupName);
            tvRankMemberCount = itemView.findViewById(R.id.tvRankMemberCount);
            imgRankTrophy = itemView.findViewById(R.id.imgRankTrophy);
        }
    }
}
