package com.example.appdoctruyen.views.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdoctruyen.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private EditText edtUsername, edtPhone, edtAddress;
    private Button btnSave;
    private ImageView btnBack;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtUsername = findViewById(R.id.edt_edit_username);
        edtPhone = findViewById(R.id.edt_edit_phone);
        edtAddress = findViewById(R.id.edt_edit_address);
        btnSave = findViewById(R.id.btn_save_profile);
        btnBack = findViewById(R.id.iv_edit_back);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnBack.setOnClickListener(v -> finish());

        if (currentUser != null) {
            loadExistingData();
        }

        btnSave.setOnClickListener(v -> {
            if (currentUser != null) {
                saveDataToFirebase();
            } else {
                Toast.makeText(this, "You have to sign in", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadExistingData() {
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("username");
                        String phone = documentSnapshot.getString("phone");
                        String address = documentSnapshot.getString("address");

                        if (name != null) edtUsername.setText(name);
                        if (phone != null) edtPhone.setText(phone);
                        if (address != null) edtAddress.setText(address);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Cant load old data " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveDataToFirebase() {
        String newName = edtUsername.getText().toString().trim();
        String newPhone = edtPhone.getText().toString().trim();
        String newAddress = edtAddress.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "You have to fill in display name", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", newName);
        updates.put("phone", newPhone);
        updates.put("address", newAddress);

        // Đẩy lên Firestore
        db.collection("users").document(currentUser.getUid())
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Save successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Bắt gọn lỗi nếu có để biết tại sao không lưu được
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}