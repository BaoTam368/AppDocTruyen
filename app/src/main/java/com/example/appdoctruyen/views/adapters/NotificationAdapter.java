package com.example.appdoctruyen.views.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

        Log.d("TEST", "Bind position = " + position);

        holder.tvContent.setText(notifications.get(position).getContent());
    }

    @Override
    public int getItemCount() {

        Log.d("TEST", "getItemCount = " + notifications.size());

        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContent = itemView.findViewById(R.id.tv_notification_content);
        }
    }


}