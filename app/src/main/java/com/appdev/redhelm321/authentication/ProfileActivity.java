package com.appdev.redhelm321.authentication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appdev.redhelm321.Home;
import com.appdev.redhelm321.R;
import com.appdev.redhelm321.utils.Constants;
import com.appdev.redhelm321.utils.FileUtils;
import com.appdev.redhelm321.utils.FirebaseAuthUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    CardView cv_displayPicContainer;
    ImageView iv_displayPic;
    EditText et_emailInfo;
    EditText et_usernameInfo;
    EditText et_bloodTypeInfo;
    Button btn_updateProfile;


    FirebaseAuth mAuth;
    FirebaseAuthUtils firebaseAuthUtils;
    FirebaseDatabase firebaseDatabase;
    GoogleSignInClient googleSignInClient ;
    DatabaseReference FBDB_profilesRef;

    Uri selectedImageUri;
    String currentImgSrc;

    OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_activiity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeComponent();
        loadUserProfile();
    }

    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();

        FBDB_profilesRef.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                UserProfile userProfile = (UserProfile) task.getResult().getValue(UserProfile.class);

                et_bloodTypeInfo.setText(userProfile.getBloodType());
                et_emailInfo.setText(userProfile.getAddress());
                et_usernameInfo.setText(userProfile.getName());

                Log.d("PROFILE_GET", userProfile.getUserImgLink());

                UserProfile.setImageToImageView(ProfileActivity.this, iv_displayPic, userProfile.getUserImgLink());
            }
        });

    }

    private void InitializeComponent() {

        okHttpClient = new OkHttpClient();

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthUtils = new FirebaseAuthUtils(mAuth);
        firebaseDatabase = FirebaseDatabase.getInstance();
        FBDB_profilesRef = firebaseDatabase.getReference("profiles");

        currentImgSrc = "https://i.imgur.com/95OGDyY.png";

        cv_displayPicContainer = findViewById(R.id.cv_displayPicContainer);
        iv_displayPic = findViewById(R.id.iv_displayPic);
        et_emailInfo = findViewById(R.id.et_emailInfo);
        et_usernameInfo = findViewById(R.id.et_usernameInfo);
        et_bloodTypeInfo = findViewById(R.id.et_bloodTypeInfo);
        btn_updateProfile = findViewById(R.id.btn_updateProfile);
        
        btn_updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_updateProfile_onClick(view);
            }
        });

        cv_displayPicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cv_displayPicContainer_onClick(view);
            }
        });
    }

    private void btn_updateProfile_onClick(View view) {
        uploadSelectedImage();

    }

    private void updateProfileInfo() {
        String userName = et_usernameInfo.getText().toString();
        String bloodType = et_bloodTypeInfo.getText().toString();
        String email = et_emailInfo.getText().toString();

        UserProfile userProfile = new UserProfile.Builder()
                .setName(userName)
                .setAddress(email)
                .setUserImgLink(currentImgSrc)
                .setBloodType(bloodType)
                .build();

        String userId = mAuth.getCurrentUser().getUid();
        String username = mAuth.getCurrentUser().getDisplayName();

        HashMap<String, String> userData = new HashMap<>();
        userData.put("TEST_PROFILE", "MY PROFILE");

        FBDB_profilesRef.child(userId).setValue(userProfile)
                .addOnCompleteListener(detailsTask -> {
                    if (detailsTask.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(this, "User data saved", Toast.LENGTH_SHORT).show());
//                                            Log.i(TAG, "User data saved for: " + user.getEmail());?
                    } else {
//                                            Log.e(TAG, "Error saving user data: " + detailsTask.getException());
                        Exception exception = detailsTask.getException();
                        if (exception != null) {
                            Toast.makeText(this, "Error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                            exception.printStackTrace();
                        } else {
                            Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_LONG).show();
                        }
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Error saving user data", Toast.LENGTH_SHORT).show();
                        });
                    }
                });

    }


    private void uploadSelectedImage() {
        if(selectedImageUri == null) return;

        Toast.makeText(this, "UPLOAD", Toast.LENGTH_SHORT).show();
        String access_token = "a523329c77d76d77467cb40be2832f707654bc38";
        String url = "https://api.imgur.com/3/image";

        File imgFile = null;

        try {
            imgFile = FileUtils.getFileFromUri(this, selectedImageUri);
            Log.d("IMGUR_Response", imgFile.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Create RequestBody for the file
        RequestBody fileBody = RequestBody.create(imgFile, MediaType.parse("image/*")); // Use appropriate MIME type

        // Create MultipartBody
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imgFile.getName(), fileBody) // 'file' is the parameter name expected by the server
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + access_token)
                .post(fileBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Toast.makeText(ProfileActivity.this, "Failed to get", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                try {

                    JSONObject json =  new JSONObject(response.body().string());
                    JSONObject data = new JSONObject(json.getString("data"));
                    String link = data.getString("link");

                    currentImgSrc = link;
                    updateProfileInfo();

                    Log.d("IMGUR_Response", "UPLOADED: " + link);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

//                Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();

            }
        });

    }




    private void cv_displayPicContainer_onClick(View view) {
        // open the file selection menu to get an image
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // This allows you to select any file type
        startActivityForResult(intent, Constants.PICK_FILE_REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle the selected image URI
            selectedImageUri = data.getData();
            iv_displayPic.setImageURI(selectedImageUri);

        }
    }
}