package com.example.planic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TitleTask> titleList;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inisialisasi database Room
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "task-database")
                .allowMainThreadQueries() // Hindari di produksi!
                .build();

        // Inisialisasi data dari database
        titleList = new ArrayList<>(db.taskDao().getAll());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter(titleList);
        recyclerView.setAdapter(taskAdapter);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnDelete = findViewById(R.id.btnDelete);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Tambah Tugas");

                final EditText input = new EditText(HomeActivity.this);
                input.setHint("Judul tugas...");
                builder.setView(input);

                builder.setPositiveButton("Tambah", (dialog, which) -> {
                    String title = input.getText().toString().trim();
                    if (!title.isEmpty()) {
                        TitleTask newTask = new TitleTask(title);
                        db.taskDao().insert(newTask);

                        // Refresh data
                        titleList.clear();
                        titleList.addAll(db.taskDao().getAll());
                        taskAdapter.notifyDataSetChanged();

                        recyclerView.scrollToPosition(titleList.size() - 1);
                    } else {
                        Toast.makeText(HomeActivity.this, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!titleList.isEmpty()) {
                    // Hapus data terakhir
                    TitleTask lastTask = titleList.get(titleList.size() - 1);
                    db.taskDao().delete(lastTask);

                    // Refresh data
                    titleList.clear();
                    titleList.addAll(db.taskDao().getAll());
                    taskAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HomeActivity.this, "Tidak ada tugas untuk dihapus", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
