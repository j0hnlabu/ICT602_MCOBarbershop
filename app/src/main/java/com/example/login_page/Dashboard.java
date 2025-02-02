package com.example.login_page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity {

    private Button btnServices, btnAppointment, btnViewAppointment, btnProfile, btnLogout, btnAboutUs, btnBranchLocations;
    private TextView welcomeTextView;
    private ImageView logoImageView; // Logo

    private FirebaseAuth auth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());

        // Initialize UI elements
        welcomeTextView = findViewById(R.id.welcome_text_view);
        logoImageView = findViewById(R.id.logo_image); // Logo Image
        btnServices = findViewById(R.id.btn_services);
        btnAppointment = findViewById(R.id.btn_appointment);
        btnViewAppointment = findViewById(R.id.btn_view_appointment);
        btnProfile = findViewById(R.id.btn_profile);
        btnLogout = findViewById(R.id.btn_logout);
        btnAboutUs = findViewById(R.id.btn_about_us);
        btnBranchLocations = findViewById(R.id.btn_branch_locations);

        // Load welcome message
        loadWelcomeMessage();

        // Set button click listeners
        btnServices.setOnClickListener(view -> startActivity(new Intent(Dashboard.this, ListofServices.class)));
        btnAppointment.setOnClickListener(view -> startActivity(new Intent(Dashboard.this, BookingAppointment.class)));
        btnViewAppointment.setOnClickListener(view -> startActivity(new Intent(Dashboard.this, ViewBooking.class)));
        btnProfile.setOnClickListener(view -> startActivity(new Intent(Dashboard.this, Profile.class)));
        btnAboutUs.setOnClickListener(view -> startActivity(new Intent(Dashboard.this, AboutUsActivity.class)));
        btnBranchLocations.setOnClickListener(view -> startActivity(new Intent(Dashboard.this, BranchLocationsActivity.class)));

        // Logout Button
        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Dashboard.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadWelcomeMessage() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                welcomeTextView.setText(name != null ? "Welcome, " + name + " !" : "Welcome!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                welcomeTextView.setText("Welcome!");
            }
        });
    }
}
