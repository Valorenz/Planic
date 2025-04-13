package com.example.planic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planic.R;
import com.example.planic.TitleTask;
import com.example.planic.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TitleTask> titleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // pastikan nama layoutnya activity_home.xml

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Isi data list
        titleList = new ArrayList<>();
        titleList.add(new TitleTask("Tugas 1 - Kerjakan Latihan"));
        titleList.add(new TitleTask("Tugas 2 - Baca Materi Kecerdasan Buatan"));
        titleList.add(new TitleTask("Tugas 3 - Upload Proyek ke GitHub"));

        // Set adapter ke RecyclerView
        taskAdapter = new TaskAdapter(titleList);
        recyclerView.setAdapter(taskAdapter);
    }
}
