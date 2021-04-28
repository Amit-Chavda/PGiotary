package com.example.dhruv.pg_accomodation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

public class UploadPostActivity1 extends AppCompatActivity {


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
//    private FirebaseStorage firebaseStorage;
//    private StorageReference storageReference;
//    StorageTask uploadtask;
//
//    //class
//    LocationProvider locationProvider;
//    Location userlocation;
//
//    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpost);

        postImageView = findViewById(R.id.post_image_uploadpost1);
        postAdddressEditText = findViewById(R.id.post_address_edittext);
        postDescriptionEditText = findViewById(R.id.post_description_edittext);

        btnBack = findViewById(R.id.btn_back_uploadpost1);
        btnNext = findViewById(R.id.btn_next_uploadpost1);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadPostActivity1.this, Home.class));
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = postAdddressEditText.getText().toString();
                String description = postDescriptionEditText.getText().toString();


                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //postBitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byte[] byteArray = stream.toByteArray();

                Intent intent = new Intent(UploadPostActivity1.this, UploadPostActivity2.class);
                intent.putExtra("imageUri",imageUri);
                intent.putExtra("address", address);
                intent.putExtra("description", description);

                startActivity(intent);
            }
        });

//        himg = findViewById(R.id.houseimg);
//        post = findViewById(R.id.postbtn);
//        rent = findViewById(R.id.rentet);
//        location = findViewById(R.id.locationet);
//        locationbtn = findViewById(R.id.locationbtn);
//        description = findViewById(R.id.descriptionet);
//        title = findViewById(R.id.titleet);
//        spinner_d = findViewById(R.id.spinnerdropdown);
//        firebaseStorage = FirebaseStorage.getInstance();
//        storageReference = firebaseStorage.getReference();
//        locationProvider = new LocationProvider();
//
//
//        //getting location permition
//        userGetLocationPermission();
//
//
//        locationbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //getting current location of user
//                userlocation = locationProvider.getLocation(getApplicationContext());
//                //Double lati = userlocation.getLatitude();
//                try {
//                    if (userlocation != null) {
//                        Geocoder geocoder = new Geocoder(uploadpost.this, Locale.getDefault());
//                        List<Address> addresses = geocoder.getFromLocation(userlocation.getLatitude(), userlocation.getLongitude(), 1);
//                        address = addresses.get(0).getAddressLine(0);
//                        location.setText(address);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Move you mobile and try again", Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
//                }//try catch
//
//            }//onclick
//        });
//
//
//        //house type selection spinner
//        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
//        spinner_d.setAdapter(arrayAdapter);
//
//        //post button to uplaod post
//        post.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadPicture();
//            }
//        });
//
//
//        //open dialog to select the image
        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);

            }
        });
//    }//oncreat
//
//    //getting location permission
//    private void userGetLocationPermission() {
//        if (ContextCompat.checkSelfPermission(uploadpost.this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(uploadpost.this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                ActivityCompat.requestPermissions(uploadpost.this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            } else {
//                ActivityCompat.requestPermissions(uploadpost.this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            }
//        }
//    }
//
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        switch (requestCode) {
//            case 1: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ContextCompat.checkSelfPermission(uploadpost.this,
//                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//        }
//    }
//
//
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
//
//    //process of uploading post
//    private void uploadPicture() {
//
//        try {
//            //display progrss bar
//            final ProgressDialog pd = new ProgressDialog(this);
//            pd.setTitle("Uploading Image...");
//            pd.show();
//            ContentResolver contentResolver = getContentResolver();
//            MimeTypeMap mime = MimeTypeMap.getSingleton();
//
//
//            final StorageReference riversRef = storageReference.child(System.currentTimeMillis()
//                    + "." + mime.getExtensionFromMimeType(contentResolver.getType(imageuri)));
//            uploadtask = riversRef.putFile(imageuri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            pd.dismiss();
//
//                            Toast.makeText(uploadpost.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            pd.dismiss();
//                            Toast.makeText(uploadpost.this, "Error while file uploading " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//            uploadtask.continueWithTask(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    if (!task.isComplete()) {
//                        throw task.getException();
//                    }
//                    return riversRef.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    Uri downloaduri = task.getResult();
//                    myUrl = downloaduri.toString();
//
//
//                    //store the data to realtime database
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
//                    final String randomKey = UUID.randomUUID().toString();
//                    String postid = databaseReference.push().getKey();
//                    String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                    HashMap<String, Object> hashMap = new HashMap<>();
//                    hashMap.put("postid", postid);
//                    hashMap.put("posttitle", title.getText().toString());
//                    hashMap.put("postimage", myUrl);
//                    hashMap.put("postdescription", description.getText().toString());
//                    hashMap.put("postprice", rent.getText().toString());
//                    hashMap.put("postaddress", address);
//                    hashMap.put("lat", userlocation.getLatitude() + "");
//                    hashMap.put("longi", userlocation.getLongitude() + "");
//                    hashMap.put("publisher", currentFirebaseUser);
//
//
//                    databaseReference.child(postid).setValue(hashMap);
//                    startActivity(new Intent(uploadpost.this, Home.class));
//
//                }//oncompletlisner
//            });
//
//
//        } catch (Exception e) {
//            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
//        }
    }//end of upload function


//end of class