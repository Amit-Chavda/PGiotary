package com.example.dhruv.pg_accomodation.actvities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.helper_classes.ValidationUtility;
import com.example.dhruv.pg_accomodation.models.UserModel;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.dhruv.pg_accomodation.helper_classes.ValidationUtility.isValidUsername;

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
    private static UserModel user;
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

    private void processSignup() {

        if (ValidationUtility.isInternetAvailable(this)) {
            String username = usernameEdittext.getText().toString();
            if (isValidUsername(username)) {
                confirmSignup();
            } else {
                usernameEdittext.setError("Invalid username!");
            }
        } else {
            Toast.makeText(this, "You are offline!", Toast.LENGTH_LONG).show();
        }
    }

    private void showConfirmationMessage() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity3.this);
        builder1.setMessage("Please verify email and login with your credentials!");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                (dialog, id) -> {
                    startActivity(new Intent(RegisterActivity3.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                    dialog.cancel();
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
                        final String userId = currentUser.getUid();
                        user.setUserId(userId);

                        //default profile image
                        if (imageUri == null) {

                            imageUriString = "https://firebasestorage.googleapis.com/v0/b/pghunter-c0bcb.appspot.com/o/user-profiles%2Fdefault-user-profile-pic%2Fdeafult-user-profile-picture.png?alt=media&token=fed865fc-fab3-4648-ae69-899788475774";
                            user.setProfileImage(imageUriString);

                            databaseReference.child(user.getUserId()).setValue(user).addOnSuccessListener(aVoid -> {
                                //send email verification
                                progressDialog.setMessage("Sending verification mail...");
                                currentUser.sendEmailVerification().addOnSuccessListener(aVoid12 -> {
                                    progressDialog.dismiss();
                                    showConfirmationMessage();
                                }).addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity3.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }).addOnFailureListener(e -> Toast.makeText(RegisterActivity3.this, e.getMessage(), Toast.LENGTH_SHORT).show());

                        } else {

                            ContentResolver contentResolver = getContentResolver();
                            MimeTypeMap mime = MimeTypeMap.getSingleton();

                            //creating path and file name to store image
                            StorageReference postImageRef = storageReference.child("user-profiles").child(userId).child(System.currentTimeMillis()
                                    + "." + mime.getExtensionFromMimeType(contentResolver.getType(imageUri)));
                            postImageRef.putFile(imageUri)
                                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {

                                        RegisterActivity3.user.setProfileImage(uri.toString());

                                        databaseReference.child(user.getUserId()).setValue(user).addOnSuccessListener(aVoid -> {
                                            //send email verification
                                            progressDialog.setMessage("Sending verification mail...");
                                            currentUser.sendEmailVerification().addOnSuccessListener(aVoid1 -> {
                                                progressDialog.dismiss();
                                                showConfirmationMessage();
                                            }).addOnFailureListener(e -> {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterActivity3.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                        }).addOnFailureListener(e -> Toast.makeText(RegisterActivity3.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                    }))
                                    .addOnFailureListener(exception -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity3.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
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
        user.setUsername(username);
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