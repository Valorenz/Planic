package com.example.planic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private List<Task> taskList;
    private DatabaseReference dbRef;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        taskAdapter.setOnItemClickListener(task -> {
            Intent intent = new Intent(HomeActivity.this, EditTaskActivity.class);
            intent.putExtra("taskId", task.id);
            intent.putExtra("taskTitle", task.title);
            intent.putExtra("taskDescription", task.description);
            intent.putExtra("taskDeadline", task.deadline);
            intent.putExtra("taskImageUrl", task.imageUrl);
            startActivity(intent);
        });


        // Referensi ke Firebase Realtime Database
        dbRef = FirebaseDatabase.getInstance("https://planic-5cfc1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Tasks");

        // Ambil data dari Firebase
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage());
            }
        });

        // Tombol Add Task → pindah ke AddTaskActivity
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });
    }
}
