package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dhruv.pg_accomodation.map_oprations.LocationProvider;
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
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class uploadpost extends AppCompatActivity {
    ImageView himg;
    Uri imageuri;
    TextView post;
    Button locationbtn;
    String myUrl="";
    String address="";
    String names[]={"Select House Type","Full House","1BHK","2BHK"};
    EditText rent,description,title,location;
    Spinner spinner_d;
    ArrayAdapter<String>arrayAdapter;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    StorageTask uploadtask;

    //class
    LocationProvider locationProvider;
    Location userlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpost);

        himg = findViewById(R.id.houseimg);
        post = findViewById(R.id.postbtn);
        rent = findViewById(R.id.rentet);
        location = findViewById(R.id.locationet);
        locationbtn = findViewById(R.id.locationbtn);
        description = findViewById(R.id.descriptionet);
        title = findViewById(R.id.titleet);
        spinner_d = findViewById(R.id.spinnerdropdown);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        locationProvider = new LocationProvider();


        //getting location permition
        userGetLocationPermission();


        locationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getting current location of user
                userlocation = locationProvider.getLocation(getApplicationContext());
                //Double lati = userlocation.getLatitude();
                try{
                    if(userlocation != null){
                        Geocoder geocoder = new  Geocoder(uploadpost.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(userlocation.getLatitude(),userlocation.getLongitude(),1);
                        address = addresses.get(0).getAddressLine(0);
                        location.setText(address);
                    }else{
                        Toast.makeText(getApplicationContext(), "Move you mobile and try again", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                }//try catch

            }//onclick
        });




        //house type selection spinner
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,names);
        spinner_d.setAdapter(arrayAdapter);

        //post button to uplaod post
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPicture();
            }
        });


        //open dialog to select the image
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

    //getting location permission
    private void userGetLocationPermission() {
        if (ContextCompat.checkSelfPermission(uploadpost.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(uploadpost.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(uploadpost.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(uploadpost.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(uploadpost.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    //getting image from local storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data != null && data.getData() != null){
            imageuri = data.getData();
            himg.setImageURI(imageuri);
        }
    }

    //process of uploading post
    private void uploadPicture() {

        try{
            //display progrss bar
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




                    //store the data to realtime database
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                    final String randomKey = UUID.randomUUID().toString();
                    String postid = databaseReference.push().getKey();
                    String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("postid",postid);
                    hashMap.put("posttitle",title.getText().toString());
                    hashMap.put("postimage",myUrl);
                    hashMap.put("postdescription",description.getText().toString());
                    hashMap.put("postprice",rent.getText().toString());
                    hashMap.put("postaddress",address);
                    hashMap.put("lat",userlocation.getLatitude()+"");
                    hashMap.put("longi",userlocation.getLongitude()+"");
                    hashMap.put("publisher",currentFirebaseUser);


                    databaseReference.child(postid).setValue(hashMap);
                    startActivity(new Intent(uploadpost.this,Home.class));

                }//oncompletlisner
            });


        }catch (Exception e){
            Toast.makeText(this, "Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }//end of upload function


}//end of class