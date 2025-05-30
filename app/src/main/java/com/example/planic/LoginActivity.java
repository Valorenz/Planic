package com.example.planic; // ganti dengan package milikmu

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private com.google.android.gms.common.SignInButton btnGoogle;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Pastikan nama layout XML-nya benar

        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi UI
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        btnGoogle = findViewById(R.id.btnGoogleSignIn);

        // Konfigurasi Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Pastikan string ini ada di strings.xml
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Tombol Login Email/Password
        btnLogin.setOnClickListener(view -> loginWithEmail());

        // Tombol Login Google
        btnGoogle.setOnClickListener(view -> signInWithGoogle());
    }

    private void loginWithEmail() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        goToHome();
                    } else {
                        Toast.makeText(this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        goToHome();
                    } else {
                        Toast.makeText(this, "Login Google gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goToHome() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            goToHome();
        }
    }
}
