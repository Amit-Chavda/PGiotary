package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.dhruv.pg_accomodation.map_oprations.LocationProvider;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import static com.example.dhruv.pg_accomodation.BitMapUtility.StringToBitMap;

public class ViewPostActivity extends AppCompatActivity {

    private LocationProvider location;

    private CircleImageView userProfileImageView;
    private MaterialTextView usernameTextView;
    private ShapeableImageView postImageView;
    private MaterialTextView postStatusTextView;
    private MaterialTextView postTypeTextView;
    private MaterialTextView postAddressTextView;
    private MaterialTextView postDescriptionTextView;
    private MaterialTextView postFacilityTextView;
    private AppCompatRatingBar ratingBar;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        userProfileImageView = findViewById(R.id.user_profile_imageView);
        postDescriptionTextView=findViewById(R.id.post_description_textView);
        usernameTextView = findViewById(R.id.username_textView);
        postImageView = findViewById(R.id.post_image_imageView);
        postStatusTextView = findViewById(R.id.post_status_textView);
        postTypeTextView = findViewById(R.id.post_type_textView);
        postAddressTextView = findViewById(R.id.post_address_textView);
        postFacilityTextView = findViewById(R.id.post_facility1_textView);
        ratingBar = findViewById(R.id.post_ratings_ratingBar);

        Intent intent = getIntent();
        final String postid = intent.getStringExtra("postId");
        String publisherid = intent.getStringExtra("ownerId");


        //set profile pic and username
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(publisherid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    usernameTextView.setText(user.getUsername());
                    Bitmap bitmap;
                    bitmap = StringToBitMap(user.getProfileImage());
                    userProfileImageView.setImageBitmap(bitmap);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewPostActivity.this, error.getMessage()+"", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
        }


        //load data from database
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    post = snapshot.getValue(Post.class);
                    if (post != null) {
                        postImageView.setImageBitmap(StringToBitMap(post.getPostImage()));
                        postTypeTextView.setText(post.getPostType());
                        postStatusTextView.setText(post.getPostStatus());
                        ratingBar.setRating(Float.parseFloat(post.getPostRatings()));
                        postAddressTextView.setText(post.getPostAddress());
                        postDescriptionTextView.setText(post.getPostDescription());
                        postFacilityTextView.setText(post.getFacility());// returns array
                    } else {
                        Toast.makeText(ViewPostActivity.this, "Loading data failed.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewPostActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }

        //view address on map
        postAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //post = snapshot.getValue(Post.class);
                        String lat = post.getLat();
                        String longi = post.getLongi();
                        Toast.makeText(getApplicationContext(), "on click", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), post.getLat() + " and " + post.getLongi(), Toast.LENGTH_SHORT).show();
                        Location currentlocation = location.getLocation(ViewPostActivity.this);
                        Toast.makeText(ViewPostActivity.this, "current location: " + currentlocation, Toast.LENGTH_SHORT).show();
                        try {
                            if (currentlocation != null) {
                                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)",
                                        currentlocation.getLatitude(), currentlocation.getLongitude(), "Source"
                                        , Double.parseDouble(lat), Double.parseDouble(longi), "Destination");
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);
                            } else {
                                Toast.makeText(ViewPostActivity.this, "Move mobile and try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ViewPostActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ViewPostActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }//oncreate
}//class