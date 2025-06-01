package com.example.planic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddTaskActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private EditText AddTitle, AddDescription, AddDeadline;
    private Button btnAdd2, btnUploadImage;
    private ImageView imageViewTask;

    private DatabaseReference databaseTasks;

    private final String SUPABASE_URL = "https://bfmqduguvvmjwzukafyb.supabase.co";
    private final String SUPABASE_API_KEY = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJmbXFkdWd1dnZtand6dWthZnliIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0ODY4MDYxNiwiZXhwIjoyMDY0MjU2NjE2fQ.ILAbaKtmgyCFwNiDEy1mutiK7t0BgbUlgMswP_qBWzE";
    private final String SUPABASE_BUCKET = "planic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        databaseTasks = FirebaseDatabase.getInstance("https://planic-5cfc1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Tasks");

        AddTitle = findViewById(R.id.AddTitle);
        AddDescription = findViewById(R.id.AddDescription);
        AddDeadline = findViewById(R.id.AddDeadline);
        btnAdd2 = findViewById(R.id.btnAdd2);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        imageViewTask = findViewById(R.id.imageViewTask);

        btnUploadImage.setOnClickListener(v -> openFileChooser());

        btnAdd2.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToSupabase(imageUri);
            } else {
                addTaskToFirebase(null); // tanpa gambar
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewTask.setImageURI(imageUri);
        }
    }

    private void uploadImageToSupabase(Uri uri) {
        String fileName = System.currentTimeMillis() + ".jpg";
        String path = FileUtils.getPath(this, uri);
        if (path == null) {
            Toast.makeText(this, "Gagal mendapatkan path file", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(path);

        OkHttpClient client = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/jpeg"));

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, fileBody)
                .build();

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + fileName)
                .addHeader("Authorization", SUPABASE_API_KEY)  // sudah termasuk "Bearer ..."
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AddTaskActivity.this, "Upload gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String imageUrl = SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET + "/" + fileName;
                    runOnUiThread(() -> addTaskToFirebase(imageUrl));
                } else {
                    final String errorBody = response.body() != null ? response.body().string() : "null";
                    runOnUiThread(() -> Toast.makeText(AddTaskActivity.this, "Gagal upload gambar: " + errorBody, Toast.LENGTH_LONG).show());
                }
            }
        });
    }


    private void addTaskToFirebase(String imageUrl) {
        String title = AddTitle.getText().toString().trim();
        String description = AddDescription.getText().toString().trim();
        String deadline = AddDeadline.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            AddTitle.setError("Judul tidak boleh kosong");
            return;
        }

        String taskId = databaseTasks.push().getKey();

        Task task = new Task(taskId, title, description, deadline, imageUrl);
        databaseTasks.child(taskId).setValue(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddTaskActivity.this, "Tugas berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddTaskActivity.this, "Gagal menambahkan tugas", Toast.LENGTH_SHORT).show();
                });
    }
}
