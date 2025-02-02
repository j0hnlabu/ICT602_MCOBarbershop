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

public class MainActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        emailField = findViewById(R.id.email_input);
        passwordField = findViewById(R.id.password_input);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // Method to handle login process
    public void openLogin(View v) {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields cannot be blank", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, Dashboard.class); // Navigate to the Profile activity
                                startActivity(intent);
                                finish(); // Prevent returning to the login screen
                            } else {
                                Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Method to open the Register Activity
    public void openRegister(View v) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    // Method to open the Forgot Password Activity
    public void openForgotPassword(View v) {
        Intent intent = new Intent(this, ForgotPass.class);
        startActivity(intent);
    }
}
