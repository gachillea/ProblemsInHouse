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

    private User currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = getIntent().getParcelableExtra("user");
        FirestoreHelper.checkUser(currentUser.getUsername(), currentUser.getPassword(), possiblyUpdatedUser ->{
            currentUser = possiblyUpdatedUser; // για την περίπτωση που ο αριθμός των ζωών του χρήστη έχει αλλάξει
            setContentView(R.layout.activity_profile);

            usernameTextView = findViewById(R.id.usernameTextView);
            livesTextView = findViewById(R.id.livesTextView);
            postsRecyclerView = findViewById(R.id.postsRecyclerView);

            if (currentUser == null) {
                Toast.makeText(this, getString(R.string.noUser), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            usernameTextView.setText(currentUser.getUsername());
            livesTextView.setText(getString(R.string.lives) + currentUser.getLives());

            setupRecyclerView();
            loadUserPosts();

        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserPosts(); // Ξαναφορτώνει τα posts από τη βάση
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
