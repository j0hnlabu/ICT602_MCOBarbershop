package com.example.login_page;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPass extends AppCompatActivity {

    private EditText emailField;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        // Initializing the input field
        emailField = findViewById(R.id.email_input_forgot);

        // Initializing Firebase Auth and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users"); // Assuming you store users here
    }

    public void resetPassword(View v) {
        String email = emailField.getText().toString().trim();

        // Validating the email field
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sending password reset email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPass.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();

                            // Optional: Update the Realtime Database with the reset request (e.g., a timestamp or status)
                            mDatabase.child(email.replace(".", ",")).child("password_reset_requested").setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ForgotPass.this, "User reset status updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ForgotPass.this, "Failed to update user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(ForgotPass.this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Back button functionality
    public void onBackPressed(View view) {
        super.onBackPressed();
    }
}
