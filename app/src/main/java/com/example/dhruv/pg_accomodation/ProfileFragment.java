package com.example.dhruv.pg_accomodation;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import com.example.dhruv.pg_accomodation.profile_RecycleView.RecyclerViewAdapater;
import com.example.dhruv.pg_accomodation.profile_RecycleView.RecyclerViewAdapater;
import com.example.dhruv.pg_accomodation.profile_RecycleView.Recycleview_post;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;


    public class ProfileFragment extends Fragment {
    Context context;
    TextView username,emailid,editbtn;
    ImageView profileimg;
    Uri imageuri;
    String myUrl="";
    StorageTask uploadtask;

    public RecyclerViewAdapater postAdapter;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private String type;
    private RecyclerView recyclerView1;
    String currentFirebaseUser;



    public ProfileFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //inisialization
        username = view.findViewById(R.id.profile_username);
        emailid = view.findViewById(R.id.profile_emailid);
        editbtn = view.findViewById(R.id.profile_Editbtn);
        profileimg = view.findViewById(R.id.profile_profileimg);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        recyclerView1 =view.findViewById(R.id.recycleview1);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();





        //to set profile pic with is stored in profileiges in database
        try{

            recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
            FirebaseRecyclerOptions<Recycleview_post> options = new FirebaseRecyclerOptions.Builder<Recycleview_post>().
                    setQuery(FirebaseDatabase.getInstance().getReference().child("Posts").orderByChild("publisher").equalTo(currentFirebaseUser),Recycleview_post.class).build();


            postAdapter = new RecyclerViewAdapater(options , getContext());
            recyclerView1.setAdapter(postAdapter);

        }catch (Exception e){
            Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }






        // this code is to set username and email id in profile

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(currentFirebaseUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.name);
                emailid.setText(user.emailid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // this code is to set username and email id in profile end





        //upload ptofil
        try{
            DatabaseReference df = FirebaseDatabase.getInstance().getReference("profileiges").child(currentFirebaseUser);
            df.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        User user = snapshot.getValue(User.class);
                        if(user.getProfileimg()!=""){
                            Glide.with(profileimg.getContext()).load(user.getProfileimg()).into(profileimg);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (Exception e){
            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //to set profile pic end




        // go to local Directory to get the profile pic
        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);
            }
        });

        return view;
    }//oncreateview







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
        if(requestCode==1 && resultCode==RESULT_OK && data != null && data.getData() != null){
            imageuri = data.getData();
            profileimg.setImageURI(imageuri);
            uploadPicture();
        }
    }//onactivity



    // to uplod image to of profile.
    private void uploadPicture() {
        try{
            final ProgressDialog pd = new ProgressDialog(getContext());

            ContentResolver contentResolver = getActivity().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            try{
                this.type = mime.getExtensionFromMimeType(contentResolver.getType(imageuri));

            }catch (Exception e){
                Toast.makeText(getContext(), "type: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            final StorageReference riversRef = storageReference.child(System.currentTimeMillis()
                    +"."+type);

            pd.setTitle("Uploading Image...");
            pd.show();
            uploadtask = riversRef.putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();

                            Toast.makeText(getContext(), "File uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pd.dismiss();
                            Toast.makeText(getContext(), "Error while file uploading "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isComplete()){
                        throw task.getException();
                    }
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloaduri = task.getResult();
                    myUrl = downloaduri.toString();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("profileiges");
                    String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("profileimg",myUrl);
                    databaseReference.child(currentFirebaseUser).setValue(hashMap);
                }//oncompletlisner
            });

        }catch (Exception e){
            Toast.makeText(getContext(), "Error: "+e, Toast.LENGTH_LONG).show();
        }
    }//end of upload function



}//fregment class