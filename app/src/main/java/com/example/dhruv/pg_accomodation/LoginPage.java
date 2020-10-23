package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    EditText emailid,passwordet;
    Button signin;
    TextView tvsignup;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        emailid = findViewById(R.id.emailEdittext);
        passwordet = findViewById(R.id.passwordedittext);
        signin = findViewById(R.id.signupbutton);
        tvsignup = findViewById(R.id.oldusertext);
        authStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    Toast.makeText(LoginPage.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginPage.this,Home.class));
                }else{
                    Toast.makeText(LoginPage.this, "sign out", Toast.LENGTH_SHORT).show();
                }
            }
        };




        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String email = emailid.getText().toString();
                String password = passwordet.getText().toString();

                if(email.isEmpty()){
                    emailid.setError("Enter Email id");
                    emailid.requestFocus();
                }
                else if(password.isEmpty()){
                    passwordet.setError("Enter Password");
                    passwordet.requestFocus();
                }else if(!(email.isEmpty() && password.isEmpty())){
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!(task.isSuccessful())){
                                Toast.makeText(LoginPage.this, "LogIn failed", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(LoginPage.this, "Login successful", Toast.LENGTH_SHORT).show();
                                firebaseUser = firebaseAuth.getCurrentUser();
                                startActivity(new Intent(LoginPage.this,Home.class));
                                finish();

                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginPage.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                }

            }//onclick
        });

        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginPage.this,Registration.class));
            }
        });
    }//oncreate


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener((authStateListener));
        firebaseUser = firebaseAuth.getCurrentUser();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
