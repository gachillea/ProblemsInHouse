package com.example.problemsinhouse;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView postImage;
    private TextView titleText, contentText, usernameText;
    private EditText commentInput;
    private Button submitComment;
    private RecyclerView commentsRecycler;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private Post currentPost;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_post_detail);

        postImage = findViewById(R.id.postImageDetail);
        titleText = findViewById(R.id.titleTextDetail);
        contentText = findViewById(R.id.contentTextDetail);
        usernameText = findViewById(R.id.usernameDetail);
        commentInput = findViewById(R.id.commentInput);
        submitComment = findViewById(R.id.submitComment);
        commentsRecycler = findViewById(R.id.commentsRecycler);

        currentUser = getIntent().getParcelableExtra("user");
        currentPost = getIntent().getParcelableExtra("post");

        if (!currentPost.getUsername().equals(currentUser.getUsername())) {
            commentInput.setVisibility(View.VISIBLE);
        }

        if (currentPost == null) {
            Toast.makeText(this, "Δεν βρέθηκε το Post", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (currentUser == null) {
            Toast.makeText(this, "Δεν βρέθηκε o User", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        titleText.setText(currentPost.getTitle());
        contentText.setText(currentPost.getContent());
        usernameText.setText(currentPost.getUsername());


        if (currentPost.getImagePath() != null && !currentPost.getImagePath().isEmpty()) {
            Glide.with(this).load(currentPost.getImagePath()).into(postImage);
        }

        commentAdapter = new CommentAdapter(this, commentList);
        commentsRecycler.setLayoutManager(new LinearLayoutManager(this));
        commentsRecycler.setAdapter(commentAdapter);
        Log.d("debug","lauos");

        loadComments();
        Log.d("debug", "debug");
        submitComment.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            if (!commentText.isEmpty()) {
                FirestoreHelper.addCommentToPost(currentPost.getId(), currentUser.getUsername(), commentText, success -> {
                    if (success){
                        commentInput.setText("");
                        loadComments();
                    }
                });
            }
        });


    }

    private void loadComments() {
        if (currentPost.getId() == null) {
            Log.e("loadComments", "post ID is null");
            return;
        }

        FirestoreHelper.getCommentsForPost(currentPost.getId(), new FirestoreHelper.CommentCallback() {
            @Override
            public void onSuccess(List<Comment> comments) {
                Log.d("loadComments", "Comments received: " + (comments != null ? comments.size() : "null"));

                commentList.clear();
                if (comments != null) {
                    commentList.addAll(comments);
                }

                if (commentAdapter == null) {
                    Log.e("loadComments", "CommentAdapter is null");
                } else {
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onResult(boolean b) {

            }
        });
    }

}
