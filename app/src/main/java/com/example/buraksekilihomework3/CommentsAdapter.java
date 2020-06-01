package com.example.buraksekilihomework3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    List<CommentItem> commentItems;
    Context context;

    public CommentsAdapter(List<CommentItem> commentItems, Context context) {
        this.commentItems = commentItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.comments_row_layout, parent, false);
        return new CommentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {

        holder.username.setText(commentItems.get(position).getName());
        holder.commentContext.setText(commentItems.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return commentItems.size();
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView commentContext;
        ConstraintLayout root;


        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            commentContext = itemView.findViewById(R.id.commentDetail);
            root = itemView.findViewById(R.id.containter);
        }
    }

}
