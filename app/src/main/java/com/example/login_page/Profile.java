package com.example.login_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference userRef;

    private TextView emailTextView, nameTextView;
    private EditText phoneEditText;
    private Button updateButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        // Initialize views
        emailTextView = findViewById(R.id.email_text_view);
        nameTextView = findViewById(R.id.name_text_view);
        phoneEditText = findViewById(R.id.phone_edit_text);
        updateButton = findViewById(R.id.update_button);
        backButton = findViewById(R.id.back_button);

        // Load user data
        loadUserData();

        // Set update button listener
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePhoneNumber();
            }
        });

        // Set back button listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadUserData() {
        if (user != null) {
            emailTextView.setText(user.getEmail());

            // Retrieve additional user details from Realtime Database
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);

                    nameTextView.setText(name != null ? name : "No Name Available");
                    phoneEditText.setText(phoneNumber != null ? phoneNumber : "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Profile.this, "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
            finish(); // Redirect back to login if no user is logged in
        }
    }

    private void updatePhoneNumber() {
        String newPhoneNumber = phoneEditText.getText().toString().trim();

        if (newPhoneNumber.isEmpty()) {
            Toast.makeText(this, "Phone number cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update phone number in Firebase Realtime Database
        userRef.child("phoneNumber").setValue(newPhoneNumber)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Profile.this, "Phone number updated successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Profile.this, "Failed to update phone number: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
