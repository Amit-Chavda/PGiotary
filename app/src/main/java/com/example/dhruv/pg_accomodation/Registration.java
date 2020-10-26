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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
    EditText emailid,passwordet,fname,conformpassword;
    Button signup;
    TextView tvsignin;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase rootnode;
    DatabaseReference userreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firebaseAuth = FirebaseAuth.getInstance();
        emailid = findViewById(R.id.emailEdittext);
        passwordet = findViewById(R.id.passwordedittext);
        signup = findViewById(R.id.signupbutton);
        tvsignin = findViewById(R.id.oldusertext);
        conformpassword = findViewById(R.id.cpasswordedittext);
        fname = findViewById(R.id.fnameet);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailid.getText().toString();
                String password = passwordet.getText().toString();
                final String name = fname.getText().toString();

                //authentication process
                Toast.makeText(Registration.this, ""+password.length(), Toast.LENGTH_SHORT).show();
                if(email.isEmpty()){
                    emailid.setError("Enter Email id");
                    emailid.requestFocus();
                }

                else if(password.isEmpty() || password.length() < 6 ){

                    passwordet.setError("Enter Password with at least 6 character");
                    passwordet.requestFocus();
                }else if(!(email.isEmpty() && password.isEmpty())){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                updateUI(user);
                                startActivity(new Intent(Registration.this,Home.class));
                                Toast.makeText(Registration.this, "Sign up succesful", Toast.LENGTH_SHORT).show();

                                //strore in realtime database of user data
                                String id = user.getUid();
                                User userclass = new User(name,email);
                                rootnode = FirebaseDatabase.getInstance();
                                userreference = rootnode.getReference().child("user");
                                userreference.child(id).setValue(userclass);

                            }else{
                                Toast.makeText(Registration.this, "Sign UP failed", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
                }else{
                    Toast.makeText(Registration.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                }






            }//onclick
        });//onclicsignup

        tvsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this,LoginPage.class));
            }
        });//onclicktv

    }//on create

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser user){
        /*GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
        }*/
    }
}//class