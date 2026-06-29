package com.example.appdoctruyen.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.User;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<User> list;

    public RankingAdapter(List<User> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = list.get(position);
//        holder.tvRank.setText(String.valueOf(user.getRank()));
//        holder.tvName.setText(user.getName());
//        holder.ivAvatar.setImageResource(user.getAvatar());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRank, tvName;
        ShapeableImageView ivAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRank = itemView.findViewById(R.id.tv_rank_number);

            tvName = itemView.findViewById(R.id.tv_user_name);

            ivAvatar = itemView.findViewById(R.id.iv_user_avatar);
        }
    }
}