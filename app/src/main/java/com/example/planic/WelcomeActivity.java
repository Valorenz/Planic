package com.example.planic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planic.utils.FirebaseUtil;

public class WelcomeActivity extends AppCompatActivity {
    Button welcome_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseUtil.isLoggedIn()) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        welcome_btn = findViewById(R.id.welcome_btn);
        welcome_btn.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}