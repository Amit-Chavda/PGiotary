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

public class RegisterActivity2 extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnNext;
    private TextInputEditText phone;
    private AutoCompleteTextView cityrAutoTV;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        //initilize city dropdown list
        cityrAutoTV= findViewById(R.id.cityTextView);
        initCityAdapterUI();


        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnNext = (MaterialButton) findViewById(R.id.btn_next);

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
                    String email = getIntent().getStringExtra("email");
                    String password = getIntent().getStringExtra("password");

                    Intent intent = new Intent(RegisterActivity2.this, RegisterActivity3.class);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("mobile", mobile);
                    intent.putExtra("city", city);
                    startActivity(intent);
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
        cityrAutoTV.setError(null);
    }

    private void initCityAdapterUI() {
        //UI reference of textView


        // create list of customer
        final ArrayList<String> cityList = getCityList();

        //Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity2.this, android.R.layout.simple_spinner_dropdown_item, cityList);
        //Set adapter
        cityrAutoTV.setAdapter(adapter);
        cityrAutoTV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city=cityList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private ArrayList<String> getCityList() {
        ArrayList<String> cities = new ArrayList<>();
        cities.add("Ahmedabad");
        cities.add("Surat");
        cities.add("Mehsana");
        cities.add("Modasa");
        cities.add("New york");
        cities.add("Ambaji");
        cities.add("Mumbai");
        cities.add("Junagadh");
        return cities;
    }

}