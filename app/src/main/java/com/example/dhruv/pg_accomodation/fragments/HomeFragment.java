package com.example.dhruv.pg_accomodation.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dhruv.pg_accomodation.models.Post;
import com.example.dhruv.pg_accomodation.adapters.PostAdapter;
import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.models.UserModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private String city;
    private String userId;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //hide action bar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        //getInfoOfCurrentUser();
        recyclerView = view.findViewById(R.id.city_post_recycleview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(FirebaseDatabase.getInstance().getReference().child("Posts"), Post.class).build();
        if (options != null) {
            postAdapter = new PostAdapter(options, getContext());
            recyclerView.setAdapter(postAdapter);
        }
        return view;
    }//end of oncreateview

    private void getInfoOfCurrentUser() {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    setLocalVariable(user.getCity(),user.getUserId());
                   // Toast.makeText(getContext(), user.getCity()+user.getUserId()+"", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), error.getMessage() + "", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocalVariable(String city, String userId) {
        this.city = city;
        this.userId=userId;
    }

    @Override
    public void onStart() {
        super.onStart();
        postAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        postAdapter.stopListening();
    }
}//end of class