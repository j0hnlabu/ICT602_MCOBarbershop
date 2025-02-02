package com.example.login_page;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ListofServices extends AppCompatActivity {
    // Models
    private static class ServiceModel {
        String id;
        String categoryId;
        String name;
        String description;
        int duration;
        double price;
        int imageResourceId;
        boolean isBookmarked;

        ServiceModel(String id, String categoryId, String name, String description,
                     int duration, double price, int imageResourceId) {
            this.id = id;
            this.categoryId = categoryId;
            this.name = name;
            this.description = description;
            this.duration = duration;
            this.price = price;
            this.imageResourceId = imageResourceId;
            this.isBookmarked = false;
        }
    }

    private static class CategoryModel {
        String id;
        String name;
        int imageResourceId;
        List<ServiceModel> services;

        CategoryModel(String id, String name, int imageResourceId) {
            this.id = id;
            this.name = name;
            this.imageResourceId = imageResourceId;
            this.services = new ArrayList<>();
        }
    }

    // Adapters
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
        private List<CategoryModel> categories;

        CategoryAdapter(List<CategoryModel> categories) {
            this.categories = categories;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View categoryView = getLayoutInflater().inflate(R.layout.item_category_layout, parent, false);
            return new CategoryViewHolder(categoryView);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            CategoryModel category = categories.get(position);
            holder.categoryTitle.setText(category.name);
            holder.categoryImage.setImageResource(category.imageResourceId);
            ServiceAdapter serviceAdapter = new ServiceAdapter(category.services);
            holder.servicesRecyclerView.setAdapter(serviceAdapter);
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        class CategoryViewHolder extends RecyclerView.ViewHolder {
            ImageView categoryImage;
            TextView categoryTitle;
            RecyclerView servicesRecyclerView;

            CategoryViewHolder(View itemView) {
                super(itemView);
                categoryImage = itemView.findViewById(R.id.categoryImage);
                categoryTitle = itemView.findViewById(R.id.categoryTitle);
                servicesRecyclerView = itemView.findViewById(R.id.servicesRecyclerView);
                servicesRecyclerView.setLayoutManager(
                        new LinearLayoutManager(ListofServices.this, LinearLayoutManager.HORIZONTAL, false));
            }
        }
    }

    private class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
        private List<ServiceModel> services;

        ServiceAdapter(List<ServiceModel> services) {
            this.services = services;
        }

        @NonNull
        @Override
        public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View serviceView = getLayoutInflater().inflate(R.layout.item_service_layout, parent, false);
            return new ServiceViewHolder(serviceView);
        }

        @Override
        public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
            ServiceModel service = services.get(position);
            holder.serviceImage.setImageResource(service.imageResourceId);
            holder.serviceName.setText(service.name);
            holder.serviceDescription.setText(service.description);
            holder.serviceDuration.setText(service.duration + " mins");
            holder.servicePrice.setText(String.format("RM%.2f", service.price));

            holder.bookmarkButton.setImageResource(
                    service.isBookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border);

            holder.bookmarkButton.setOnClickListener(v -> {
                service.isBookmarked = !service.isBookmarked;
                notifyItemChanged(position);
                saveBookmarkStatus(service.id, service.isBookmarked);

                String message = service.isBookmarked ?
                        "Service added to bookmarks" :
                        "Service removed from bookmarks";
                Toast.makeText(ListofServices.this, message, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return services.size();
        }

        class ServiceViewHolder extends RecyclerView.ViewHolder {
            ImageView serviceImage;
            TextView serviceName;
            TextView serviceDescription;
            TextView serviceDuration;
            TextView servicePrice;
            ImageButton bookmarkButton;

            ServiceViewHolder(View itemView) {
                super(itemView);
                serviceImage = itemView.findViewById(R.id.serviceImage);
                serviceName = itemView.findViewById(R.id.serviceName);
                serviceDescription = itemView.findViewById(R.id.serviceDescription);
                serviceDuration = itemView.findViewById(R.id.serviceDuration);
                servicePrice = itemView.findViewById(R.id.servicePrice);
                bookmarkButton = itemView.findViewById(R.id.bookmarkButton);
            }
        }
    }

    // Class variables
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<CategoryModel> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        setupViews();
        setupCategories();
    }

    private void setupViews() {
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categories);
        categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupCategories() {
        // Create categories
        CategoryModel hairServices = new CategoryModel(
                "hair",
                "Hair Services",
                R.drawable.icon
        );

        CategoryModel shavingServices = new CategoryModel(
                "shaving",
                "Shaving",
                R.drawable.icon1
        );

        CategoryModel facialServices = new CategoryModel(
                "facial",
                "Face Masking",
                R.drawable.icon2
        );

        // Add services to Hair category
        List<ServiceModel> hairServicesList = new ArrayList<>();
        hairServicesList.add(new ServiceModel(
                "h1", "hair",
                "Classic Haircut",
                "Traditional haircut with scissors and clippers",
                30, 35.00,
                R.drawable.service_classic_haircut
        ));
        hairServicesList.add(new ServiceModel(
                "h2", "hair",
                "Fade Haircut",
                "Modern fade with precise tapering",
                45, 45.00,
                R.drawable.service_fade_haircut
        ));
        hairServices.services = hairServicesList;

        // Add services to Shaving category
        List<ServiceModel> shavingServicesList = new ArrayList<>();
        shavingServicesList.add(new ServiceModel(
                "s1", "shaving",
                "Classic Shave",
                "Traditional straight razor shave with hot towel",
                25, 30.00,
                R.drawable.service_classic_shave
        ));
        shavingServicesList.add(new ServiceModel(
                "s2", "shaving",
                "Beard Trim",
                "Professional beard grooming and shaping",
                20, 25.00,
                R.drawable.service_beard_trim
        ));
        shavingServices.services = shavingServicesList;

        // Add services to Facial category
        List<ServiceModel> facialServicesList = new ArrayList<>();
        facialServicesList.add(new ServiceModel(
                "f1", "facial",
                "Basic Face Mask",
                "Deep cleansing face mask treatment",
                20, 40.00,
                R.drawable.service_face_mask
        ));
        facialServicesList.add(new ServiceModel(
                "f2", "facial",
                "Premium Facial",
                "Luxury facial treatment with massage",
                40, 60.00,
                R.drawable.service_premium_facial
        ));
        facialServices.services = facialServicesList;

        // Add all categories
        categories.add(hairServices);
        categories.add(shavingServices);
        categories.add(facialServices);

        categoryAdapter.notifyDataSetChanged();
    }

    private void saveBookmarkStatus(String serviceId, boolean isBookmarked) {
        SharedPreferences prefs = getSharedPreferences("BookmarkPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("bookmark_" + serviceId, isBookmarked).apply();
    }
}