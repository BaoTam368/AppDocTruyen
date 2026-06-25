package com.example.appdoctruyen.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.firebase.AuthCallback;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private AuthManager authManager;
    private EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AuthManager authManager = new AuthManager();
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);

        if (authManager.isUserLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        Button btnLogin = findViewById(R.id.btn_login);

//        Click nút Đăng Nhập -> Mở màn hình chính (MainActivity)
        btnLogin.setOnClickListener(v -> {
            String email = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tài khoản và mật khẩu", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            authManager.login(email, password, new AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", android.widget.Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(LoginActivity.this, "Lỗi: " + errorMessage, android.widget.Toast.LENGTH_LONG).show();
                }
            });
        });

        Button btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
           // finish();

        });
    }
}
