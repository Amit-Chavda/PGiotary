package com.example.dhruv.pg_accomodation.map_oprations;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class CountDistance {
    DatabaseReference databaseReference;
    ArrayList<String> keys;
    Location location;


    public void getDistance(final Context context) {
        keys = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    keys.add(key);
                    //Toast.makeText(context, ""+keys, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }//get distance


}
