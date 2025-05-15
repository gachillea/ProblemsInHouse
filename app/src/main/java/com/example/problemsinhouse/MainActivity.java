package com.example.problemsinhouse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button createPostButton;
    private TextView welcomeText;
    private String username;
    private RecyclerView recyclerView;
    private PostDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Πάρε το όνομα χρήστη από το intent
        username = getIntent().getStringExtra("username");
        if (username == null) {
            username = "User";
        }

        // Σύνδεση UI στοιχείων
        welcomeText = findViewById(R.id.welcomeText);
        createPostButton = findViewById(R.id.createPostButton);

        welcomeText.setText(getString(R.string.welcome) + username + "!");

        recyclerView = findViewById(R.id.postRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new PostDatabaseHelper(this);
        List<Post> allPosts = db.getAllPosts();

        PostAdapter adapter = new PostAdapter(this, allPosts);
        recyclerView.setAdapter(adapter);

        // Listener για το κουμπί
        createPostButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            intent.putExtra("username", username);  // στέλνεις τον χρήστη στο PostActivity
            startActivity(intent);
        });
    }
}
