package com.example.problemsinhouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private List<Comment> commentList;
    private boolean sameUser;
    private OnSolveClickListener solveClickListener;

    public CommentAdapter(Context context, List<Comment> commentList, boolean sameUser, OnSolveClickListener solveClickListener) {
        this.context = context;
        this.commentList = commentList;
        this.sameUser = sameUser;
        this.solveClickListener = solveClickListener;
    }

    public interface OnSolveClickListener {
        void onSolveClick(Comment comment);
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.usernameText.setText(comment.getUsername());
        holder.commentText.setText(comment.getText());
        if (sameUser){
            holder.solveButton.setVisibility(View.VISIBLE);
        }


        holder.solveButton.setOnClickListener(v -> {
            if (solveClickListener != null) {
                solveClickListener.onSolveClick(comment); // ενημερώνει το Activity
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, commentText;
        Button solveButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.commentUsername);
            commentText = itemView.findViewById(R.id.commentText);
            solveButton = itemView.findViewById(R.id.btnSolve);
        }
    }
}
