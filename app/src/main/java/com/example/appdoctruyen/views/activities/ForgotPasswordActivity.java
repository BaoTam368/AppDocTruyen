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
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Password reset email sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String errorMsg = task.getException() != null ? task.getException().getLocalizedMessage() : "Something went wrong";
                    Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
