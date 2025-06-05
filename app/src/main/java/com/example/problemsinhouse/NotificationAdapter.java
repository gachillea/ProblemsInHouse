package com.example.problemsinhouse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.problemsinhouse.R;
import com.example.problemsinhouse.Notification;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.messageText.setText(notification.getMessage());

        String date = DateFormat.getDateTimeInstance().format(new Date(notification.getTimestamp()));
        holder.timestampText.setText(date);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestampText;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.notificationMessage);
            timestampText = itemView.findViewById(R.id.notificationTimestamp);
        }
    }
}
