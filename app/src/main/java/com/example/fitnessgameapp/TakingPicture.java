package com.example.fitnessgameapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class TakingPicture extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    Button takePictureBtn;
    Button goBackBtn;
    ImageView imageView;
    DatabaseReference databaseReference;                                    //Setting up variables and references
    Model model;
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taking_picture);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String GoogleID = account.getId();
        model = new Model();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(GoogleID).child("ImageData");

        imageView = findViewById(R.id.image_view);
        takePictureBtn = findViewById(R.id.capture_image_btn);
        goBackBtn = findViewById(R.id.goBackButton);

        goBackBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(TakingPicture.this, MainActivity.class));

            }

        });

        takePictureBtn.setOnClickListener(new View.OnClickListener() {@Override

        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {              //Checking if the sdk version is good enough

                if (checkSelfPermission(Manifest.permission.CAMERA) ==

                        PackageManager.PERMISSION_DENIED ||                                 //Asking for permission from the user to use the camera

                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==

                                PackageManager.PERMISSION_DENIED) {
                    String[] permission = {
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };

                    requestPermissions(permission, PERMISSION_CODE);

                }
                else {
                    //permission already granted
                    openCamera();
                }
            }

            else {

                openCamera();
            }
        }
        });
    }

    private void openCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);          //Creating my intent
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // method called when image was captured from camera
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CAPTURE_CODE) {

            // set image captured to ImageView
            imageView.setImageURI(image_uri);

            model.setImage(image_uri.toString());

            model.setTitle("Taken at Level: " + Integer.toString(MainActivity.Level));
            databaseReference.push().setValue(model);

        }

    }
}