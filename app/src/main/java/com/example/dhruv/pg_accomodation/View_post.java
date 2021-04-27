package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.map_oprations.LocationProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import static com.example.dhruv.pg_accomodation.BitMapUtility.StringToBitMap;

public class View_post extends AppCompatActivity {

    public ImageView post_image;
    public CircleImageView profile_image;
    public TextView username,title,description,address,rent,publisher;
    LocationProvider location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        profile_image = (CircleImageView)findViewById(R.id.vp_profile_image);
        post_image = findViewById(R.id.vp_post_image);
        username = findViewById(R.id.vp_username);
        title = findViewById(R.id.vp_post_title);
        description =findViewById(R.id.vp_post_description);
        address = findViewById(R.id.vp_addresstv);
        rent = findViewById(R.id.vp_post_rent);
        location = new LocationProvider();

        Intent intent = getIntent();
        final String postid = intent.getStringExtra("Postid");
        String publisherid = intent.getStringExtra("Publisherid");


        //set username
        try{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(publisherid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    username.setText(user.getUsername());
                    Bitmap bitmap;
                    bitmap = StringToBitMap(user.getProfileImage());
                    profile_image.setImageBitmap(bitmap);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }

        try{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Post post = snapshot.getValue(Post.class);
                    title.setText(post.getPosttitle());
                    //post img
                    Glide.with(post_image.getContext()).load(post.getPostimage()).into(post_image);

                    //set post title
                    if(post.getPosttitle().equals("")){
                        title.setVisibility(View.GONE);
                    }else {
                        title.setVisibility(View.VISIBLE);
                        title.setText(post.getPosttitle());
                    }

                    //set description
                    if(post.getPostdescription().equals("")){
                        description.setVisibility(View.GONE);
                    }else {
                        description.setVisibility(View.VISIBLE);
                        description.setText(post.getPostdescription());
                    }

                    //set address
                    if(post.getPostaddress().equals("")){
                        address.setVisibility(View.GONE);
                    }else {
                        address.setVisibility(View.VISIBLE);
                        address.setText(post.getPostaddress());
                    }

                    //set rent
                    if(post.getPostprice().equals("")){
                        rent.setVisibility(View.GONE);
                    }else {
                        rent.setVisibility(View.VISIBLE);
                        rent.setText(post.getPostprice());
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }



        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post = snapshot.getValue(Post.class);
                        String lat = post.getLat();
                        String longi = post.getLongi();
                        Toast.makeText(getApplicationContext(), "on click", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), post.getLat()+" and "+post.getLongi(), Toast.LENGTH_SHORT).show();
                        Location currentlocation = location.getLocation(View_post.this);
                        Toast.makeText(View_post.this, "current location: "+currentlocation, Toast.LENGTH_SHORT).show();
                        try {
                            if(currentlocation!=null){
                                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)",
                                        currentlocation.getLatitude(), currentlocation.getLongitude(), "Source"
                                        ,Double.parseDouble(lat),Double.parseDouble(longi) , "Destination");
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);
                            }else {
                                Toast.makeText(View_post.this, "Move mobile and try again", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(View_post.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(View_post.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });





        //set profile img
        /*try{

            DatabaseReference df = FirebaseDatabase.getInstance().getReference("profileiges").child(publisherid);
            df.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        User user = snapshot.getValue(User.class);
                        if(user.getProfileimg()!=""){
                            Glide.with(profile_image.getContext()).load(user.getProfileimg()).into(profile_image);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/





    }//oncreate
}//class