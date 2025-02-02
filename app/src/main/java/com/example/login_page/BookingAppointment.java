package com.example.login_page;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class BookingAppointment extends AppCompatActivity {

    private TextView tvName;
    private EditText edtDate, edtTime;
    private Spinner serviceSpinner;
    private Button btnBook;
    private ImageButton backButton;

    private DatabaseReference bookingsRef, servicesRef;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private static final String CHANNEL_ID = "appointment_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_appointment);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        servicesRef = FirebaseDatabase.getInstance().getReference("Services");

        // Initialize UI elements
        tvName = findViewById(R.id.tv_name);
        edtDate = findViewById(R.id.edt_date);
        edtTime = findViewById(R.id.edt_time);
        serviceSpinner = findViewById(R.id.spinner_service);
        btnBook = findViewById(R.id.btn_book);
        backButton = findViewById(R.id.back_button);

        // Create notification channel
        createNotificationChannel();

        // Load User Data
        loadUserData();

        // Load available services
        loadServices();

        // Handle Date Picker
        edtDate.setOnClickListener(view -> openDatePicker());

        // Handle Time Picker
        edtTime.setOnClickListener(view -> openTimePicker());

        // Handle booking button click
        btnBook.setOnClickListener(view -> bookAppointment());

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(BookingAppointment.this, Dashboard.class);
            startActivity(intent);
            finish(); // Close current activity
        });


    }

    private void loadUserData() {
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    tvName.setText(name != null ? name : "Unknown User");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    tvName.setText("Unknown User");
                }
            });
        }
    }

    private void loadServices() {
        List<String> serviceList = new ArrayList<>();
        serviceList.add("Select Service");

        servicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot serviceSnapshot : snapshot.getChildren()) {
                    String serviceName = serviceSnapshot.child("name").getValue(String.class);
                    if (serviceName != null) {
                        serviceList.add(serviceName);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingAppointment.this,
                        android.R.layout.simple_spinner_dropdown_item, serviceList);
                serviceSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingAppointment.this, "Failed to load services", Toast.LENGTH_SHORT).show();
            }
        });
    } //

    //
    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, day) -> {
            edtDate.setText(day + "/" + (month + 1) + "/" + year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(this, (view, hour, minute) -> {
            edtTime.setText(String.format("%02d:%02d", hour, minute));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePicker.show();
    }

    private void bookAppointment() {
        String name = tvName.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        String time = edtTime.getText().toString().trim();
        String selectedService = serviceSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || selectedService.equals("Select Service")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String bookingId = bookingsRef.push().getKey(); // Generate unique booking ID

            // Create a booking data structure
            HashMap<String, Object> bookingData = new HashMap<>();
            bookingData.put("userId", userId);
            bookingData.put("name", name);
            bookingData.put("date", date);
            bookingData.put("time", time);
            bookingData.put("service", selectedService);
            bookingData.put("status", "Pending");

            if (bookingId != null) {
                bookingsRef.child(bookingId).setValue(bookingData)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(BookingAppointment.this, "Appointment Booked!", Toast.LENGTH_SHORT).show();
                                finish(); // Close activity after booking
                            } else {
                                Toast.makeText(BookingAppointment.this, "Booking Failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void createNotificationChannel() {
        // Check if the Android version is Oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Appointment Channel";
            String description = "Channel for appointment booking notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void showNotification() {
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.noti)  // Use a small icon for the notification
                .setContentTitle("Appointment Booked")
                .setContentText("Your appointment has been successfully booked.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);  // Dismiss notification when clicked

        // Get the NotificationManager system service
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Show the notification
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}
