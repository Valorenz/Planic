package com.example.planic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> taskList;
    private DatabaseReference taskRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        // Ambil user ID yang sedang login
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Path: Tasks/{userId}
        taskRef = FirebaseDatabase.getInstance("https://planic-2-default-rtdb.firebaseio.com/")
                .getReference("Tasks")
                .child(userId);

        // Ambil data task user dari Firebase
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    TaskModel task = taskSnapshot.getValue(TaskModel.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });

        taskAdapter.setOnItemClickListener(task -> {
            Intent intent = new Intent(HomeActivity.this, EditTaskActivity.class);
            intent.putExtra("taskId", task.id);
            intent.putExtra("taskTitle", task.title);
            intent.putExtra("taskDescription", task.description);
            intent.putExtra("taskDeadline", task.deadline);
            intent.putExtra("taskImageUrl", task.imageUrl);
            startActivity(intent);
        });

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(view -> {
            // Intent ke AddTaskActivity
            startActivity(new Intent(HomeActivity.this, AddTaskActivity.class));
        });


    }
}
