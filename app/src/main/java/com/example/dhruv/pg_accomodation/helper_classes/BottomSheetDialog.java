package com.example.dhruv.pg_accomodation.helper_classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.actvities.CallActivity;
import com.example.dhruv.pg_accomodation.actvities.ChatActivity;
import com.example.dhruv.pg_accomodation.call_support.BaseActivity;
import com.example.dhruv.pg_accomodation.call_support.CallScreenActivity;
import com.example.dhruv.pg_accomodation.call_support.LoginActivity;
import com.example.dhruv.pg_accomodation.call_support.PlaceCallActivity;
import com.example.dhruv.pg_accomodation.call_support.SinchService;
import com.example.dhruv.pg_accomodation.models.Post;
import com.example.dhruv.pg_accomodation.models.UserListModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private String chatId;
    private String currentUserId;
    private String ownerId;
    private String recepientName;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String callerName;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout,
                container, false);

        Button callButton = v.findViewById(R.id.call_button);
        Button chatButton = v.findViewById(R.id.chat_button);
        firebaseDatabase = FirebaseDatabase.getInstance();

        Bundle bundle = getArguments();
        currentUserId = bundle.getString("firstuser", "");
        ownerId = bundle.getString("seconduser", "");
        chatId = bundle.getString("chatId", "");
        recepientName = bundle.getString("recepientName", "");
        callerName = bundle.getString("callerName");

        callButton.setOnClickListener(v1 -> {
            processCall();
            dismiss();
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processChat();
                //dismiss();
            }
        });

        return v;
    }

    private void processCall() {
        Intent intent = new Intent(getContext(), PlaceCallActivity.class);
        intent.putExtra("recepientID", ownerId);
        intent.putExtra("recepientName", recepientName);
        intent.putExtra("callerName", callerName);
        startActivity(intent);
    }

    private void setChatId(String s) {
        chatId = s;
    }

    private void processChat() {
        Context context=getContext();
        databaseReference = firebaseDatabase.getReference().child("user_chatswith").child(currentUserId).child("chatwith").child(ownerId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    UserListModel user = snapshot.getValue(UserListModel.class);
                    setChatId(user.getChatid());
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("firstuser", currentUserId);
                    intent.putExtra("seconduser", ownerId);
                    intent.putExtra("chatid", chatId + "");
                    startActivity(intent);
                } else {
                    DatabaseReference user_chatswithReference = firebaseDatabase.getReference().child("user_chatswith").child(currentUserId).child("chatwith").child(ownerId);
                    DatabaseReference user_chatswithReference1 = firebaseDatabase.getReference().child("user_chatswith");

                    chatId = user_chatswithReference.push().getKey();
                    UserListModel users1 = new UserListModel();
                    users1.setChatid(chatId);
                    users1.setUserid(ownerId);
                    user_chatswithReference1.child(currentUserId).child("chatwith").child(ownerId).setValue(users1);

                    UserListModel users2 = new UserListModel();
                    users2.setChatid(chatId);
                    users2.setUserid(currentUserId);
                    user_chatswithReference1.child(ownerId).child("chatwith").child(currentUserId).setValue(users2);
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("firstuser", currentUserId);
                    intent.putExtra("seconduser", ownerId);
                    intent.putExtra("chatid", chatId + "");
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}