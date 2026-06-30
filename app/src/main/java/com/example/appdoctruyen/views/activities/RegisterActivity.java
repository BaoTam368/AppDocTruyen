package com.example.appdoctruyen.views.activities; // Sửa lại tên package cho đúng với dự án của bạn

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.firebase.AuthCallback;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "AuthDebug";
    private AuthManager authManager;
    private EditText edtEmailPhone, edtUsername, edtPassword, edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authManager = new AuthManager(this);
        edtEmailPhone = findViewById(R.id.edt_email_phone);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        Button btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            String email = edtEmailPhone.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            // Validate email/phone field
            if (email.isEmpty()) {
                edtEmailPhone.setError("Please enter your email");
                edtEmailPhone.requestFocus();
                return;
            }

            // Validate email format
            if (!isValidEmail(email)) {
                edtEmailPhone.setError("Invalid email");
                edtEmailPhone.requestFocus();
                return;
            }

            // Validate username
            if (username.isEmpty()) {
                edtUsername.setError("Please enter a username");
                edtUsername.requestFocus();
                return;
            }

            if (username.length() < 3) {
                edtUsername.setError("Username must be at least 3 characters");
                edtUsername.requestFocus();
                return;
            }

            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                edtUsername.setError("Username can only contain letters, numbers, and underscores");
                edtUsername.requestFocus();
                return;
            }

            // Validate password
            if (password.isEmpty()) {
                edtPassword.setError("Please enter a password");
                edtPassword.requestFocus();
                return;
            }

            if (password.length() < 6) {
                edtPassword.setError("Password must be at least 6 characters");
                edtPassword.requestFocus();
                return;
            }

            // Validate confirm password
            if (confirmPassword.isEmpty()) {
                edtConfirmPassword.setError("Please confirm your password");
                edtConfirmPassword.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                edtConfirmPassword.setError("Passwords do not match");
                edtConfirmPassword.requestFocus();
                return;
            }

            authManager.register(email, password, username, new AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Log.d(TAG, "Register SUCCESS, uid=" + user.getUid());
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e(TAG, "Register FAILED: " + errorMessage);
                    Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });

        Button btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

