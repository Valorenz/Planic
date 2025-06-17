package com.example.planic;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends Activity {

    EditText etTitle, etDescription;
    TextView tvStartTime, tvEndTime, tvDate;
    ImageView ivCalendar;
    Button btnAddImage, btnEvent;
    String selectedCat;
    Uri imageUri = null;
    String eventId = null, oldImgUrl = null;
    Calendar calendar = Calendar.getInstance();
    boolean isEditMode = false;

    private static final int PICK_IMAGE_REQUEST = 201;

    @Override
    protected void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_add_event);

        etTitle = findViewById(R.id.et_task_title);
        etDescription = findViewById(R.id.et_task_description);
        tvDate = findViewById(R.id.tv_date);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        ivCalendar = findViewById(R.id.iv_calendar_icon);
        btnAddImage = findViewById(R.id.btn_attach_image);
        btnEvent = findViewById(R.id.btn_create_task);

        Button bDesign = findViewById(R.id.btn_category_design);
        Button bDev = findViewById(R.id.btn_category_development);
        Button bResearch = findViewById(R.id.btn_category_research);

        // Cek intent untuk edit mode
        Intent i = getIntent();
        if (i != null && i.hasExtra("id")) {
            isEditMode = true;
            eventId = i.getStringExtra("id");
            oldImgUrl = i.getStringExtra("imgUrl");
            etTitle.setText(i.getStringExtra("title"));
            selectedCat = i.getStringExtra("category");
            tvDate.setText(i.getStringExtra("date"));
            tvStartTime.setText(i.getStringExtra("start"));
            tvEndTime.setText(i.getStringExtra("end"));
            etDescription.setText(i.getStringExtra("desc"));

            updateCategorySelection(selectedCat, bDesign, bDev, bResearch);
        } else {
            // Mode tambah, generate ID baru
            eventId = String.valueOf(System.currentTimeMillis());
        }

        ivCalendar.setOnClickListener(v -> pickDate());
        tvStartTime.setOnClickListener(v -> pickTime(tvStartTime));
        tvEndTime.setOnClickListener(v -> pickTime(tvEndTime));

        bDesign.setOnClickListener(v -> {
            selectedCat = "Design";
            updateCategorySelection(selectedCat, bDesign, bDev, bResearch);
        });
        bDev.setOnClickListener(v -> {
            selectedCat = "Development";
            updateCategorySelection(selectedCat, bDesign, bDev, bResearch);
        });
        bResearch.setOnClickListener(v -> {
            selectedCat = "Research";
            updateCategorySelection(selectedCat, bDesign, bDev, bResearch);
        });

        btnAddImage.setOnClickListener(v -> chooseImage());
        btnEvent.setOnClickListener(v -> {
            if (!validateInput()) return;
            if (imageUri != null) uploadAndUpdate();
            else updateEvent(oldImgUrl != null ? oldImgUrl : "");
        });
    }

    private boolean validateInput() {
        if (etTitle.getText().toString().trim().isEmpty() ||
                tvDate.getText().toString().trim().isEmpty() ||
                tvStartTime.getText().toString().trim().isEmpty() ||
                tvEndTime.getText().toString().trim().isEmpty() ||
                selectedCat == null || selectedCat.trim().isEmpty()) {
            Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateCategorySelection(String sel, Button... bs) {
        for (Button b : bs) {
            if (b.getText().toString().equalsIgnoreCase(sel)) {
                b.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_blue_light));
            } else {
                b.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
            }
        }
    }

    private void pickDate() {
        int y = calendar.get(Calendar.YEAR), m = calendar.get(Calendar.MONTH), d = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, (wv, yy, mm, dd) -> {
            calendar.set(yy, mm, dd);
            String dt = android.text.format.DateFormat.format("dd MMMM, EEEE", calendar).toString();
            tvDate.setText(dt);
        }, y, m, d).show();
    }

    private void pickTime(TextView tv) {
        int h = calendar.get(Calendar.HOUR_OF_DAY), min = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(this, (wv, hh, mm) -> {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hh);
            c.set(Calendar.MINUTE, mm);
            String t = android.text.format.DateFormat.format("hh:mm a", c).toString();
            tv.setText(t);
        }, h, min, false).show();
    }

    private void chooseImage() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int rc, int rr, @Nullable Intent data) {
        super.onActivityResult(rc, rr, data);
        if (rc == PICK_IMAGE_REQUEST && rr == RESULT_OK && data != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Gambar dipilih", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAndUpdate() {
        String path = "event_images/" + eventId + "_" + System.currentTimeMillis() + ".jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference(path);
        ref.putFile(imageUri)
                .addOnSuccessListener(ts -> ref.getDownloadUrl().addOnSuccessListener(uri ->
                        updateEvent(uri.toString())))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload gagal", Toast.LENGTH_SHORT).show();
                    updateEvent(oldImgUrl != null ? oldImgUrl : "");
                });
    }

    private void updateEvent(String imgUrl) {
        Map<String, Object> m = new HashMap<>();
        m.put("title", etTitle.getText().toString());
        m.put("desc", etDescription.getText().toString());
        m.put("date", tvDate.getText().toString());
        m.put("start", tvStartTime.getText().toString());
        m.put("end", tvEndTime.getText().toString());
        m.put("category", selectedCat);
        m.put("imgUrl", imgUrl);
        m.put("timestamp", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventId)
                .set(m) // menggunakan set() agar bisa tambah & edit
                .addOnSuccessListener(un -> {
                    Toast.makeText(this, isEditMode ? "Event diperbarui" : "Event ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal menyimpan", Toast.LENGTH_SHORT).show());
    }
}
