package com.example.dhruv.pg_accomodation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.dhruv.pg_accomodation.BitMapUtility.BitMapToString;
import static com.example.dhruv.pg_accomodation.BitMapUtility.StringToBitMap;
import static com.example.dhruv.pg_accomodation.ValidationUtility.isValidUsername;

public class RegisterActivity3 extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnSignup;
    private ImageButton btnImage;
    private TextInputEditText usernameEdittext;
    private ProgressDialog progressDialog;

    //firebase code
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //User data model
    UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        progressDialog = new ProgressDialog(RegisterActivity3.this);


        //firbase instance initialization
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("user");
        user = new UserModel();

        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnSignup = (MaterialButton) findViewById(R.id.btn_signup);

        btnImage = findViewById(R.id.profile_pic);
        usernameEdittext = findViewById(R.id.username_edittext);

        // go back btn
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity3.this, RegisterActivity2.class));
            }
        });

        //select profile btn
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
                String username = usernameEdittext.getText().toString();

                if (isValidUsername(username)) {
                    String email = getIntent().getStringExtra("email");
                    String password = getIntent().getStringExtra("password");
                    String mobile = getIntent().getStringExtra("mobile");
                    String city = getIntent().getStringExtra("city");
                    ;

                    user.setEmail(email);
                    user.setCity(city);
                    user.setMobileNumber(mobile);
                    user.setUsername(username);
                    user.setPassword(password);

                    //show dialog box
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                    saveDatatoDatabase();
                    //showMsgDialog();

                } else {
                    usernameEdittext.setError("Invalid username!");
                }

            }//onclick
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        usernameEdittext.setError(null);
    }


    private void showMsgDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity3.this);
        builder1.setMessage("Verification Email is sent. Please check your mailbox!");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //saveDatatoDatabase(user);
                        //go to login for login
                        startActivity(new Intent(RegisterActivity3.this, LoginActivity.class));
                        finish();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    //get image from local storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                //bitmap to string
                String profileImage = BitMapToString(bitmap);
                //string to bitmap
                bitmap = StringToBitMap(profileImage);
                btnImage.setImageBitmap(bitmap);
                user.setProfileImage(profileImage);
            } catch (IOException e) {
                Toast.makeText(RegisterActivity3.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }


    //save database
    private void saveDatatoDatabase() {

        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(RegisterActivity3.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    currentUser = firebaseAuth.getCurrentUser();

                    String userId = null;
                    if (currentUser != null) {
                        userId = currentUser.getUid();
                        user.setUserId(userId);
                        databaseReference.child(user.getUserId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(RegisterActivity3.this, "Data Saved", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //send email verification
                        currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity3.this, "Mail sent", Toast.LENGTH_SHORT).show();
                                showMsgDialog();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity3.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity3.this, "User not created", Toast.LENGTH_SHORT).show();
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

}