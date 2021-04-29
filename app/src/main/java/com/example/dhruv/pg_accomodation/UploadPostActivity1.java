package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.example.dhruv.pg_accomodation.map_oprations.LocationProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class UploadPostActivity1 extends AppCompatActivity {

    private TextInputEditText postAdddressEditText;
    private TextInputEditText postDescriptionEditText;
    private ImageView postImageView;

    private TextInputLayout addressInputLayout;
    private FrameLayout frameLayout;
    private TextView uploadPictureTextView;

    private ImageView btnBack;
    private MaterialButton btnNext;
    private Uri imageUri;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    //new code
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private LocationTracker locationTracker;

    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpost);


//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        postImageView = findViewById(R.id.post_image_uploadpost1);
        postAdddressEditText = findViewById(R.id.post_address_edittext);
        postDescriptionEditText = findViewById(R.id.post_description_edittext);

        btnBack = findViewById(R.id.btn_back_uploadpost1);
        btnNext = findViewById(R.id.btn_next_uploadpost1);

        addressInputLayout = findViewById(R.id.address_edittext_uploadpost1);
        frameLayout = findViewById(R.id.post_framelayout);
        uploadPictureTextView = findViewById(R.id.post_image_textview_uploadpost1);

        //go back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadPostActivity1.this, Home.class));
            }
        });

        addressInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    locationTracker = new LocationTracker(UploadPostActivity1.this);
                    if (locationTracker.canGetLocation()) {
                        mLatitude = locationTracker.getLatitude();
                        mLongitude = locationTracker.getLongitude();
                        Geocoder geocoder = new Geocoder(UploadPostActivity1.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(locationTracker.getLatitude(), locationTracker.getLongitude(), 1);
                        String address = addresses.get(0).getAddressLine(0);
                        postAdddressEditText.setText(address);
                    } else {
                        locationTracker.showSettingsAlert();
                    }
                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        //upload image
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(UploadPostActivity1.this, UploadPostActivity2.class));
                uploadPicture();
            }
        });

        //open dialog to select the image
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });


        //new code
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }

    //check needed permisi=sion allowed or not
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for this service. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            break;
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(UploadPostActivity1.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationTracker.stopListener();
    }

    //getting image from local storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadPictureTextView.setVisibility(View.GONE);
            Glide.with(UploadPostActivity1.this).load(imageUri).into(postImageView);
        }
    }

    //process of uploading post
    private void uploadPicture() {

        try {
            //display progrss bar
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Uploading Image...");
            pd.show();
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();

            //creating path and file name to store image
            StorageReference postImageRef = storageReference.child("post-images").child(FirebaseAuth.getInstance().getUid()).child(System.currentTimeMillis()
                    + "." + mime.getExtensionFromMimeType(contentResolver.getType(imageUri)));

            //storing image
            postImageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //passing data to next activity by intent
                                    Intent intent = new Intent(UploadPostActivity1.this, UploadPostActivity2.class);
                                    String address = postAdddressEditText.getText().toString();
                                    String description = postDescriptionEditText.getText().toString();
                                    intent.putExtra("latitude", mLatitude);
                                    intent.putExtra("longitude", mLongitude);
                                    intent.putExtra("imageUri", uri.toString());
                                    intent.putExtra("address", address);
                                    intent.putExtra("description", description);
                                    startActivity(intent);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pd.dismiss();
                            Toast.makeText(UploadPostActivity1.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}