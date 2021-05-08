package com.example.dhruv.pg_accomodation.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.actvities.ChatActivity;
import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.actvities.ViewPostActivity;
import com.example.dhruv.pg_accomodation.models.UserListModel;
import com.example.dhruv.pg_accomodation.models.UserModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserListAdapter extends FirebaseRecyclerAdapter<UserListModel, ChatUserListAdapter.ViewHolder> {
    public Context context;
    String currentFirebaseUser;
    String chatid;

    public ChatUserListAdapter(@NonNull FirebaseRecyclerOptions<UserListModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final UserListModel model) {
        if(model != null){
            //set username
            try {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(model.getUserid());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //set user details
                        UserModel user = snapshot.getValue(UserModel.class);
                        if (user!=null) {
                            holder.username.setText(user.getUsername().toLowerCase());
                            Glide.with(context).load(user.getProfileImage()).into(holder.profileimg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.userlistitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("firstuser",currentFirebaseUser);
                        intent.putExtra("seconduser",model.getUserid());
                        intent.putExtra("chatid",model.getChatid());
                        context.startActivity(intent);
                    }
                });

                /*

                This is code for delating chat

                */

                holder.userlistitem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user_chatswith")
                                .child(currentFirebaseUser).child("chatwith").child(model.getUserid());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try{

                                    if (snapshot.hasChildren()) {
                                        UserListModel user = snapshot.getValue(UserListModel.class);
                                        chatid = user.getChatid();
                                        //Toast.makeText(context, "Chatid: "+chatid, Toast.LENGTH_SHORT).show();
                                    }

                                }catch (Exception e){
                                    Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete Chat");
                        builder.setMessage("Are you sure you want to delete this Chat?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(!(chatid == null)){
                                    deletchat(chatid, model.getUserid());
                                }

                            }//onclick
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();

                        return true;
                    }
                });// delet onlong


            } catch (Exception e) {
                Toast.makeText(context, e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(context, "Data not found", Toast.LENGTH_SHORT).show();
        }

    }//onbind

    public void deletchat(String chatid,String userid){
        DatabaseReference user_chats = FirebaseDatabase.getInstance().getReference().child("user_chats");
        user_chats.child(chatid).removeValue();
        DatabaseReference user_chatswithReference1 = FirebaseDatabase.getInstance().getReference().child("user_chatswith");
        user_chatswithReference1.child(currentFirebaseUser).child("chatwith").child(userid).removeValue();
        user_chatswithReference1.child(userid).child("chatwith").child(currentFirebaseUser).removeValue();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout userlistitem;
        CircleImageView profileimg;
        MaterialTextView username;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            userlistitem = itemView.findViewById(R.id.userlistlinierlayout);
            profileimg = itemView.findViewById(R.id.userlistprofilepic);
            username = itemView.findViewById(R.id.userlistusername);

        }
    }//viewholder



}//class
