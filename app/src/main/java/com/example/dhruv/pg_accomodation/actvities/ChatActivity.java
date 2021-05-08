package com.example.dhruv.pg_accomodation.actvities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.fragments.ChatFragment;
import com.example.dhruv.pg_accomodation.models.UserModel;
import com.example.dhruv.pg_accomodation.models.ChatModel;
import com.example.dhruv.pg_accomodation.adapters.MessageViewAdapter;
import com.example.dhruv.pg_accomodation.models.UserListModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {
    private MaterialTextView username;
    private CircleImageView profilepic;
    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton send;
    private String firstuser;
    private String seconduser;
    private String chatid;
    private MessageViewAdapter messageViewAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference user_chatsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //hide action bar
        this.getSupportActionBar().hide();
        //initialization
        username = findViewById(R.id.chat_username);
        profilepic = findViewById(R.id.chat_profilepic);
        recyclerView = findViewById(R.id.chat_recyclerview);
        editText = findViewById(R.id.chat_edittext);
        send = findViewById(R.id.chat_sendbtn);
        firebaseDatabase = FirebaseDatabase.getInstance();

        //get chat,sender and reciever id from parent activity
        firstuser = getIntent().getStringExtra("firstuser");
        seconduser = getIntent().getStringExtra("seconduser");
        chatid = getIntent().getStringExtra("chatid");

        //set profile and username on action bar
        setupActionbar();
        //display chat messages
        displayMessages();
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processMessage();
            }
        });

    }//oncreate

    private void processMessage() {
        try {
            String msgtext = editText.getText().toString();
            if (!chatid.equals("1")) {

                user_chatsReference = firebaseDatabase.getReference().child("user_chats");
                ChatModel message = new ChatModel();
                message.setMessage(msgtext);
                message.setSender(firstuser + "");
                message.setReceiver(seconduser + "");
                user_chatsReference.child(chatid).push().setValue(message);
                editText.setText("");
            }
        } catch (Exception e) {
            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayMessages() {
        if (!chatid.equals("1")) {
            ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
            FirebaseRecyclerOptions<ChatModel> options = new FirebaseRecyclerOptions.Builder<ChatModel>().
                    setQuery(FirebaseDatabase.getInstance().getReference().child("user_chats").child(chatid), ChatModel.class).build();
            messageViewAdapter = new MessageViewAdapter(options, ChatActivity.this);
            recyclerView.setAdapter(messageViewAdapter);
            progressDialog.dismiss();
        }
    }

    private void setupActionbar() {
        // this code is to set username and email id in profile
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(seconduser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                Glide.with(ChatActivity.this).load(user.getProfileImage()).into(profilepic);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!chatid.equals("1")) {
            messageViewAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!chatid.equals("1")) {
            messageViewAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChatActivity.this, ChatFragment.class));
        finish();
    }
}