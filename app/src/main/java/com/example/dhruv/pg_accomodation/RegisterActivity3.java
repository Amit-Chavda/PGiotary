package com.example.dhruv.pg_accomodation;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.dhruv.pg_accomodation.BitMapUtility.BitMapToString;
import static com.example.dhruv.pg_accomodation.BitMapUtility.StringToBitMap;
import static com.example.dhruv.pg_accomodation.ValidationUtility.isValidUsername;

public class RegisterActivity3 extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnSignup;
    private CircleImageView btnImage;
    private TextInputEditText usernameEdittext;
    private ProgressDialog progressDialog;
    private TextView changeProfilePicTV;

    //firebase code
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    //User data model
    private UserModel user;
    private Uri imageUri;
    private String imageUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        progressDialog = new ProgressDialog(RegisterActivity3.this);


        //firbase instance initialization
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("user");
        user = new UserModel();

        btnBack = findViewById(R.id.btn_back);
        btnSignup = findViewById(R.id.btn_signup);
        changeProfilePicTV = findViewById(R.id.profile_pic_textview);

        btnImage = findViewById(R.id.profile_pic);
        usernameEdittext = findViewById(R.id.username_edittext);

        // go back btn
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity3.this, RegisterActivity2.class));
            }
        });

        //select profile picture btn
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        //sign up btn
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processSignup();
            }
        });
    }

    public boolean isInternetConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    private void processSignup() {
        if (isInternetConnected()) {
            String username = usernameEdittext.getText().toString();
            if (isValidUsername(username)) {
                confirmSignup();
            } else {
                usernameEdittext.setError("Invalid username!");
            }
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity3.this);
            builder1.setTitle("Network Error");
            builder1.setMessage("Please check your network connection and try again!");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private void showConfirmationMessage() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity3.this);
        builder1.setMessage("Please verify email and login with your credentials!");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(RegisterActivity3.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void uploadUserProfile() {

        progressDialog.setMessage("Processing...");
        progressDialog.show();
        prepareUserProfile();
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(RegisterActivity3.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        user.setUserId(userId);
                        //upload profile image
                        uploadPicture();
                        databaseReference.child(user.getUserId()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //send email verification
                                progressDialog.setMessage("Sending verification mail...");
                                currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        showConfirmationMessage();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity3.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity3.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity3.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void confirmSignup() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity3.this);
        builder1.setTitle("Confirmation");
        builder1.setMessage("Email verification is necessary to use our services. To confirm please click \"OK\" and check your inbox!");
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        uploadUserProfile();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        startActivity(new Intent(RegisterActivity3.this, WelcomeActivity.class));
                        finish();
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void prepareUserProfile() {
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String mobile = getIntent().getStringExtra("mobile");
        String city = getIntent().getStringExtra("city");
        String username = usernameEdittext.getText().toString();
        user.setEmail(email);
        user.setPassword(password);
        user.setMobileNumber(mobile);
        user.setCity(city);
        if(imageUriString==null){
            //default user profile link
            imageUriString="https://firebasestorage.googleapis.com/v0/b/pghunter-c0bcb.appspot.com/o/user-profiles%2Fdefault-user-profile-pic%2Fdeafult-user-profile-picture.png?alt=media&token=fed865fc-fab3-4648-ae69-899788475774";
        }
        user.setProfileImage(imageUriString);
        user.setUsername(username);
    }

    private void setLocalVaribale(String string) {
        imageUriString = string;
    }

    private void uploadPicture() {
        if(imageUri==null){
            return;
        }
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        //creating path and file name to store image
        StorageReference postImageRef = storageReference.child("user-profiles").child(FirebaseAuth.getInstance().getUid()).child(System.currentTimeMillis()
                + "." + mime.getExtensionFromMimeType(contentResolver.getType(imageUri)));
        postImageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                setLocalVaribale(uri.toString());
                                prepareUserProfile();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity3.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            changeProfilePicTV.setVisibility(View.GONE);
            Glide.with(RegisterActivity3.this).load(imageUri).into(btnImage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        usernameEdittext.setError(null);
    }
}