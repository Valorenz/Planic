package com.example.planic;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.planic.utils.AndroidUtil;
import com.example.planic.utils.FirebaseUtil;
import com.google.android.gms.auth.api.signin.*;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmailOrUsername, etPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseUtil.isLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmailOrUsername = findViewById(R.id.login_input_et);
        etPassword = findViewById(R.id.login_password_et);
        Button btnLogin = findViewById(R.id.login_btn);
        TextView tvRegister = findViewById(R.id.register_tv);

        mAuth = FirebaseAuth.getInstance();

        SpannableString ss = getSs();
        tvRegister.setText(ss);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());

        btnLogin.setOnClickListener(v -> {
            String input = etEmailOrUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(input) || TextUtils.isEmpty(pass)) {
                AndroidUtil.showToast(this, "Email/Username dan password harus diisi");
                return;
            }

            if (input.contains("@")) {
                signInWithEmail(input, pass);
            } else {
                FirebaseUtil.allUserCollectionReference()
                        .whereEqualTo("username", input)
                        .get()
                        .addOnSuccessListener(query -> {
                            if (query.isEmpty()) {
                                AndroidUtil.showToast(this, "Username tidak terdaftar");
                            } else {
                                String email = query.getDocuments().get(0).getString("email");
                                signInWithEmail(email, pass);
                            }
                        })
                        .addOnFailureListener(e -> AndroidUtil.showToast(this, "Gagal cek username: " + e.getMessage()));
            }
        });
    }

    @NonNull
    private SpannableString getSs() {
        String fullText = "Don’t have an account? Register Now";
        SpannableString ss = new SpannableString(fullText);
        String linkText = "Register Now";
        int start = fullText.indexOf(linkText);
        int end = start + linkText.length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.txt_color));
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(auth -> {
                    AndroidUtil.showToast(this, "Login berhasil");
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        AndroidUtil.showToast(this, "Belum terdaftar, silakan daftar dahulu");
                    } else {
                        AndroidUtil.showToast(this, "Login gagal: " + e.getMessage());
                    }
                });
    }
}