package com.example.problemsinhouse;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.problemsinhouse.FirestoreHelper;
import com.example.problemsinhouse.Post;
import com.example.problemsinhouse.PostAdapter;
import com.example.problemsinhouse.R;
import com.example.problemsinhouse.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameTextView, livesTextView;
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();

    private User currentUser;  // Ο χρήστης που έχει γίνει login

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameTextView = findViewById(R.id.usernameTextView);
        livesTextView = findViewById(R.id.livesTextView);
        postsRecyclerView = findViewById(R.id.postsRecyclerView);

        // Από το intent ή FirebaseAuth
        currentUser = getIntent().getParcelableExtra("user");

        if (currentUser == null) {
            Toast.makeText(this, "Δεν βρέθηκε ο χρήστης", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        usernameTextView.setText(currentUser.getUsername());
        livesTextView.setText("Lives: " + currentUser.getLives());

        setupRecyclerView();
        loadUserPosts();
    }

    private void setupRecyclerView() {
        postAdapter = new PostAdapter(this, postList, currentUser);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerView.setAdapter(postAdapter);
    }

    private void loadUserPosts() {
        FirestoreHelper.getUserPosts(currentUser.getUsername(), posts -> {
            postList.clear();
            postList.addAll(posts);
            postAdapter.notifyDataSetChanged();
        });
    }

}
