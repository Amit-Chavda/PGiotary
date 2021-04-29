package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

public class UploadPostActivity2 extends AppCompatActivity {

    private TextInputEditText postRentEditText;
    private TextInputEditText postFacilityEditText;
    private ImageView btnBack;
    private MaterialButton btnUpload;
    private String houseType;
    private ProgressDialog progressDialog;
    private AutoCompleteTextView postTypeAutoTV;
    private AutoCompleteTextView availabilityStatusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post2);
        postTypeAutoTV = findViewById(R.id.postTypeTextView);
        setPGTypeAdapter();

        availabilityStatusTV=findViewById(R.id.availability_edittext);
        setAvailabilityAdapter();

        postRentEditText=findViewById(R.id.post_rent_edittext);
        postFacilityEditText=findViewById(R.id.post_facility_edittext);
        progressDialog = new ProgressDialog(UploadPostActivity2.this);

        btnBack = findViewById(R.id.btn_back_uploadpost2);
        btnUpload = findViewById(R.id.btn_upload);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadPostActivity2.this, UploadPostActivity1.class));
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Processing...");
                progressDialog.show();

                String address = getIntent().getStringExtra("address");
                String description = getIntent().getStringExtra("description");
                double lat =getIntent().getDoubleExtra("latitude",0.0d);
                double logi = getIntent().getDoubleExtra("longitude",0.0d);
                String imageString = getIntent().getStringExtra("imageUri");
                Toast.makeText(UploadPostActivity2.this, lat+"  "+logi, Toast.LENGTH_SHORT).show();

                String rent = postRentEditText.getText().toString();
                String facility = postFacilityEditText.getText().toString();
                String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String type=postTypeAutoTV.getText().toString();
                String status= availabilityStatusTV.getText().toString();

                Post post = new Post();
                post.setPostImage(imageString);
                post.setPostAddress(address);
                post.setPostDescription(description);
                post.setFacility(facility);
                post.setPostRent(rent);
                post.setPostType(type);
                post.setPostRatings("0.0");
                post.setPostOwner(ownerId);
                post.setPostStatus(status);
                post.setLat(lat+"");
                post.setLongi(logi+"");
                saveDatatoDatabase(post);
                progressDialog.dismiss();
            }
        });

    }

    private void setPGTypeAdapter() {

        ArrayList<String> houseTypes = new ArrayList<>();
        houseTypes.add("1-BHK");
        houseTypes.add("2-BHK");
        houseTypes.add("2-BHK Flat");
        houseTypes.add("3-BHK House");
        houseTypes.add("Row house");
        houseTypes.add("Luxurious Bunglow");
        postTypeAutoTV.setAdapter(new ArrayAdapter<>(UploadPostActivity2.this, android.R.layout.simple_spinner_dropdown_item, houseTypes));
    }
    private void setAvailabilityAdapter(){
        ArrayList<String> availabilityList = new ArrayList<>();
        availabilityList.add("Available Now");
        availabilityList.add("Available Soon");
        availabilityList.add("Not Available");
        availabilityStatusTV.setAdapter(new ArrayAdapter<>(UploadPostActivity2.this, android.R.layout.simple_spinner_dropdown_item, availabilityList));
    }


    //save database
    private void saveDatatoDatabase(Post post) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        final String randomKey = UUID.randomUUID().toString();
        String postId = databaseReference.push().getKey();
        post.setPostId(postId);
        databaseReference.child(postId).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    startActivity(new Intent(UploadPostActivity2.this,Home.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadPostActivity2.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

}