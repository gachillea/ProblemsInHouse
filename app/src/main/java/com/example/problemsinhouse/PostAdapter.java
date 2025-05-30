package com.example.problemsinhouse;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private final List<Post> posts;
    private final Context context;
    private User user;

    public PostAdapter(Context context, List<Post> posts, User user) {
        this.context = context;
        this.posts = posts;
        this.user = user;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView titleText, contentText, username;

        public PostViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            postImage = itemView.findViewById(R.id.postImage);
            titleText = itemView.findViewById(R.id.titleText);
            contentText = itemView.findViewById(R.id.contentText);
        }
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.username.setText(post.getUsername());
        holder.titleText.setText(post.getTitle());
        holder.contentText.setText(post.getContent());
        if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.getImagePath())
                    .into(holder.postImage);
        }

        holder.itemView.setOnClickListener(v -> {
            Log.d("DEBUG", "Going to start PostDetailActivity");
            Intent intent = new Intent(context, PostDetailActivity.class);

            intent.putExtra("post", post);
            intent.putExtra("user", user);

            context.startActivity(intent);
            Log.d("DEBUG", "PostDetailActivity started?");
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
