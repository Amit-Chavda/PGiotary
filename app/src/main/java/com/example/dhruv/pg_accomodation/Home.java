package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    BottomNavigationView navigationView;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.toolbar,menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    try {

        switch (item.getItemId()){
            case R.id.signoutbutton:{

                        firebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Home.this,LoginPage.class));

            }
        }


    }
    catch (Exception e){
        Toast.makeText(this, "Error:"+ e.getMessage(), Toast.LENGTH_SHORT).show();
    }
                return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //initialization

        navigationView =findViewById(R.id.bottom_navigation);

    /*    //signout process

*/

        //navigationbar selection
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id =menuItem.getItemId();
                if(id == R.id.home){
                    HomeFragment fragment =new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, fragment);
                    fragmentTransaction.commit();
                }
                if(id == R.id.Upload){
                    startActivity(new Intent(Home.this, uploadpost.class));
                }
                if(id == R.id.profile){
                    ProfileFragment fragment =new ProfileFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, fragment);
                    fragmentTransaction.commit();
                }
                return true;
            }
        });
        //difault selection
        navigationView.setSelectedItemId(R.id.home);

    }


}