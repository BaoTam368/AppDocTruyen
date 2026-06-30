package com.example.appdoctruyen.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
            holder.tvGroupComicCount.setText(R.string.group_stats_unavailable_short);
            holder.tvGroupMemberCount.setText(R.string.group_stats_unavailable_short);
            holder.imgGroupAvatar.setVisibility(View.GONE);
            holder.tvGroupInitials.setVisibility(View.VISIBLE);
            holder.tvGroupInitials.setText("?");
            holder.itemView.setOnClickListener(null);
            return;
        }

        holder.tvGroupName.setText(isBlank(group.getName())
                ? context.getString(R.string.group_default_name)
                : group.getName());
        holder.tvGroupComicCount.setText(formatCount(group.getComicCount(), R.string.group_comic_count_format));
        holder.tvGroupMemberCount.setText(formatCount(group.getMemberCount(), R.string.group_members_count_format));

        // Initials avatar: luôn dùng initials vì MangaDex group không có avatar thật
        bindInitialsAvatar(holder.imgGroupAvatar, holder.tvGroupInitials,
                group.getName(), group.getGroupId());

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

    /**
     * Hiển thị initials avatar với màu nền cố định theo groupId.
     * Ẩn ImageView, hiện TextView initials.
     */
    public static void bindInitialsAvatar(ImageView imgAvatar, TextView tvInitials,
                                   String name, String groupId) {
        imgAvatar.setVisibility(View.GONE);
        tvInitials.setVisibility(View.VISIBLE);
        tvInitials.setText(generateInitials(name));

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.OVAL);
        background.setColor(generateColorFromId(groupId));
        tvInitials.setBackground(background);
    }

    /**
     * Lấy 1-2 ký tự đầu của tên nhóm làm initials.
     */
    static String generateInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String trimmed = name.trim();
        String[] words = trimmed.split("\\s+");
        if (words.length >= 2) {
            return (words[0].substring(0, 1) + words[1].substring(0, 1)).toUpperCase();
        }
        return trimmed.substring(0, Math.min(2, trimmed.length())).toUpperCase();
    }

    /**
     * Hash groupId ra màu HSL cố định. Dùng hue từ hash, saturation 55-70%, lightness 40-55%
     * để tạo màu sắc đa dạng nhưng nhất quán giữa các lần load.
     */
    static int generateColorFromId(String id) {
        if (id == null || id.isEmpty()) return Color.parseColor("#00ACC1");
        int hash = id.hashCode();
        float hue = Math.abs(hash % 360);
        float saturation = 0.55f + (Math.abs((hash >> 8) % 15) / 100f);
        float lightness = 0.40f + (Math.abs((hash >> 16) % 15) / 100f);
        return hslToRgb(hue, saturation, lightness);
    }

    private static int hslToRgb(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;
        float r, g, b;

        if (h < 60) { r = c; g = x; b = 0; }
        else if (h < 120) { r = x; g = c; b = 0; }
        else if (h < 180) { r = 0; g = c; b = x; }
        else if (h < 240) { r = 0; g = x; b = c; }
        else if (h < 300) { r = x; g = 0; b = c; }
        else { r = c; g = 0; b = x; }

        int ri = Math.round((r + m) * 255);
        int gi = Math.round((g + m) * 255);
        int bi = Math.round((b + m) * 255);
        return Color.rgb(ri, gi, bi);
    }

    private String formatCount(int value, int formatResId) {
        return value > 0
                ? context.getString(formatResId, value)
                : context.getString(R.string.group_stats_unavailable_short);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public interface OnGroupClickListener {
        void onGroupClick(TranslationGroup group, int position);
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGroupAvatar;
        TextView tvGroupInitials;
        TextView tvGroupName;
        TextView tvGroupComicCount;
        TextView tvGroupMemberCount;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGroupAvatar = itemView.findViewById(R.id.imgGroupAvatar);
            tvGroupInitials = itemView.findViewById(R.id.tvGroupInitials);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvGroupComicCount = itemView.findViewById(R.id.tvGroupComicCount);
            tvGroupMemberCount = itemView.findViewById(R.id.tvGroupMemberCount);
        }
    }
}