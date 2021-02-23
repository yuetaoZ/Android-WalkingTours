package com.example.walkingtours;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Possibly chck perm's here
        // Possibly load required resources here

        // Handler is used to execute something in the future
        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            // Start your app main activity
            Intent i =
                    new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out); // new act, old act
            // close this activity
            finish();
        }, SPLASH_TIME_OUT);
    }
}
