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

import com.example.planic.model.UserModel;
import com.example.planic.utils.AndroidUtil;
import com.example.planic.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etUsername, etPassword, etPasswordConfirm;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.register_email_et);
        etUsername = findViewById(R.id.register_username_et);
        etPassword = findViewById(R.id.register_password_et);
        etPasswordConfirm = findViewById(R.id.register_password_confirm_et);
        Button btnRegister = findViewById(R.id.register_btn);
        TextView tvLogin = findViewById(R.id.login_tv);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        SpannableString ss = getSs();
        tvLogin.setText(ss);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String passConf = etPasswordConfirm.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(pass) || TextUtils.isEmpty(passConf)) {
                AndroidUtil.showToast(this, "Semua field harus diisi");
                return;
            }
            if (!pass.equals(passConf)) {
                AndroidUtil.showToast(this, "Password dan konfirmasi tidak cocok");
                return;
            }

            db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener((QuerySnapshot snapUser) -> {
                        if (!snapUser.isEmpty()) {
                            AndroidUtil.showToast(this, "Username sudah digunakan");
                            return;
                        }
                        db.collection("users")
                                .whereEqualTo("email", email)
                                .get()
                                .addOnSuccessListener((QuerySnapshot snapEmail) -> {
                                    if (!snapEmail.isEmpty()) {
                                        AndroidUtil.showToast(this, "Email sudah digunakan");
                                        return;
                                    }
                                    createFirebaseUser(username, email, pass);
                                })
                                .addOnFailureListener(e ->
                                        AndroidUtil.showToast(this, "Gagal cek email: " + e.getMessage()));
                    })
                    .addOnFailureListener(e ->
                            AndroidUtil.showToast(this, "Gagal cek username: " + e.getMessage()));
        });
    }

    @NonNull
    private SpannableString getSs() {
        String fullText = "Already have an account? Log In";
        SpannableString ss = new SpannableString(fullText);
        String linkText = "Log In";
        int start = fullText.indexOf(linkText);
        int end = start + linkText.length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(RegisterActivity.this, R.color.txt_color));
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private void createFirebaseUser(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = FirebaseUtil.currentUserId();

                    UserModel user = new UserModel();
                    user.setUserId(userId);
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setCreatedTimestamp(new Timestamp(new Date()));

                    FirebaseUtil.allUserCollectionReference()
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener(unused -> {
                                FirebaseUtil.logout();
                                AndroidUtil.showToast(this, "Registrasi berhasil, silakan login");
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    AndroidUtil.showToast(this, "Gagal simpan data: " + e.getMessage()));
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        AndroidUtil.showToast(this, "Email sudah terdaftar");
                    } else {
                        AndroidUtil.showToast(this, "Registrasi gagal: " + e.getMessage());
                    }
                });
    }
}