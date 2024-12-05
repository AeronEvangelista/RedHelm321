package com.appdev.redhelm321;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class connect2 extends AppCompatActivity {
    private RippleView rippleView;
    private Button connectButton;
    private boolean isDetecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect2);

        rippleView = findViewById(R.id.rippleView);
        connectButton = findViewById(R.id.connectButton);

        connectButton.setOnClickListener(v -> {
            if (isDetecting) {
                rippleView.stopRippleEffect();
            } else {
                rippleView.startRippleEffect(connectButton.getWidth());
            }
            isDetecting = !isDetecting;
        });
    }
}
