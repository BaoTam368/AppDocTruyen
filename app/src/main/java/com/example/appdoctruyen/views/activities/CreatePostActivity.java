package com.example.appdoctruyen.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.appdoctruyen.R;
import com.example.appdoctruyen.data.api.CreatePostRequest;
import com.example.appdoctruyen.data.api.MangaRepository;
import com.example.appdoctruyen.data.firebase.AuthManager;
import com.example.appdoctruyen.models.Post;
import com.example.appdoctruyen.models.User;

public class CreatePostActivity extends AppCompatActivity {

    private ImageView btnClose, imagePreview, imgAvatar;
    private TextView btnPost, tvDisplayName;
    private EditText editCaption;
    private LinearLayout btnPickImage;

    private Uri selectedImageUri = null;

    private MangaRepository repository;
    private AuthManager authManager;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
            selectedImageUri = uri;
            imagePreview.setImageURI(uri);
            btnPickImage.setVisibility(View.GONE);
            imagePreview.setVisibility(View.VISIBLE);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        repository = new MangaRepository();

        initViews();
        loadUserProfile();

        btnClose.setOnClickListener(v -> finish());
        btnPickImage.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });

        btnPost.setOnClickListener(v -> createPost());
    }

    private void initViews() {
        btnClose = findViewById(R.id.btn_close);
        btnPost = findViewById(R.id.btn_post);
        editCaption = findViewById(R.id.edit_caption);
        btnPickImage = findViewById(R.id.btn_pick_image);
        imagePreview = findViewById(R.id.image_preview);

        tvDisplayName = findViewById(R.id.tv_display_name);
        imgAvatar = findViewById(R.id.img_avatar);
        authManager = new AuthManager(this);

    }

    public void loadUserProfile() {
        String userId = authManager.getCurrentUserId();
        repository.getUserProfile(userId, new MangaRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {

                String name = user.getDisplayName();
                if (name == null || name.isEmpty()) {
                    name = user.getEmail() != null ? user.getEmail() : "User";
                }

                tvDisplayName.setText(name);
                Glide.with(CreatePostActivity.this).load(user.getAvatarUrl()).placeholder(android.R.drawable.ic_menu_myplaces).into(imgAvatar);
            }

            @Override
            public void onError(String message) {
                tvDisplayName.setText("User");
            }
        });
    }


    private void createPost() {
        String content = editCaption.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter content!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = authManager.getCurrentUserId();
        CreatePostRequest request = new CreatePostRequest(userId, content, selectedImageUri != null ? selectedImageUri.toString() : null);
        repository.createPost(request, new MangaRepository.RepositoryCallback<Post>() {
            @Override
            public void onSuccess(Post data) {

                Toast.makeText(CreatePostActivity.this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CreatePostActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}