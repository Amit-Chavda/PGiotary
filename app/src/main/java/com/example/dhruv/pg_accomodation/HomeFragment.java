package com.example.dhruv.pg_accomodation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dhruv.pg_accomodation.map_oprations.CountDistance;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class HomeFragment extends Fragment {

    public RecyclerView recyclerView;
    public PostAdapter postAdapter;
    //private List<Post> postlist;
    CountDistance countDistance;


    public HomeFragment(){

    }

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_home, container, false);
            countDistance = new CountDistance();
            countDistance.getDistance(getContext());

            recyclerView = view.findViewById(R.id.recycleview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                       .setQuery(FirebaseDatabase.getInstance().getReference().child("Posts"), Post.class).build();
            postAdapter = new PostAdapter(options,getContext());
            recyclerView.setAdapter(postAdapter);


//            postlist = new ArrayList<>();
//            readPosts();

            return view;
    }//end of oncreateview

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

//    private void readPosts(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postlist.clear();
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Post post = snapshot.getValue(Post.class);
//                    postlist.add(post);
//                }
//                postAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}//end of class