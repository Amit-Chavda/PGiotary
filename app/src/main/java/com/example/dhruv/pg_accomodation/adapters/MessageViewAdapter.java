package com.example.dhruv.pg_accomodation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.models.ChatModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;

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
    protected void onBindViewHolder(@NonNull final MessageViewAdapter.ViewHolder holder, int position, @NonNull final ChatModel model) {

        holder.message.setText(model.getMessage());

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

        // return super.getItemViewType(position);


    }
}//class
