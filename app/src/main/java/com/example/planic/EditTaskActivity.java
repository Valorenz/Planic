package com.example.planic;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditTaskActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editDeadline;
    private Button btnUpdate, btnDelete;

    private DatabaseReference dbRef;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editDeadline = findViewById(R.id.editDeadline);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        // Ambil data dari intent
        Intent intent = getIntent();
        taskId = intent.getStringExtra("taskId");
        String taskTitle = intent.getStringExtra("taskTitle");
        String taskDescription = intent.getStringExtra("taskDescription");
        String taskDeadline = intent.getStringExtra("taskDeadline");

        // Set data ke form
        editTitle.setText(taskTitle);
        editDescription.setText(taskDescription);
        editDeadline.setText(taskDeadline);

        // Referensi database untuk task ini
        dbRef = FirebaseDatabase.getInstance("https://planic-5cfc1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Tasks").child(taskId);

        btnUpdate.setOnClickListener(v -> updateTask());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void updateTask() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String deadline = editDeadline.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError("Judul tidak boleh kosong");
            editTitle.requestFocus();
            return;
        }

        // Buat objek Task baru dengan data terbaru
        Task updatedTask = new Task(taskId, title, description, deadline);

        // Update data di Firebase
        dbRef.setValue(updatedTask)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditTaskActivity.this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
                    finish(); // tutup activity dan kembali ke Home
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditTaskActivity.this, "Gagal mengupdate data", Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus tugas ini?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTask();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteTask() {
        dbRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditTaskActivity.this, "Tugas berhasil dihapus", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditTaskActivity.this, "Gagal menghapus tugas", Toast.LENGTH_SHORT).show();
                });
    }
}
