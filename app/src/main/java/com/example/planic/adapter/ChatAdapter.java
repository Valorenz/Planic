package com.example.planic.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.planic.FullscreenImageActivity;
import com.example.planic.R;
import com.example.planic.model.ChatMessageModel;
import com.example.planic.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatAdapter.ChatModelViewHolder> {
    Context context;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);

            if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
                holder.rightChatImageview.setVisibility(View.VISIBLE);
                Glide.with(context).load(model.getImageUrl()).into(holder.rightChatImageview);
                holder.rightChatTextview.setVisibility(View.GONE);
            } else {
                holder.rightChatImageview.setVisibility(View.GONE);
                holder.rightChatTextview.setVisibility(View.VISIBLE);
                holder.rightChatTextview.setText(model.getMessage());
            }
        } else {
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatLayout.setVisibility(View.GONE);

            if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
                holder.leftChatImageview.setVisibility(View.VISIBLE);
                Glide.with(context).load(model.getImageUrl()).into(holder.leftChatImageview);
                holder.leftChatTextview.setVisibility(View.GONE);
            } else {
                holder.leftChatImageview.setVisibility(View.GONE);
                holder.leftChatTextview.setVisibility(View.VISIBLE);
                holder.leftChatTextview.setText(model.getMessage());
            }
        }

        holder.rightChatImageview.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullscreenImageActivity.class);
            intent.putExtra("image_url", model.getImageUrl());
            context.startActivity(intent);
        });

        holder.leftChatImageview.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullscreenImageActivity.class);
            intent.putExtra("image_url", model.getImageUrl());
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextview, rightChatTextview;
        ImageView leftChatImageview, rightChatImageview;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatImageview = itemView.findViewById(R.id.left_chat_imageview);
            rightChatImageview = itemView.findViewById(R.id.right_chat_imageview);
        }
    }
}
