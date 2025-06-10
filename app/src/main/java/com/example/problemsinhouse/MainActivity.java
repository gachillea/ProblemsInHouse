package com.example.problemsinhouse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button createPostButton, profileButton, notificationsButton ;
    private TextView welcomeText;
    private User user;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Πάρε το όνομα χρήστη από το intent
        user = getIntent().getParcelableExtra("user");
        if (user == null) {
            user = new User("","", 0L);
        }

        // Σύνδεση UI στοιχείων
        welcomeText = findViewById(R.id.welcomeText);
        createPostButton = findViewById(R.id.createPostButton);

        welcomeText.setText(getString(R.string.welcome) + user.getUsername() + "!");

        recyclerView = findViewById(R.id.postRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirestoreHelper.getAllPosts(user.getUsername(), (FirestoreHelper.PostsCallback) posts -> {
            PostAdapter adapter = new PostAdapter(MainActivity.this, posts , user);
            recyclerView.setAdapter(adapter);
        });


        createPostButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            intent.putExtra("user", user);  // στέλνεις τον χρήστη στο PostActivity
            startActivity(intent);
        });

        profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        notificationsButton = findViewById(R.id.notificationsButton);

        notificationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirestoreHelper.checkUser(user.getUsername(), user.getPassword(), updatedUser ->
        {
            user = updatedUser;
            Log.d("lives", user.getLives().toString());
        });
    }
}
