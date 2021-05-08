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

import com.example.dhruv.pg_accomodation.adapters.PostAdapter2;
import com.example.dhruv.pg_accomodation.adapters.SearchPostAdapter;
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

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private String city;
    private String userId;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //hide action bar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        //init
        recyclerView = view.findViewById(R.id.city_post_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getCurrentCityAndUserId();
        setupInitialFeed();
        return view;
    }//end of oncreateview

    private void getCurrentCityAndUserId() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                setLocalVariable(user.getCity(), user.getUserId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage() + "", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setLocalVariable(String city, String userId) {
        this.city = city;
        this.userId = userId;
    }


    private void setupInitialFeed() {
        //get all data of all posts
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> iterable = snapshot.getChildren();
                ArrayList<Post> postArray = new ArrayList<>();
                for (DataSnapshot d : iterable) {
                    Post post = d.getValue(Post.class);
                    postArray.add(post);
                }
                filterPosts(postArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterPosts(ArrayList<Post> postArrayList) {
        ArrayList<Post> filteredPosts = new ArrayList<>();

        for (Post post : postArrayList) {
            if (post.getPostCity().equals(city) && post.getPostOwner().equals(userId)) {
                filteredPosts.add(post);
            }
        }

        for (Post post : postArrayList) {
            if (!post.getPostOwner().equals(userId)) {
                filteredPosts.add(post);
            }
        }
        PostAdapter2 postRecyclerAdapter = new PostAdapter2(getContext(), filteredPosts);
        recyclerView.setAdapter(postRecyclerAdapter);
    }

}//end of class