package com.example.dhruv.pg_accomodation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import static com.example.dhruv.pg_accomodation.ValidationUtility.isValidMobile;
import static com.example.dhruv.pg_accomodation.ValidationUtility.isValidateCity;

public class RegisterActivity2 extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnNext;
    private TextInputEditText phone;
    private AutoCompleteTextView cityAutoTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        cityAutoTV= findViewById(R.id.cityTextView);

        //initialize city dropdown list
        initCityAdapterUI();


        btnBack = findViewById(R.id.btn_back);
        btnNext = findViewById(R.id.btn_next);

        phone = findViewById(R.id.mobile_edittext);

        //go back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity2.this, RegisterActivity1.class));
            }
        });

        // go next
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = phone.getText().toString();

                if (isValidMobile(mobile)) {
                    String city= cityAutoTV.getText().toString();
                    if(isValidateCity(city)){
                        String email = getIntent().getStringExtra("email");
                        String password = getIntent().getStringExtra("password");
                        Intent intent = new Intent(RegisterActivity2.this, RegisterActivity3.class);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("mobile", mobile);
                        intent.putExtra("city", city);
                        startActivity(intent);
                    }else {
                        cityAutoTV.setError("Invalid City Selected!");
                    }
                } else {
                    phone.setError("Invalid Mobile Number!");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        phone.setError(null);
        cityAutoTV.setError(null);
    }

    private void initCityAdapterUI() {
        ArrayList<String> cities = new ArrayList<>();
        cities.add("Ahmedabad");
        cities.add("Gandhinagar");
        cities.add("Surat");
        cities.add("Vadodara");
        cities.add("Mehsana");
        cities.add("Modasa");
        cities.add("Junagadh");
        cities.add("Mumbai");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity2.this, android.R.layout.simple_spinner_dropdown_item, cities);
        cityAutoTV.setAdapter(adapter);
    }

}