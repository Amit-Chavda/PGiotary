package com.example.dhruv.pg_accomodation.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.models.ChatModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewAdapter extends FirebaseRecyclerAdapter<ChatModel, MessageViewAdapter.ViewHolder> {
    public Context context;

    public MessageViewAdapter(@NonNull FirebaseRecyclerOptions<ChatModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == 2){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rightmessageview_item, parent, false);
            return new MessageViewAdapter.ViewHolder(view);

        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leftmessageview_item, parent, false);
            return new MessageViewAdapter.ViewHolder(view);

        }

    }

    @Override
    protected void onBindViewHolder(@NonNull final MessageViewAdapter.ViewHolder holder, final int position, @NonNull final ChatModel model) {

        holder.message.setText(model.getMessage());

        holder.message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(model.getSender().equals(user.getUid())){
                    //Toast.makeText(context, "on long press", Toast.LENGTH_SHORT).show();

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete-Update Message");
                    builder.setMessage("Delete or Update?");

                    builder.setPositiveButton("Delete mesasge", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Delete Message");
                            builder.setMessage("Are you sure you want to delete this Mesage?");

                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                            .child("user_chats").child(model.getChatid()).child(model.getMessageid());

                                    databaseReference.removeValue();

                                }//onclick
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();

                        }//onclick
                    });

                    builder.setNegativeButton("Update message", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Toast.makeText(context, "For update", Toast.LENGTH_SHORT).show();

                            final DialogPlus dialog = DialogPlus.newDialog(context)
                                    .setGravity(Gravity.CENTER)
                                    .setPadding(15,15,15,15)
                                    .setMargin(50, 0, 50, 0)
                                    .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.message_update))
                                    .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                                    .create();
                            final View postholder = dialog.getHolderView();
                            final EditText msg = postholder.findViewById(R.id.upmessage);
                            MaterialButton updatbtn = postholder.findViewById(R.id.mumessageupdatebtn);
                            msg.setText(model.getMessage());
                            updatbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String upmsg = msg.getText().toString();
                                    model.setMessage(upmsg);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("chatid", model.getChatid());
                                    map.put("messageid", model.getMessageid());
                                    map.put("message", upmsg);
                                    map.put("sender", model.getSender());
                                    map.put("receiver", model.getReceiver());
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                            .child("user_chats").child(model.getChatid()).child(model.getMessageid());
                                    databaseReference.updateChildren(map);
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    });

                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                return true;
            }
        });

    }//onbind


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView message;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.showmessage);


        }
    }//viewholder


    @Override
    public int getItemViewType(int position) {

        ChatModel model = getItem(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(model.getSender().equals(user.getUid())){
            return 1;
        }else {

            return 2;
        }
    }

}//class
