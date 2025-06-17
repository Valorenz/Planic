package com.example.planic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.planic.R;
import com.example.planic.model.NoteModel;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final List<NoteModel> noteList;

    public NoteAdapter(List<NoteModel> noteList) {
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteModel note = noteList.get(position);
        holder.textNote.setText(note.content);
        if (note.imageUrl != null && !note.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(note.imageUrl)
                    .into(holder.imageNote);
        } else {
            holder.imageNote.setImageResource(R.drawable.gambar1);
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textNote;
        ImageView imageNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textNote = itemView.findViewById(R.id.textNote);
            imageNote = itemView.findViewById(R.id.imageNote);
        }
    }
}