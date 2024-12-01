package com.appdev.redhelm321;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appdev.redhelm321.chat_room.WIFI_P2P_SharedData;
import com.appdev.redhelm321.chat_room.WiFiDirectBroadcastReceiver;

public class MainActivity extends AppCompatActivity {

    public static final int IP_PORT = 1307;

    WifiManager wifiManager;
    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel wifiP2pChannel;

    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeComponent();

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

    private void InitializeComponent() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WIFI_P2P_SharedData.setWifiManager(wifiManager);

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifiP2pChannel = wifiP2pManager.initialize(this, getMainLooper(), null);
        broadcastReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, wifiP2pChannel, this);
        WIFI_P2P_SharedData.setWifiP2pManager(wifiP2pManager);
        WIFI_P2P_SharedData.setWifiP2pChannel(wifiP2pChannel);
        WIFI_P2P_SharedData.setBroadcastReceiver(broadcastReceiver);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        WIFI_P2P_SharedData.setIntentFilter(intentFilter);
    }
}
