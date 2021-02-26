package com.example.walkingtours;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        ConstraintLayout layout = findViewById(R.id.layout);
        TextView nameText = findViewById(R.id.name);
        TextView addressText = findViewById(R.id.address);
        TextView descriptionText = findViewById(R.id.description);
        descriptionText.setMovementMethod(new ScrollingMovementMethod());
        ImageView locationImage = findViewById(R.id.locationImage);

        FenceData fd = (FenceData) getIntent().getSerializableExtra("DATA");

        if (fd != null) {
            layout.setBackgroundColor(Color.parseColor("#015A5A"));
            nameText.setText(fd.getId());
            addressText.setText(fd.getAddress());
            descriptionText.setBackgroundColor(Color.parseColor("#FF03DAC5"));
            descriptionText.setText(fd.getDescription());
            String imageUrl = fd.getImage();
            Picasso.get().load(imageUrl).into(locationImage);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.home_image);
        setTitle("");
        return super.onCreateOptionsMenu(menu);
    }

}