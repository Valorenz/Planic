package com.example.planic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetailTugasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_tugas);

        // Inisialisasi UI
        TextView tvTaskName = findViewById(R.id.tv_task_name);
        TextView tvTaskDescription = findViewById(R.id.tv_task_description); // Tambahkan TextView untuk deskripsi
        Button btnEditTask = findViewById(R.id.btn_edit_task);
        Button btnDeleteTask = findViewById(R.id.btn_delete_task);

        // Ambil data tugas dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("TaskData", MODE_PRIVATE);
        String taskName = sharedPreferences.getString("task_name", "Tidak ada tugas");
        String taskDescription = sharedPreferences.getString("task_description", "Tidak ada deskripsi");

        // Tampilkan data tugas di TextView
        tvTaskName.setText(taskName);
        tvTaskDescription.setText(taskDescription); // Set deskripsi ke TextView

        // Event handler untuk hapus tugas
        btnDeleteTask.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Kirim hasil ke Activity sebelumnya
            Intent resultIntent = new Intent();
            resultIntent.putExtra("deleted_task", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Sesuaikan padding untuk system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
