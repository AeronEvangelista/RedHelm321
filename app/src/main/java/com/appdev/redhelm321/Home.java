package com.appdev.redhelm321;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupClickListeners();
    }

    private void setupClickListeners() {
        findViewById(R.id.policestation).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, PoliceStations.class);
            startActivity(intent);
        });

        findViewById(R.id.medass).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Profile.class);
            startActivity(intent);
        });

        findViewById(R.id.firestations).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, FireStations.class);
            startActivity(intent);
        });

        findViewById(R.id.mata).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, MotorAndTrafficAssistance.class);
            startActivity(intent);
        });

        findViewById(R.id.ndh).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, NationalDisasterHotlines.class);
            startActivity(intent);
        });

        findViewById(R.id.redcross).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, RedCross.class);
            startActivity(intent);
        });

        findViewById(R.id.localgov).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, LocalGovernment.class);
            startActivity(intent);
        });

    }
}