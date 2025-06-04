package com.example.planic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditTaskActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editDeadline;
    private Button btnUpdate, btnDelete, btnEditImage;
    private ImageView imageEditTask;

    private DatabaseReference dbRef;
    private String taskId, currentImageUrl, userId;
    private Uri newImageUri = null;
    private static final int PICK_IMAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text); // Pastikan layout ini benar

        // Inisialisasi view
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editDeadline = findViewById(R.id.editDeadline);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnEditImage = findViewById(R.id.btnEditImage);
        imageEditTask = findViewById(R.id.imageEditTask);


        // Ambil data dari intent
        Intent intent = getIntent();
        taskId = intent.getStringExtra("taskId");
        editTitle.setText(intent.getStringExtra("taskTitle"));
        editDescription.setText(intent.getStringExtra("taskDescription"));
        editDeadline.setText(intent.getStringExtra("taskDeadline"));
        currentImageUrl = intent.getStringExtra("taskImageUrl");

        Log.d("EditTaskActivity", "Image URL: " + currentImageUrl);

        // Tampilkan gambar dengan Glide (gunakan fallback jika gagal)
        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(currentImageUrl)
                    .placeholder(R.drawable.gambar1)
                    .error(R.drawable.gambar1)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageEditTask);
        } else {
            imageEditTask.setImageResource(R.drawable.gambar1);
        }

        // Database reference
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance("https://planic-5cfc1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Tasks").child(userId).child(taskId);

        // Aksi tombol
        btnEditImage.setOnClickListener(v -> chooseImage());
        btnUpdate.setOnClickListener(v -> updateTask());
        btnDelete.setOnClickListener(v -> deleteTask());
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            newImageUri = data.getData();
            imageEditTask.setImageURI(newImageUri);
            Toast.makeText(this, "Fitur ganti gambar belum diimplementasikan", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTask() {
        String title = editTitle.getText().toString().trim();
        String desc = editDescription.getText().toString().trim();
        String deadline = editDeadline.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError("Judul wajib diisi");
            return;
        }

        Task updated = new Task(taskId, title, desc, deadline, currentImageUrl, userId);
        dbRef.setValue(updated)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Tugas diupdate", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal update", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteTask() {
        dbRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Tugas dihapus", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal hapus", Toast.LENGTH_SHORT).show();
                });
    }
}
