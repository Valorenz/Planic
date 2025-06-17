package com.example.planic.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planic.ChatActivity;
import com.example.planic.R;
import com.example.planic.model.ChatroomModel;
import com.example.planic.model.UserModel;
import com.example.planic.utils.AndroidUtil;
import com.example.planic.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecentChatAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatAdapter.ChatroomModelViewHolder> {
    Context context;

    public RecentChatAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if (t.isSuccessful()) {
                                        Uri uri = t.getResult();
                                        AndroidUtil.setProfilePic(context, uri, holder.profilePicture);
                                    }
                                });

                        String displayName = otherUserModel.getUsername();
                        if (otherUserModel.getUserId().equals(FirebaseUtil.currentUserId())) {
                            displayName += " (Me)";
                        }
                        holder.usernameText.setText(displayName);

                        if (lastMessageSentByMe)
                            holder.lastMessageText.setText(String.format("You: %s", model.getLastMessage()));
                        else
                            holder.lastMessageText.setText(model.getLastMessage());
                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                        holder.itemView.setOnLongClickListener(v -> {
                            new AlertDialog.Builder(context)
                                    .setTitle("Hapus Chat")
                                    .setMessage("Anda yakin ingin menghapus chat ini?")
                                    .setPositiveButton("Hapus", (dialog, which) -> {
                                        FirebaseUtil.getChatroomReference(model.getChatroomId())
                                                .delete()
                                                .addOnSuccessListener(aVoid -> AndroidUtil.showToast(context, "Chat berhasil dihapus"))
                                                .addOnFailureListener(e -> AndroidUtil.showToast(context, "Gagal menghapus chat"));

                                        FirebaseUtil.getChatroomMessagesReference(model.getChatroomId())
                                                .get()
                                                .addOnSuccessListener(querySnapshot -> {
                                                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                                        doc.getReference().delete();
                                                    }
                                                });
                                    })
                                    .setNegativeButton("Batal", null)
                                    .show();
                            return true;
                        });
                    }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_chat, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, lastMessageText, lastMessageTime;
        ImageView profilePicture;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePicture = itemView.findViewById(R.id.profile_picture);
        }
    }
}