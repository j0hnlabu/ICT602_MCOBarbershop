package com.example.login_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private EditText nameField, phoneField, emailField, passwordField, confirmPasswordField;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initializing the input fields
        nameField = findViewById(R.id.name_input);
        phoneField = findViewById(R.id.phone_input);
        emailField = findViewById(R.id.email_input);
        passwordField = findViewById(R.id.password_input);
        confirmPasswordField = findViewById(R.id.confirm_password_input);

        // Initializing Firebase Auth and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void createUser(View v) {
        String name = nameField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        // Validating input fields
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creating user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Save user data to Firebase Realtime Database
                            String userId = mAuth.getCurrentUser().getUid();
                            User newUser = new User(name, phone, email, password);
                            mDatabase.child(userId).setValue(newUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Register.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                                // Navigate to profile or another activity
                                                Intent intent = new Intent(Register.this, MainActivity.class);
                                                intent.putExtra("name", name);
                                                intent.putExtra("phone", phone);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(Register.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(Register.this, "Failed to create user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Back button functionality to navigate back when pressed
    public void onBackPressed(View view) {
        super.onBackPressed();
    }
}

// User class for storing user data in Realtime Database
class User {
    public String name;
    public String phone;
    public String email;
    public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String phone, String email, String password) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }
}
