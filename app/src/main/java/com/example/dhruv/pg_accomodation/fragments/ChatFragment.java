package com.example.dhruv.pg_accomodation.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.models.UserListModel;
import com.example.dhruv.pg_accomodation.adapters.ChatUserListAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ChatFragment extends Fragment {
    RecyclerView users_recycleview;
    private ChatUserListAdapter chatUserListAdapter;
    String currentFirebaseUser;

    public ChatFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        users_recycleview = view.findViewById(R.id.frchatrecycleview);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try {

            users_recycleview.setLayoutManager(new LinearLayoutManager(getContext()));

            FirebaseRecyclerOptions<UserListModel> options = new FirebaseRecyclerOptions.Builder<UserListModel>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("user_chatswith")
                            .child(currentFirebaseUser).child("chatwith"), UserListModel.class).build();
            if(options!=null){
                chatUserListAdapter = new ChatUserListAdapter(options, getContext());
                users_recycleview.setAdapter(chatUserListAdapter);
            }else{
                Toast.makeText(getContext(), "Data not found", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }



        return view;
    }//oncreate view

    @Override
    public void onStart() {
        super.onStart();
        chatUserListAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatUserListAdapter.stopListening();
    }

}//class