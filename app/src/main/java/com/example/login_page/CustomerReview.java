package com.example.login_page;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CustomerReview extends AppCompatActivity {

    private EditText etReview;
    private RatingBar rbRatings;
    private Button btnSubmitReview;
    private DatabaseReference reviewsRef;
    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        etReview = findViewById(R.id.et_review);
        rbRatings = findViewById(R.id.rb_ratings);
        btnSubmitReview = findViewById(R.id.btn_submit_review);

        // Initialize Firebase Reference
        reviewsRef = FirebaseDatabase.getInstance().getReference("Reviews");

        // Get Booking ID
        bookingId = getIntent().getStringExtra("BOOKING_ID");

        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        String reviewText = etReview.getText().toString().trim();
        float rating = rbRatings.getRating();

        if (TextUtils.isEmpty(reviewText) || rating == 0) {
            Toast.makeText(this, "Please provide a rating and review!", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> reviewData = new HashMap<>();
        reviewData.put("bookingId", bookingId);
        reviewData.put("review", reviewText);
        reviewData.put("rating", rating);

        reviewsRef.child(bookingId).setValue(reviewData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Review Submitted!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to submit review!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
