package com.appdev.redhelm321;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appdev.redhelm321.chat_room.WIFI_P2P_SharedData;
import com.appdev.redhelm321.chat_room.WiFiDirectBroadcastReceiver;
//import com.appdev.redhelm321.info_features.LoginActivity;
import com.appdev.redhelm321.utils.FirebaseAuthUtils;
import com.appdev.redhelm321.utils.FormValidation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static final int IP_PORT = 1307;

    WifiManager wifiManager;
    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel wifiP2pChannel;
    private DatabaseReference usersRef;
    private SharedPreferences loginPrefs;
    private SharedPreferences.Editor loginPrefsEditor;
    private ActivityResultLauncher<Intent> activityResultLauncher;


    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    private FirebaseAuth mAuth;
    private FirebaseAuthUtils firebaseAuthUtils;
    private FirebaseDatabase firebaseDatabase;
    GoogleSignInClient googleSignInClient ;

    ImageView rhLogo;  // First logo
    ImageView rhLogo2;  // Second logo to animate
    LinearLayout loginLayout;  // Login layout
    ImageButton btn_signIn;  // Sign In button
    EditText et_email;
    EditText et_password;


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
        ImageButton btn_signIn = findViewById(R.id.btn_signIn);  // Sign In button

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

        rhLogo = findViewById(R.id.rhLogo);  // First logo
        rhLogo2 = findViewById(R.id.rhLogo2);  // Second logo to animate
        loginLayout = findViewById(R.id.login_layout);  // Login layout
        btn_signIn = findViewById(R.id.btn_signIn);  // Sign In button
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthUtils = new FirebaseAuthUtils(mAuth);
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");
        loginPrefs = getSharedPreferences("LogInPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPrefs.edit();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();


                        if(resultCode == RESULT_OK) {
                            handleSigningInWithGoogle(data);
                        }
                        else {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Google Sign In Cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();
//                            pb_login.setVisibility(View.INVISIBLE);
                        }


                    }
                }
        );


        // Set OnClickListener for Sign In button
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_signIn_onClick();
            }
        });
    }

    private void handleSigningInWithGoogle(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(Exception.class);

            if(account != null) {
//                pb_login.setVisibility(View.INVISIBLE);
//                resetFailedLoginCounter();

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                HashMap<String, String> userData = new HashMap<>();
                userData.put("username", account.getDisplayName());


                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(MainActivity.this, "Successfully signed in with Google!", Toast.LENGTH_SHORT).show();
                        finish();

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            String userId = user.getUid();

                            usersRef.child(userId).setValue(userData)
                                    .addOnCompleteListener(detailsTask -> {
                                        if (detailsTask.isSuccessful()) {
                                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "User data saved", Toast.LENGTH_SHORT).show());
//                                            Log.i(TAG, "User data saved for: " + user.getEmail());?
                                        } else {
//                                            Log.e(TAG, "Error saving user data: " + detailsTask.getException());
                                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error saving user data", Toast.LENGTH_SHORT).show());
                                        }
                                    });
                        } else {
//                            Log.e(TAG, "FirebaseUser is null after successful registration");
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: User data is missing", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            }

        }
        catch (Exception e) {
//            Log.e(TAG, e.toString());
            Toast.makeText(this, "An error happened while getting google account!", Toast.LENGTH_SHORT).show();
        }


//        pb_login.setVisibility(View.INVISIBLE);
    }

    private void handleUserLoggedIn() {

        if (firebaseAuthUtils.isUserLoggedIn()) {

            Toast.makeText(
                            MainActivity.this,
                            FormValidation.WarningMessage.USER_ALREADY_LOGGED_IN_WARNING.getMessage(),
                            Toast.LENGTH_LONG
                    )
                    .show();
            finish();
        }
    }

    private void btn_signIn_onClick() {
        handleUserLoggedIn();

        final String email = et_email.getText().toString().trim();
        final  String password = et_password.getText().toString().trim();

        FormValidation.FormValidationResult loginFormResult = FormValidation.isLoginFormValid(
                email,
                password
        );

        String message = "";

        switch (loginFormResult) {
            case INVALID_EMAIL:
                message = FormValidation.WarningMessage.INVALID_EMAIL_WARNING.getMessage();
                break;
            case INVALID_PASSWORD:
                message = FormValidation.WarningMessage.INVALID_PASSWORD_WARNING.getMessage();
                break;
            case INPUT_NULL:
                message = FormValidation.WarningMessage.INPUT_NULL_WARNING.getMessage();
                break;
        }

        if(!TextUtils.isEmpty(message)) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)
                    .show();
//            pb_login.setVisibility(View.INVISIBLE);
            return;
        }

//        if(isLoginLocked()) {
//            pb_login.setVisibility(View.INVISIBLE);
//            return;
//        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            pb_login.setVisibility(View.INVISIBLE);
//                            resetFailedLoginCounter();
                            Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, Home.class));
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            pb_login.setVisibility(View.INVISIBLE);
//                            handleFailedLoginAttempt();
                        }


                    }
                });
    }
}
