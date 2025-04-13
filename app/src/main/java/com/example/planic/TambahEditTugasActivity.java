package com.example.planic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TambahEditTugasActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    private Button btnSaveTask;
    private EditText etTaskName, etTaskDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tambah_edit_tugas);

        // Inisialisasi UI
        btnSaveTask = findViewById(R.id.btn_save_task);
        etTaskName = findViewById(R.id.et_task_name);
        etTaskDescription = findViewById(R.id.et_task_description);

        // Tombol Simpan
        btnSaveTask.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("TaskData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String taskName = etTaskName.getText().toString();
            String taskDescription = etTaskDescription.getText().toString();

            editor.putString("task_name", taskName);
            editor.putString("task_description", taskDescription);
            editor.apply();

            Intent intent = new Intent();
            intent.putExtra("task_saved", true);
            setResult(RESULT_OK, intent);
            finish();
        });

        // Buka DetailTugasActivity untuk melihat detail tugas
        Intent intent = new Intent(TambahEditTugasActivity.this, DetailTugasActivity.class);
        startActivityForResult(intent, REQUEST_CODE);

        // Mengatur padding untuk menyesuaikan dengan system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Menerima hasil dari DetailTugasActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (data.hasExtra("deleted_task")) {
                Toast.makeText(this, "Tugas dihapus", Toast.LENGTH_SHORT).show();
            } else if (data.hasExtra("task_saved")) {
                Toast.makeText(this, "Tugas disimpan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
