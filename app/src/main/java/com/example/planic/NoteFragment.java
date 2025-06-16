package com.example.planic;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.planic.adapter.NoteAdapter;
import com.example.planic.model.NoteModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteFragment extends Fragment {

    private RecyclerView recyclerViewNote;
    private NoteAdapter noteAdapter;
    private List<NoteModel> noteList;
    private DatabaseReference noteRef;

    public NoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewNote = view.findViewById(R.id.recyclerViewNote);

        recyclerViewNote.setLayoutManager(new LinearLayoutManager(requireContext()));
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList);
        recyclerViewNote.setAdapter(noteAdapter);

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        noteRef = FirebaseDatabase.getInstance("https://planic-2-default-rtdb.firebaseio.com/")
                .getReference("Notes")
                .child(userId);

        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noteList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    NoteModel note = ds.getValue(NoteModel.class);
                    if (note != null) {
                        noteList.add(note);
                    }
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Gagal memuat catatan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}