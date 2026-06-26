package com.example.appdoctruyen.views.activities; // Sửa lại tên package cho đúng với dự án của bạn

import android.content.Intent;
import android.os.Bundle;
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
    private AuthManager authManager;
    private EditText edtEmailPhone, edtUsername, edtPassword, edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authManager = new AuthManager();
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
                edtEmailPhone.setError("Vui lòng nhập email");
                edtEmailPhone.requestFocus();
                return;
            }

            // Validate email format
            if (!isValidEmail(email)) {
                edtEmailPhone.setError("Email không hợp lệ");
                edtEmailPhone.requestFocus();
                return;
            }

            // Validate username
            if (username.isEmpty()) {
                edtUsername.setError("Vui lòng nhập tên đăng nhập");
                edtUsername.requestFocus();
                return;
            }

            if (username.length() < 3) {
                edtUsername.setError("Tên đăng nhập phải từ 3 ký tự trở lên");
                edtUsername.requestFocus();
                return;
            }

            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                edtUsername.setError("Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới");
                edtUsername.requestFocus();
                return;
            }

            // Validate password
            if (password.isEmpty()) {
                edtPassword.setError("Vui lòng nhập mật khẩu");
                edtPassword.requestFocus();
                return;
            }

            if (password.length() < 6) {
                edtPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
                edtPassword.requestFocus();
                return;
            }

            // Validate confirm password
            if (confirmPassword.isEmpty()) {
                edtConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
                edtConfirmPassword.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                edtConfirmPassword.setError("Mật khẩu không khớp");
                edtConfirmPassword.requestFocus();
                return;
            }

            authManager.register(email, password, new AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(RegisterActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });

        Button btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
}