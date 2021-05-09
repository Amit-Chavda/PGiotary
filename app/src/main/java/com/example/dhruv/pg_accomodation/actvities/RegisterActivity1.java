package com.example.dhruv.pg_accomodation.actvities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dhruv.pg_accomodation.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import static com.example.dhruv.pg_accomodation.helper_classes.ValidationUtility.isValidEmail;
import static com.example.dhruv.pg_accomodation.helper_classes.ValidationUtility.isValidPassword;

public class RegisterActivity1 extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnNext;
    private TextInputEditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        //edit texts
        emailEditText = findViewById(R.id.email_edittext_reg1);
        passwordEditText = findViewById(R.id.password_edittext_reg1);

        //buttons
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnNext = (MaterialButton) findViewById(R.id.btn_next);

        //go back btn
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity1.this, WelcomeActivity.class));
            }
        });


        // go next btn
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (isValidEmail(email) && isValidPassword(password)) {
                    Intent intent = new Intent(RegisterActivity1.this, RegisterActivity2.class);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    startActivity(intent);
                } else {
                    emailEditText.setError("Invalid email id");
                    passwordEditText.setError("Invalid password(Must be at least 8 character)!");
                }
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        emailEditText.setError(null);
        passwordEditText.setError(null);
    }
}
