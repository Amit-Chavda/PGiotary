package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

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
    private Post post;
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
private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpost);
        post=new Post();

        firebaseStorage =FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

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
                uploadPicture();
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
            riversRef.putFile(imageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            String address = postAdddressEditText.getText().toString();
                            String description = postDescriptionEditText.getText().toString();
                            intent = new Intent(UploadPostActivity1.this, UploadPostActivity2.class);
                            Toast.makeText(UploadPostActivity1.this, taskSnapshot.getUploadSessionUri()+"", Toast.LENGTH_SHORT).show();
                            intent.putExtra("imageUri", taskSnapshot.getUploadSessionUri().toString());
                            intent.putExtra("address", address);
                            intent.putExtra("description", description);
                            post.setPostImage(taskSnapshot.getUploadSessionUri().toString());
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pd.dismiss();
                            Toast.makeText(UploadPostActivity1.this, "Error while file uploading " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }//end of upload function
}