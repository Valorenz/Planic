package com.example.planic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planic.model.NoteModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class AddNoteActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1001;

    private EditText etNoteContent;
    private ImageView imageNote;
    private Button btnChooseImage, btnSaveNote;

    private Uri selectedImageUri;

    private DatabaseReference noteRef;
    private StorageReference storageRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        etNoteContent = findViewById(R.id.etNoteContent);
        imageNote = findViewById(R.id.imageNote);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSaveNote = findViewById(R.id.btnSaveNote);

        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        noteRef = FirebaseDatabase.getInstance("https://planic-2-default-rtdb.firebaseio.com/")
                .getReference("Notes").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference("note_images");

        btnChooseImage.setOnClickListener(v -> pickImage());
        btnSaveNote.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImageAndSaveNote();
            } else {
                saveNoteToFirebase(null);
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageNote.setImageURI(selectedImageUri);
        }
    }

    private void uploadImageAndSaveNote() {
        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveNoteToFirebase(uri.toString())))
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal upload gambar", Toast.LENGTH_SHORT).show());
    }

    private void saveNoteToFirebase(String imageUrl) {
        String content = etNoteContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            etNoteContent.setError("Catatan tidak boleh kosong");
            return;
        }

        String noteId = noteRef.push().getKey();
        NoteModel note = new NoteModel(noteId, content, imageUrl, userId);
        noteRef.child(noteId).setValue(note)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Catatan berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal menyimpan catatan", Toast.LENGTH_SHORT).show());
    }
}