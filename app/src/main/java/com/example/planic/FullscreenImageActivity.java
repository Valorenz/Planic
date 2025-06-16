package com.example.planic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class FullscreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        PhotoView photoView = findViewById(R.id.fullscreen_image);
        ImageButton btnClose = findViewById(R.id.btn_close);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("image_url");

        Glide.with(this)
                .load(imageUrl)
                .into(photoView);

        btnClose.setOnClickListener(v -> finish());
    }
}