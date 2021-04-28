package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.example.dhruv.pg_accomodation.map_oprations.LocationProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UploadPostActivity1 extends AppCompatActivity {

    Button getlocation;
    private TextInputEditText postAdddressEditText;
    private TextInputEditText postDescriptionEditText;
    private ImageView postImageView;

    private ImageView btnBack;
    private MaterialButton btnNext;
    private String postImageString;
    private Bitmap postBitmapImage;
    private Uri imageUri;
//    ImageView himg;
//    Uri imageuri;
//    TextView post;
//    Button locationbtn;
//    String myUrl = "";
//    String address = "";
//    String names[] = {"Select House Type", "Full House", "1BHK", "2BHK"};
//    EditText rent, description, title, location;
//    Spinner spinner_d;
//    ArrayAdapter<String> arrayAdapter;
//
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageTask uploadtask;

   // @Override
//    private FirebaseStorage firebaseStorage;
//    private StorageReference storageReference;
//    StorageTask uploadtask;
//
//    //class
    LocationProvider locationProvider;
    Location userlocation;
//
//    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpost);

        firebaseStorage =FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        postImageView = findViewById(R.id.post_image_uploadpost1);
        postAdddressEditText = findViewById(R.id.post_address_edittext);
        postDescriptionEditText = findViewById(R.id.post_description_edittext);

        btnBack = findViewById(R.id.btn_back_uploadpost1);
        btnNext = findViewById(R.id.btn_next_uploadpost1);
        getlocation = findViewById(R.id.getlocation);
        locationProvider = new LocationProvider();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadPostActivity1.this, Home.class));
            }
        });

        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    String address;
                    userlocation = locationProvider.getLocation(UploadPostActivity1.this);
                    if (userlocation != null) {
                        Geocoder geocoder = new Geocoder(UploadPostActivity1.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(userlocation.getLatitude(), userlocation.getLongitude(), 1);
                        address = addresses.get(0).getAddressLine(0);
                        postAdddressEditText.setText(address);
                    } else {
                        Toast.makeText(getApplicationContext(), "Move you mobile and try again", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                }//try catch


            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = postAdddressEditText.getText().toString();
                String description = postDescriptionEditText.getText().toString();
                Intent intent = new Intent(UploadPostActivity1.this, UploadPostActivity2.class);
                uploadPicture();
                intent.putExtra("imageUri", postImageString);
                intent.putExtra("address", address);
                intent.putExtra("description", description);
                intent.putExtra("lat", userlocation.getLatitude());
                intent.putExtra("longi", userlocation.getLongitude());

                startActivity(intent);
            }
        });

        //open dialog to select the image
        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);

            }
        });

    }

    //getting image from local storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                imageUri = data.getData();
                postBitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                postImageView.setImageBitmap(postBitmapImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    //process of uploading post
    private void uploadPicture() {

        try {
            //display progrss bar
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Uploading Image...");
            pd.show();
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();


            final StorageReference riversRef = storageReference.child(System.currentTimeMillis()
                    + "." + mime.getExtensionFromMimeType(contentResolver.getType(imageUri)));
            uploadtask = riversRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(UploadPostActivity1.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pd.dismiss();
                            Toast.makeText(UploadPostActivity1.this, "Error while file uploading " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()) {
                        throw task.getException();
                    }
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloaduri = task.getResult();
                    postImageString = downloaduri.toString();

                }//oncompletlisner
            });


        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }//end of upload function
}