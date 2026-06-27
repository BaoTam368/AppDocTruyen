package com.example.appdoctruyen.views.activities;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdoctruyen.R;
import com.google.firebase.auth.FirebaseAuth;
public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.edt_email);
        Button btnSendRequest = findViewById(R.id.btn_send_request);

        btnSendRequest.setOnClickListener(v -> {
            String emailAddress = edtEmail.getText().toString().trim();

            if (emailAddress.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Email khôi phục đã được gửi! Vui lòng kiểm tra hộp thư.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String errorMsg = task.getException() != null ? task.getException().getLocalizedMessage() : "Có lỗi xảy ra";
                    Toast.makeText(this, "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
