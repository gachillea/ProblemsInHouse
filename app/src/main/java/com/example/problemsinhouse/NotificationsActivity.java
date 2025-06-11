package com.example.problemsinhouse;

import static android.widget.Toast.LENGTH_LONG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.problemsinhouse.NotificationAdapter;
import com.example.problemsinhouse.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerView = findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        User currentUser = getIntent().getParcelableExtra("user");

        loadNotifications(currentUser.getUsername());
    }

    private void loadNotifications(String username) {
        FirestoreHelper.getNotificationsForUser(username, notifications -> {
            notificationList.clear();
            if (notifications.isEmpty() || notifications == null)
            {
                Toast.makeText(this, getString(R.string.noNotifications), LENGTH_LONG).show();
            }
            else {
                notificationList.addAll(notifications);
            }

            adapter.notifyDataSetChanged();
        });
    }
}
