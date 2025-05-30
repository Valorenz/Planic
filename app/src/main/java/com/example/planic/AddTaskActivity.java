package com.example.planic;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTaskActivity extends AppCompatActivity {

    private EditText AddTitle, AddDescription, AddDeadline;
    private Button btnAdd2;

    private DatabaseReference databaseTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Inisialisasi Firebase Database
        databaseTasks = FirebaseDatabase.getInstance("https://planic-5cfc1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Tasks");

        // Bind view
        AddTitle = findViewById(R.id.AddTitle);
        AddDescription = findViewById(R.id.AddDescription);
        AddDeadline = findViewById(R.id.AddDeadline);
        btnAdd2 = findViewById(R.id.btnAdd2);

        btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTaskToFirebase();
            }
        });
    }

    private void addTaskToFirebase() {
        String title = AddTitle.getText().toString().trim();
        String description = AddDescription.getText().toString().trim();
        String deadline = AddDeadline.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            AddTitle.setError("Judul tidak boleh kosong");
            return;
        }

        String taskId = databaseTasks.push().getKey(); // Auto ID

        Task task = new Task(taskId, title, description, deadline);
        databaseTasks.child(taskId).setValue(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddTaskActivity.this, "Tugas berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish(); // kembali ke activity sebelumnya
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddTaskActivity.this, "Gagal menambahkan tugas", Toast.LENGTH_SHORT).show();
                });
    }
}
