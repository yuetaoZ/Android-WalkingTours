package com.example.walkingtours;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        ConstraintLayout layout = findViewById(R.id.layout);
        TextView nameText = findViewById(R.id.name);
        TextView addressText = findViewById(R.id.address);
        TextView latLonText = findViewById(R.id.lat_lon);

        FenceData fd = (FenceData) getIntent().getSerializableExtra("DATA");

        if (fd != null) {
            layout.setBackgroundColor(Color.parseColor(fd.getFenceColor()));
            nameText.setText(fd.getId());
            addressText.setText(fd.getAddress());
            latLonText.setText(
                    String.format(Locale.getDefault(), "%.5f, %.5f", fd.getLat(), fd.getLon()));
        }
    }
}