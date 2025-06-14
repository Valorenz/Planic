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

import com.example.planic.adapter.TaskAdapter;
import com.example.planic.model.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> taskList;
    private DatabaseReference taskRef;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance()
                        .getCurrentUser())
                .getUid();

        taskRef = FirebaseDatabase.getInstance(
                        "https://planic-2-default-rtdb.firebaseio.com/")
                .getReference("Tasks")
                .child(userId);

        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    TaskModel task = ds.getValue(TaskModel.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(),
                        "Gagal memuat data",
                        Toast.LENGTH_SHORT).show();
            }
        });

        taskAdapter.setOnItemClickListener(task -> {
            Intent intent = new Intent(requireActivity(), EditTaskActivity.class);
            intent.putExtra("taskId", task.id);
            intent.putExtra("taskTitle", task.title);
            intent.putExtra("taskDescription", task.description);
            intent.putExtra("taskDeadline", task.deadline);
            intent.putExtra("taskImageUrl", task.imageUrl);
            startActivity(intent);
        });
    }
}