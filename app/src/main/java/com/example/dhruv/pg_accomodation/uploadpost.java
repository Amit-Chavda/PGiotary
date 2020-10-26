package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class uploadpost extends AppCompatActivity {
    ImageView himg;
    Uri imageuri;
    TextView post;
    String myUrl="";
    EditText rent,location,description;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    StorageTask uploadtask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpost);
        himg = findViewById(R.id.houseimg);
        post = findViewById(R.id.postbtn);
        rent = findViewById(R.id.rentet);
        location = findViewById(R.id.locationet);
        description = findViewById(R.id.descriptionet);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPicture();
            }
        });



        himg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);

            }
        });
    }//oncreat

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data != null && data.getData() != null){
            imageuri = data.getData();
            himg.setImageURI(imageuri);
        }
    }

    private void uploadPicture() {

        try{
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Uploading Image...");
            pd.show();
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();


            final StorageReference riversRef = storageReference.child(System.currentTimeMillis()
                    +"."+ mime.getExtensionFromMimeType(contentResolver.getType(imageuri)));
            uploadtask = riversRef.putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();

                            Toast.makeText(uploadpost.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pd.dismiss();
                            Toast.makeText(uploadpost.this, "Error while file uploading "+exception.getMessage(), Toast.LENGTH_SHORT).show();
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

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                    final String randomKey = UUID.randomUUID().toString();
                    String postid = databaseReference.push().getKey();
                    String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("postid",postid);
                    hashMap.put("Publisher",currentFirebaseUser);
                    hashMap.put("postimage",myUrl);
                    hashMap.put("description",description.getText().toString());
                    hashMap.put("postrent",rent.getText().toString());
                    hashMap.put("postlocation",location.getText().toString());

                    databaseReference.child(postid).setValue(hashMap);
                    startActivity(new Intent(uploadpost.this,Home.class));

                }//oncompletlisner
            });


        }catch (Exception e){
            Toast.makeText(this, "Error: "+e, Toast.LENGTH_SHORT).show();
        }

    }//end of upload function
}//end of class