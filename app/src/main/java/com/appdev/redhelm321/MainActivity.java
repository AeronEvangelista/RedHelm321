package com.appdev.redhelm321;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the views
        ImageView rhLogo = findViewById(R.id.rhLogo);  // First logo
        ImageView rhLogo2 = findViewById(R.id.rhLogo2);  // Second logo to animate
        LinearLayout loginLayout = findViewById(R.id.login_layout);  // Login layout
        ImageButton signInButton = findViewById(R.id.signIN);  // Sign In button

        // Initially set rhLogo to invisible
        rhLogo.setVisibility(View.INVISIBLE);

        // Load the animation for the second logo (rhLogo2)
        Animation moveUp = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        rhLogo2.startAnimation(moveUp);

        // Set animation listener to manage visibility after the animation
        moveUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Nothing to do here
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Make rhLogo2 disappear
                rhLogo2.setVisibility(View.INVISIBLE);  // Hide the second logo

                // Make rhLogo visible after the animation ends
                rhLogo.setVisibility(View.VISIBLE);  // Show the first logo

                // Make login layout visible after the animation ends
                loginLayout.setVisibility(View.VISIBLE);  // Make the login layout visible
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Nothing to do here
            }
        });

        // Set OnClickListener for Sign In button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to navigate to the Home activity
                Intent intent = new Intent(MainActivity.this, Home.class);  // Change "Home" to your Home activity class
                startActivity(intent);  // Start the new activity
                finish();  // Optionally finish the current activity if you don't want to go back to it
            }
        });
    }
}
