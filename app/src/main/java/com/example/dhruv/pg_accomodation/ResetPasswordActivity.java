package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.dhruv.pg_accomodation.ValidationUtility.isValidEmail;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText resetEditText;
    private MaterialButton btnSendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetEditText = findViewById(R.id.email_edittext_reset);

        btnSendMail = findViewById(R.id.btn_send_mail);
        btnSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = resetEditText.getText().toString();
                if (isValidEmail(email)) {
                    sendResetPasswordResetMail(email);
                    showMsgDialog();
                }
            }
        });

    }


    private void showMsgDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ResetPasswordActivity.this);
        builder1.setMessage("Password reset mail is sent. Please check your mailbox and login!");

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                        finish();
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void sendResetPasswordResetMail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                        } else {
                            // ...
                        }
                    }
                });
    }
}