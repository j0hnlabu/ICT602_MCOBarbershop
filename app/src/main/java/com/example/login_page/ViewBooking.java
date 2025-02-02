package com.example.login_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.util.ArrayList;

public class ViewBooking extends AppCompatActivity {

    private ListView bookingsListView;
    private Button btnMarkDone;
    private DatabaseReference bookingsRef;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private ArrayList<String> bookingsList;
    private ArrayList<String> bookingIds;
    private ArrayList<String> bookingStatuses;
    private ImageButton backButton;

    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");

        // Initialize UI Elements
        bookingsListView = findViewById(R.id.bookings_list_view);
        btnMarkDone = findViewById(R.id.btn_mark_done);
        backButton = findViewById(R.id.back_button);
        bookingsList = new ArrayList<>();
        bookingIds = new ArrayList<>();
        bookingStatuses = new ArrayList<>();

        // Fetch bookings from Firebase
        loadBookings();

        // Back Button to go back to Dashboard
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(ViewBooking.this, Dashboard.class);
            startActivity(intent);
            finish();
        });

        // ListView Item Click Listener (Select Booking)
        bookingsListView.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedPosition = position;
            String status = bookingStatuses.get(position);

            // Enable button only if the selected booking is "Pending"
            if (status.equals("Pending")) {
                btnMarkDone.setEnabled(true);
                Toast.makeText(this, "Selected: " + bookingsList.get(position), Toast.LENGTH_SHORT).show();
            } else {
                btnMarkDone.setEnabled(false);
                Toast.makeText(this, "This appointment is already completed!", Toast.LENGTH_SHORT).show();
            }
        });

        // Button Click to Mark as Completed and Go to Review Page
        btnMarkDone.setOnClickListener(view -> {
            if (selectedPosition != -1) {
                String bookingId = bookingIds.get(selectedPosition);
                String status = bookingStatuses.get(selectedPosition);

                if (status.equals("Pending")) {
                    updateBookingStatusAndGoToReview(bookingId);
                } else {
                    Toast.makeText(this, "This appointment is already completed!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select a booking first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBookings() {
        if (currentUser != null) {
            bookingsRef.orderByChild("userId").equalTo(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            bookingsList.clear();
                            bookingIds.clear();
                            bookingStatuses.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String bookingId = snapshot.getKey();
                                String service = snapshot.child("service").getValue(String.class);
                                String date = snapshot.child("date").getValue(String.class);
                                String time = snapshot.child("time").getValue(String.class);
                                String status = snapshot.child("status").getValue(String.class);

                                String bookingDetails = "Service: " + service +
                                        "\nDate: " + date +
                                        "\nTime: " + time +
                                        "\nStatus: " + status;

                                bookingsList.add(bookingDetails);
                                bookingIds.add(bookingId);
                                bookingStatuses.add(status);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewBooking.this,
                                    android.R.layout.simple_list_item_1, bookingsList);
                            bookingsListView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ViewBooking.this, "Error loading bookings", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // ✅ Update Status to "Completed" & Go to Review Page
    private void updateBookingStatusAndGoToReview(String bookingId) {
        bookingsRef.child(bookingId).child("status").setValue("Completed")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ViewBooking.this, "Appointment marked as Completed!", Toast.LENGTH_SHORT).show();

                        // Go to Customer Review Page
                        Intent intent = new Intent(ViewBooking.this, CustomerReview.class);
                        intent.putExtra("BOOKING_ID", bookingId);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ViewBooking.this, "Failed to update status!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
