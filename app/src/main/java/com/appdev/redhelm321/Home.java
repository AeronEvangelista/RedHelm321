package com.appdev.redhelm321;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appdev.redhelm321.chat_room.ConnectNearbyActivity;
import com.appdev.redhelm321.info_features.FireStations;
import com.appdev.redhelm321.info_features.LocalGovernment;
import com.appdev.redhelm321.info_features.MedicalAssistance;
import com.appdev.redhelm321.info_features.MotorAndTrafficAssistance;
import com.appdev.redhelm321.info_features.NationalDisasterHotlines;
import com.appdev.redhelm321.info_features.PoliceStations;
import com.appdev.redhelm321.info_features.RedCross;
import com.appdev.redhelm321.utils.FirebaseAuthUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuthUtils firebaseAuthUtils;
    private FirebaseDatabase firebaseDatabase;


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
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {

            Toast.makeText(this, "Welcome! " + mAuth.getCurrentUser().getDisplayName() , Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Welcome! UNKNOWN" , Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        findViewById(R.id.policestation).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, PoliceStations.class);
            startActivity(intent);
        });

        findViewById(R.id.medass).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, MedicalAssistance.class);
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

        findViewById(R.id.connectnearby).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, ConnectNearbyActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.bmp).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, BroadCastMyPhone.class);
            startActivity(intent);
        });
    }
}