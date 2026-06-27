package com.example.appdoctruyen.views.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.firebase.AuthCallback;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private AuthManager authManager;
    private EditText edtUsername, edtPassword;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    OAuthProvider.Builder twitterProvider;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthManager(this);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);

        mCallbackManager = CallbackManager.Factory.create();
        twitterProvider = OAuthProvider.newBuilder("twitter.com");

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

        // Google
        View imgGoogleLogin = findViewById(R.id.img_google_login);
        imgGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        // Facebook
        View imgFacebookLogin = findViewById(R.id.img_facebook_login);
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                authManager.loginWithFacebook(accessToken.getToken(), new AuthCallback() {
                    @Override
                    public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                        android.widget.Toast.makeText(LoginActivity.this, "Đăng nhập Facebook thành công!", android.widget.Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        android.widget.Toast.makeText(LoginActivity.this, "Lỗi Firebase: " + errorMessage, android.widget.Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancel() {
                android.widget.Toast.makeText(LoginActivity.this, "Đã hủy đăng nhập Facebook", android.widget.Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                android.widget.Toast.makeText(LoginActivity.this, "Lỗi Facebook: " + error.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            }
        });

        imgFacebookLogin.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        });

        // X
        View imgTwitterLogin = findViewById(R.id.img_twitter_login);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        imgTwitterLogin.setOnClickListener(v -> {
            Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();

            if (pendingResultTask != null) {
                pendingResultTask.addOnSuccessListener(authResult -> {
                            android.widget.Toast.makeText(LoginActivity.this, "Đăng nhập X thành công!", android.widget.Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        })
                        .addOnFailureListener(e -> {
                            android.widget.Toast.makeText(LoginActivity.this, "Lỗi X: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                        });
            } else {
                mAuth.startActivityForSignInWithProvider(LoginActivity.this, twitterProvider.build())
                        .addOnSuccessListener(authResult -> {
                            android.widget.Toast.makeText(LoginActivity.this, "Đăng nhập X thành công!", android.widget.Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        })
                        .addOnFailureListener(e -> {
                            android.widget.Toast.makeText(LoginActivity.this, "Lỗi X: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                        });
            }
        });

        Button btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
           // finish();

        });
    }

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null && account.getIdToken() != null) {
                            authManager.loginWithGoogle(account.getIdToken(), new AuthCallback() {
                                @Override
                                public void onSuccess(FirebaseUser user) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
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
