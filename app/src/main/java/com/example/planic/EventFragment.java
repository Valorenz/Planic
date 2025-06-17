package com.example.planic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planic.adapter.EventAdapter;
import com.example.planic.model.EventModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class EventFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<EventModel> eventList;
    private FirebaseFirestore db;

    public EventFragment() {
        // Required empty public constructor
    }

    public static EventFragment newInstance() {
        return new EventFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false); // Reuse layout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = view.findViewById(R.id.tv_title);

        recyclerView = view.findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();

        eventAdapter = new EventAdapter(requireContext(), eventList, new EventAdapter.OnEventListener() {
            @Override
            public void delete(String docId) {
                confirmDeleteEvent(docId);
            }

            @Override
            public void update(String docId, EventModel event) {
                openEditEvent(docId, event);
            }
        });

        recyclerView.setAdapter(eventAdapter);

        db = FirebaseFirestore.getInstance();
        loadEvents();
    }

    private void loadEvents() {
        db.collection("events")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    eventList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            EventModel event = doc.toObject(EventModel.class);
                            event.setId(doc.getId());
                            eventList.add(event);
                        }
                        eventAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void confirmDeleteEvent(String eventId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin menghapus event ini?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteEvent(eventId))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteEvent(String eventId) {
        db.collection("events").document(eventId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Event dihapus", Toast.LENGTH_SHORT).show();
                    loadEvents();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal menghapus event", Toast.LENGTH_SHORT).show());

        FirebaseStorage.getInstance()
                .getReference("event_images/" + eventId + ".jpg")
                .delete();
    }

    private void openEditEvent(String id, EventModel e) {
        Intent i = new Intent(getContext(), EditEventActivity.class);
        i.putExtra("id", id);
        i.putExtra("title", e.getTitle());
        i.putExtra("category", e.getCategory());
        i.putExtra("date", e.getDate());
        i.putExtra("start", e.getStart());
        i.putExtra("end", e.getEnd());
        i.putExtra("desc", e.getDesc());
        i.putExtra("imgUrl", e.getImgUrl());
        startActivity(i);
    }
}
