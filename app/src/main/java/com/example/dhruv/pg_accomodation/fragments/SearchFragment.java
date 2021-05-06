package com.example.dhruv.pg_accomodation.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.adapters.SearchPostAdapter;
import com.example.dhruv.pg_accomodation.models.Post;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import static androidx.recyclerview.widget.RecyclerView.*;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<Post> postArrayList;
    private TextInputEditText autoCompleteTextView;

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.search_recyclerview);
        autoCompleteTextView = view.findViewById(R.id.search_edittext);


        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable str) {

                if(autoCompleteTextView.getText().toString().equals("")){
                    setupInitialFeed();
                }
                filter(str.toString().toLowerCase());
            }
        });


        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layout);
        postArrayList = new ArrayList<>();

        setupInitialFeed();
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
                Adapter postRecyclerAdapter = new SearchPostAdapter(getContext(), postArray);
                recyclerView.setAdapter(postRecyclerAdapter);
                setPostList(postArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPostList(ArrayList<Post> postArray) {
        this.postArrayList = postArray;
    }

    private void filter(String rawText) {
        ArrayList<Post> filteredPosts = new ArrayList<>();

        for (Post post : postArrayList) {
            if (post.getPostType().toLowerCase().contains(rawText)) {
                filteredPosts.add(post);
            }
        }

        for (Post post : postArrayList) {
            if (post.getPostRent().toLowerCase().contains(rawText)) {
                filteredPosts.add(post);
            }
        }

        for (Post post : postArrayList) {
            if (post.getPostCity().toLowerCase().contains(rawText) || post.getPostAddress().toLowerCase().trim().contains(rawText)) {
                filteredPosts.add(post);
            }
        }

        Adapter postRecyclerAdapter = new SearchPostAdapter(getContext(), filteredPosts);
        recyclerView.setAdapter(postRecyclerAdapter);

    }
}