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

import com.example.appdoctruyen.R;

public class CreatePostActivity extends AppCompatActivity {

    private ImageView btnClose, imagePreview;
    private TextView btnPost;
    private EditText editCaption;
    private LinearLayout btnPickImage;
    private Uri selectedImageUri = null;
    private ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imagePreview.setImageURI(selectedImageUri);
                    btnPickImage.setVisibility(View.GONE);
                    imagePreview.setVisibility(View.VISIBLE);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        btnClose = findViewById(R.id.btn_close);
        btnPost = findViewById(R.id.btn_post);
        editCaption = findViewById(R.id.edit_caption);
        btnPickImage = findViewById(R.id.btn_pick_image);
        imagePreview = findViewById(R.id.image_preview);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                pickImageLauncher.launch(intent);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = editCaption.getText().toString().trim();
                if (caption.isEmpty() && selectedImageUri == null) {
                    Toast.makeText(CreatePostActivity.this, "Please enter content or select an image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(CreatePostActivity.this, "Post created successfully!\nContent: " + caption, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
