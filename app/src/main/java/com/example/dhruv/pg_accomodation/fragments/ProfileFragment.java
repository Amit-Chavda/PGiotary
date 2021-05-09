package com.example.dhruv.pg_accomodation.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.helper_classes.ValidationUtility;
import com.example.dhruv.pg_accomodation.models.Post;
import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.models.UserModel;
import com.example.dhruv.pg_accomodation.adapters.ProfilePostAdapater;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {


    private Context context;
    private TextView usernameTextView;
    private TextView emailTextView;
    private MaterialTextView mobileTextView;
    private TextView changeProfilePic;
    private ImageView profileImageView;

    private Uri imageUri;
    private ProfilePostAdapater postAdapter;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private RecyclerView postRecyclerView;
    private String currentFirebaseUser;


    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        //inisialization
        usernameTextView = view.findViewById(R.id.profile_username);
        emailTextView = view.findViewById(R.id.profile_emailid);
        mobileTextView = view.findViewById(R.id.proflie_mobile_no_textView);
        profileImageView = view.findViewById(R.id.profile_pic);
        changeProfilePic = view.findViewById(R.id.change_profile_pic_textview);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        postRecyclerView = view.findViewById(R.id.post_recycleview);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        context = getContext();


        //set posts uploaded by current user
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(FirebaseDatabase.getInstance().getReference()
                .child("Posts").orderByChild("postOwner").equalTo(currentFirebaseUser), Post.class).build();
        postAdapter = new ProfilePostAdapater(options, getContext());
        postRecyclerView.setAdapter(postAdapter);

        // this code is to set user profile info
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(currentFirebaseUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                usernameTextView.setText(user.getUsername());
                emailTextView.setText(user.getEmail());
                mobileTextView.setText(user.getMobileNumber());
                Glide.with(profileImageView.getContext()).load(user.getProfileImage()).into(profileImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        changeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createImageChooser();
            }
        });
        return view;
    }

    private void createImageChooser() {
        if (ValidationUtility.isInternetAvailable(getContext())) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(context, "You are offline!", Toast.LENGTH_SHORT).show();
        }
    }

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(context).load(imageUri).into(profileImageView);
            uploadPicture();
        }
    }



    private void uploadPicture() {
        if (imageUri == null) {
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        //creating path and file name to store image
        StorageReference postImageRef = storageReference.child("user-profiles").child(FirebaseAuth.getInstance().getUid()).child(System.currentTimeMillis()
                + "." + mime.getExtensionFromMimeType(contentResolver.getType(imageUri)));
        postImageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Toast.makeText(context, uri.toString() + "", Toast.LENGTH_SHORT).show();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getUid()).child("profileImage");
                                databaseReference.setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Profile picture updated.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}