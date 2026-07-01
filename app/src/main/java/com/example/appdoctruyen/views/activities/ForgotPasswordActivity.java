package com.example.appdoctruyen.views.activities;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdoctruyen.R;
import com.google.firebase.auth.FirebaseAuth;
public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "AuthDebug";
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
                edtEmail.setError("Please type your email!");
                edtEmail.requestFocus();
                return;
            }

            // Validate email format
            if (!isValidEmail(emailAddress)) {
                edtEmail.setError("Invalid email format");
                edtEmail.requestFocus();
                return;
            }

            Log.d(TAG, "ForgotPassword: sending reset email to " + emailAddress);
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "ForgotPassword: reset email sent successfully to " + emailAddress);
                    Toast.makeText(this, "Recovery Email was sent, Please check your email!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String errorMsg = task.getException() != null ? task.getException().getLocalizedMessage() : "Error";
                    Log.e(TAG, "ForgotPassword: FAILED to send reset email: " + errorMsg, task.getException());
                    Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
