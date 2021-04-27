package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class View_post extends AppCompatActivity {

    public ImageView post_image;
    public CircleImageView profile_image;
    public TextView username,title,description,address,rent,publisher;
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

        Intent intent = getIntent();
        String postid = intent.getStringExtra("Postid");
        String publisherid = intent.getStringExtra("Publisherid");


        //set username
        try{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(publisherid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    username.setText(user.name);
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



        //set profile img
        try{

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
        }





    }//oncreate
}//class