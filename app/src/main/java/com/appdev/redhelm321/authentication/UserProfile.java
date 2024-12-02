package com.appdev.redhelm321.authentication;

import android.content.Context;
import android.widget.ImageView;

import com.appdev.redhelm321.R;
import com.bumptech.glide.Glide;

public class UserProfile {
    String userImgLink;
    String name;
    int age;
    String birthDate;
    String address;
    String phoneNumber;
    String bloodType;

    public UserProfile(String userImgLink, String name, int age, String birthDate, String address, String phoneNumber, String bloodType) {
        this.userImgLink = userImgLink;
        this.name = name;
        this.age = age;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.bloodType = bloodType;
    }

    // Method to set image to an ImageView using Glide
    public void setImageToImageView(Context context, ImageView imageView) {
        Glide.with(context)
                .load(userImgLink) // URL of the image
//                .placeholder(R.drawable.placeholder_image) // Optional placeholder image
//                .error(R.drawable.error_image) // Optional error image
                .into(imageView); // Target ImageView
    }
}
