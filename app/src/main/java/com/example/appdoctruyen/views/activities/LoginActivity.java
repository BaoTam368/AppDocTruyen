package com.example.appdoctruyen.views.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private static final String TAG = "AuthDebug";
    private AuthManager authManager;
    private EditText edtUsername, edtPassword;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    OAuthProvider.Builder twitterProvider;
    private TextView tvForgotPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthManager(this);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);

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
        Button btnContinueGuest = findViewById(R.id.btn_continue_guest);
        btnContinueGuest.setOnClickListener(v -> goToMainActivity());

//        Click nút Đăng Nhập -> Mở màn hình chính (MainActivity)
        btnLogin.setOnClickListener(v -> {
            String email = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Validate email field
            if (email.isEmpty()) {
                edtUsername.setError("Please enter your email");
                edtUsername.requestFocus();
                return;
            }

            // Validate email format
            if (!isValidEmail(email)) {
                edtUsername.setError("Invalid email");
                edtUsername.requestFocus();
                return;
            }

            // Validate password field
            if (password.isEmpty()) {
                edtPassword.setError("Please enter your password");
                edtPassword.requestFocus();
                return;
            }

            authManager.login(email, password, new AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Log.d(TAG, "Email login SUCCESS, uid=" + user.getUid());
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e(TAG, "Email login FAILED: " + errorMessage);
                    Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
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
                    public void onSuccess(FirebaseUser user) {
                        Log.d(TAG, "Facebook login SUCCESS, uid=" + user.getUid());
                        Toast.makeText(LoginActivity.this, "Facebook login successful!", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e(TAG, "Facebook login FAILED: " + errorMessage);
                        Toast.makeText(LoginActivity.this, "Firebase error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "Facebook login CANCELLED by user");
                Toast.makeText(LoginActivity.this, "Facebook login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Facebook login ERROR: " + error.getMessage(), error);
                Toast.makeText(LoginActivity.this, "Facebook error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
                            Log.d(TAG, "X (Twitter) login SUCCESS (pending), uid=" + authResult.getUser().getUid());
                            authManager.syncUserToBackend(authResult.getUser(), null);
                            Toast.makeText(LoginActivity.this, "X login successful!", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "X (Twitter) login FAILED (pending): " + e.getMessage(), e);
                            Toast.makeText(LoginActivity.this, "X error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            } else {
                mAuth.startActivityForSignInWithProvider(LoginActivity.this, twitterProvider.build())
                        .addOnSuccessListener(authResult -> {
                            Log.d(TAG, "X (Twitter) login SUCCESS, uid=" + authResult.getUser().getUid());
                            authManager.syncUserToBackend(authResult.getUser(), null);
                            Toast.makeText(LoginActivity.this, "X login successful!", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "X (Twitter) login FAILED: " + e.getMessage(), e);
                            Toast.makeText(LoginActivity.this, "X error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
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
                Log.d(TAG, "Google Sign-In result: resultCode=" + result.getResultCode());

                if (result.getData() == null) {
                    Log.e(TAG, "Google Sign-In: result data is null, resultCode=" + result.getResultCode());
                    Toast.makeText(this, "Google Sign-In cancelled or failed (no data returned)", Toast.LENGTH_LONG).show();
                    return;
                }

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account == null) {
                        Log.e(TAG, "Google Sign-In: account is null after getResult");
                        Toast.makeText(this, "Google Sign-In failed: account is null", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String idToken = account.getIdToken();
                    if (idToken == null) {
                        Log.e(TAG, "Google Sign-In: idToken is null. Check requestIdToken() in GoogleSignInOptions.");
                        Toast.makeText(this, "Google Sign-In failed: idToken is null", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Log.d(TAG, "Google Sign-In: got idToken, calling loginWithGoogle...");
                    authManager.loginWithGoogle(idToken, new AuthCallback() {
                        @Override
                        public void onSuccess(FirebaseUser user) {
                            Log.d(TAG, "Google loginWithGoogle SUCCESS, uid=" + user.getUid());
                            Toast.makeText(LoginActivity.this, "Google login successful!", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e(TAG, "Google loginWithGoogle FAILED: " + errorMessage);
                            Toast.makeText(LoginActivity.this, "Google login error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (ApiException e) {
                    Log.e(TAG, "Google Sign-In ApiException: statusCode=" + e.getStatusCode() + ", message=" + e.getMessage(), e);
                    Toast.makeText(this, "Google Sign-In error (code " + e.getStatusCode() + "): " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
