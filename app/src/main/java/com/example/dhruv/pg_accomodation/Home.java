package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dhruv.pg_accomodation.actvities.UploadPostActivity1;
import com.example.dhruv.pg_accomodation.actvities.WelcomeActivity;
import com.example.dhruv.pg_accomodation.fragments.ChatFragment;
import com.example.dhruv.pg_accomodation.fragments.HomeFragment;
import com.example.dhruv.pg_accomodation.fragments.ProfileFragment;
import com.example.dhruv.pg_accomodation.fragments.SearchFragment;
import com.example.dhruv.pg_accomodation.helper_classes.PrefManager;
import com.example.dhruv.pg_accomodation.helper_classes.ValidationUtility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {

    private BottomNavigationView navigationView;
    private FirebaseAuth.AuthStateListener authStateListener;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navigationView = findViewById(R.id.bottom_navigation);

        prefManager = new PrefManager(getApplicationContext());
        if (prefManager.getCallerID() == null || prefManager.getCallerID().isEmpty()) {
            prefManager.setCallerID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        if (!ValidationUtility.isInternetAvailable(this)) {
            Toast.makeText(this, "Your are offline!", Toast.LENGTH_SHORT).show();
        }

        //navigationbar selection
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.home_item) {
                    HomeFragment fragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, fragment);
                    fragmentTransaction.commit();
                }
                if (id == R.id.search_item) {
                    SearchFragment searchFragment = new SearchFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, searchFragment);
                    fragmentTransaction.commit();
                }
                if (id == R.id.upload_item) {
                    navigationView.setSelectedItemId(R.id.home_item);
                    startActivity(new Intent(Home.this, UploadPostActivity1.class));

                }
                if (id == R.id.chat_item) {
                    ChatFragment fragment = new ChatFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, fragment);
                    fragmentTransaction.commit();
                }

                if (id == R.id.profile_item) {
                    ProfileFragment fragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, fragment);
                    fragmentTransaction.commit();
                }
                return true;
            }
        });
        //difault selection
        navigationView.setSelectedItemId(R.id.home_item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {

            switch (item.getItemId()) {
                case R.id.signoutbutton: {
                    prefManager.setIsLoggedIn(false);
                    startActivity(new Intent(Home.this, WelcomeActivity.class));
                    finish();

                }
            }


        } catch (Exception e) {
            Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}