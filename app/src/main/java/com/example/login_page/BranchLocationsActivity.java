package com.example.login_page;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.maps.model.LatLngBounds;

public class BranchLocationsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    private static class BranchLocation {
        String name;
        LatLng location;
        String address;

        BranchLocation(String name, LatLng location, String address) {
            this.name = name;
            this.location = location;
            this.address = address;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        setupMap();
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addBranchLocations();
    }

    private void addBranchLocations() {
        List<BranchLocation> branches = new ArrayList<>();
        branches.add(new BranchLocation("M.CO Barbershop Shah Alam",
                new LatLng(3.0652654666554917, 101.48902900983266),
                "Shah Alam Branch"));
        branches.add(new BranchLocation("M.CO Barbershop Semenyih",
                new LatLng(2.9284433677191273, 101.85729908284597),
                "Semenyih Branch"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<Marker> markers = new ArrayList<>();

        for (BranchLocation branch : branches) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(branch.location)
                    .title(branch.name)
                    .snippet(branch.address));

            if (marker != null) {
                markers.add(marker);
            }

            builder.include(branch.location);
        }

        // Adjust the camera to fit all markers
        if (!branches.isEmpty()) {
            mMap.setOnMapLoadedCallback(() -> {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

                // Show the info window for all markers
                for (Marker marker : markers) {
                    marker.showInfoWindow();
                }
            });
        }
    }}
