package com.example.dhruv.pg_accomodation.actvities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.R;
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

        ((AppCompatActivity) this).getSupportActionBar().hide();
        username = findViewById(R.id.chat_username);
        profilepic = findViewById(R.id.chat_profilepic);
        recyclerView = findViewById(R.id.chat_recyclerview);
        editText = findViewById(R.id.chat_edittext);
        send = findViewById(R.id.chat_sendbtn);
        firebaseDatabase = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        firstuser = intent.getStringExtra("firstuser");
        seconduser = intent.getStringExtra("seconduser");
        chatid = intent.getStringExtra("chatid");

        setUsernameAndProfile();
        try {

            recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

            FirebaseRecyclerOptions<UserListModel> options = new FirebaseRecyclerOptions.Builder<UserListModel>()
                    .setQuery(FirebaseDatabase.getInstance().getReference()
                                    .child("user_chats")
                            , UserListModel.class).build();
            if (options != null) {
                //userListViewAdapter = new UserListViewAdapter(options, ChatActivity.this);
                //users_recycleview.setAdapter(userListViewAdapter);
            } else {
                Toast.makeText(ChatActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(ChatActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //ahiya lakh ok
//                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("user_chatswith").child("gGH6i4QwE5ZvwHYKvQgOq7jMVZo2/chatwith/pM8GeBSDglP1qYO84KHRtmqdBJz1");
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        users=snapshot.getValue(UserListModel.class);
//                        Toast.makeText(ChatActivity.this, users.getChatid()+"users.get", Toast.LENGTH_SHORT).show();
//                        //karijo run ok
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //Toast.makeText(ChatActivity.this, "on click call", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ChatActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        displayMessages();


    }//oncreate

//    private void checkExistingChat() {
//
//        user_chatswithReference = firebaseDatabase.getReference().child("user_chatswith").child(firstuser).child("chatwith").child(seconduser);
//        user_chatswithReference1 = firebaseDatabase.getReference().child("user_chatswith");
//
//
//        user_chatswithReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                try {
//                    if(snapshot.exists()){
//                        //Toast.makeText(ChatActivity.this, "in set", Toast.LENGTH_SHORT).show();
//                        users = snapshot.getValue(UserListModel.class);
//                        //Toast.makeText(ChatActivity.this,"chat id: "+users.getChatid()+" userid: "+users.getUserid(), Toast.LENGTH_SHORT).show();
//                        if(users.getChatid() != null){
//                            //Toast.makeText(ChatActivity.this, "in getchatid", Toast.LENGTH_SHORT).show();
//                            chatid = users.getChatid().toString();
//                        }else{
//                            //Toast.makeText(ChatActivity.this, "I Data Not found", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }else {
//                        chatid = user_chatswithReference.push().getKey();
//                        UserListModel  users1 = new UserListModel();
//                        users1.setChatid(chatid);
//                        users1.setUserid(seconduser);
//                        user_chatswithReference1.child(firstuser).child("chatwith").child(seconduser).setValue(users1);
//
//                        UserListModel  users2 = new UserListModel();
//                        users2.setChatid(chatid);
//                        users2.setUserid(firstuser);
//                        user_chatswithReference1.child(seconduser).child("chatwith").child(firstuser).setValue(users2);
//                    }
//                }catch (Exception e){
//                    Toast.makeText(ChatActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ChatActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//    }//end function

    private void displayMessages() {
        //Toast.makeText(this, "chatid: "+chatid, Toast.LENGTH_SHORT).show();
        if (!chatid.equals("1")) {
            //to set profile pic with is stored in profileiges in database
            try {

                recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                FirebaseRecyclerOptions<ChatModel> options = new FirebaseRecyclerOptions.Builder<ChatModel>().
                        setQuery(FirebaseDatabase.getInstance().getReference().child("user_chats").child(chatid),
                                ChatModel.class).build();
                messageViewAdapter = new MessageViewAdapter(options, ChatActivity.this);
                recyclerView.setAdapter(messageViewAdapter);

            } catch (Exception e) {
                Toast.makeText(ChatActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }


    }

    private void setUsernameAndProfile() {
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
    }//end function


    @Override
    public void onStart() {
        super.onStart();
        //Toast.makeText(this, "Chatid ad: "+chatid, Toast.LENGTH_SHORT).show();
        if (!chatid.equals("1")) {
            //Toast.makeText(this, "Adapter Start", Toast.LENGTH_SHORT).show();
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
}//class