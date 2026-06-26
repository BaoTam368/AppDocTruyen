package com.example.appdoctruyen.views.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.firebase.AuthCallback;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private AuthManager authManager;
    private EditText edtUsername, edtPassword;
    private GoogleSignInClient mGoogleSignInClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthManager();
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);

        if (authManager.isUserLoggedIn()) {
            goToMainActivity();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button btnLogin = findViewById(R.id.btn_login);

//        Click nút Đăng Nhập -> Mở màn hình chính (MainActivity)
        btnLogin.setOnClickListener(v -> {
            String email = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Validate email field
            if (email.isEmpty()) {
                edtUsername.setError("Vui lòng nhập email");
                edtUsername.requestFocus();
                return;
            }

            // Validate email format
            if (!isValidEmail(email)) {
                edtUsername.setError("Email không hợp lệ");
                edtUsername.requestFocus();
                return;
            }

            // Validate password field
            if (password.isEmpty()) {
                edtPassword.setError("Vui lòng nhập mật khẩu");
                edtPassword.requestFocus();
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

                @SuppressLint("MissingInflatedId")
                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(LoginActivity.this, "Lỗi: " + errorMessage, android.widget.Toast.LENGTH_LONG).show();
                }
            });
        });

        View imgGoogleLogin = findViewById(R.id.img_google_login);
        imgGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        Button btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
           // finish();

        });
    }

    private final androidx.activity.result.ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null && account.getIdToken() != null) {
                            authManager.loginWithGoogle(account.getIdToken(), new AuthCallback() {
                                @Override
                                public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                                    android.widget.Toast.makeText(LoginActivity.this, "Đăng nhập Google thành công!", android.widget.Toast.LENGTH_SHORT).show();
                                    goToMainActivity();
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    android.widget.Toast.makeText(LoginActivity.this, "Lỗi: " + errorMessage, android.widget.Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (ApiException e) {
                        android.widget.Toast.makeText(this, "Lỗi kết nối Google: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
}
