package com.example.dhruv.pg_accomodation.actvities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dhruv.pg_accomodation.R;

public class CallActivity extends AppCompatActivity {

    private String callerID;
    private  String recipientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        if (ContextCompat.checkSelfPermission(CallActivity.this,
                android.Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission
                (CallActivity.this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(CallActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},
                    1);
        }

        callerID=getIntent().getStringExtra("callerID");
        recipientID=getIntent().getStringExtra("recipientID");

        Toast.makeText(CallActivity.this, callerID+" ca", Toast.LENGTH_SHORT).show();
        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CallActivity2.class);
                intent.putExtra("callerID", callerID);
                intent.putExtra("recipientID", recipientID);
                startActivity(intent);
            }
        });
    }
}