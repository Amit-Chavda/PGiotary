package com.example.dhruv.pg_accomodation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.content.res.ResourcesCompat;

import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.map_oprations.LocationProvider;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.dhruv.pg_accomodation.BitMapUtility.StringToBitMap;
import static com.example.dhruv.pg_accomodation.R.drawable.vector;

public class ViewPostActivity extends AppCompatActivity {

    private ImageButton viewInMapBtn;
    private LinearLayout linearLayout;
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


    //new code
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        viewInMapBtn = findViewById(R.id.view_in_map_btn);
        userProfileImageView = findViewById(R.id.user_profile_imageView);
        postDescriptionTextView = findViewById(R.id.post_description_textView);
        usernameTextView = findViewById(R.id.username_textView);
        postImageView = findViewById(R.id.post_image_imageView);
        postStatusTextView = findViewById(R.id.post_status_textView);
        postTypeTextView = findViewById(R.id.post_type_textView);
        postAddressTextView = findViewById(R.id.post_address_textView);
        postFacilityTextView = findViewById(R.id.post_facility1_textView);
        ratingBar = findViewById(R.id.post_ratings_ratingBar);
/*
        linearLayout=findViewById(R.id.linear_layout);

        LinearLayout parent = new LinearLayout(ViewPostActivity.this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 0);
        parent.setLayoutParams(params);
        parent.setOrientation(LinearLayout.HORIZONTAL);


//children of parent linearlayout

        ImageView iconImageView = new ImageView(ViewPostActivity.this);
        iconImageView.setImageResource(vector);
        //iconImageView.setLayoutParams(params);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iconImageView.setLayoutParams(layoutParams);



        TextView tv = new TextView(ViewPostActivity.this);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        tv.setLayoutParams(params);
        tv.setLayoutParams(layoutParams);
        tv.setText("This is text");
        tv.setTextSize(R.dimen.btn_textsize);

        parent.addView(iconImageView);
        parent.addView(tv);

        linearLayout.addView(parent);
        */

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
                    usernameTextView.setText("@" + user.getUsername().toLowerCase());
                    Glide.with(ViewPostActivity.this).load(user.getProfileImage()).into(userProfileImageView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewPostActivity.this, error.getMessage() + "", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }


        //load data from database
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    post = snapshot.getValue(Post.class);
                    if (post != null) {
                        postTypeTextView.setText(post.getPostType());
                        postStatusTextView.setText(post.getPostStatus());
                        ratingBar.setRating(Float.parseFloat(post.getPostRatings()));
                        postAddressTextView.setText(post.getPostAddress());
                        postDescriptionTextView.setText(post.getPostDescription());
                        Glide.with(ViewPostActivity.this).load(post.getPostImage()).into(postImageView);
                        postFacilityTextView.setText(post.getFacility());
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

        //view address in map
        viewInMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        post = snapshot.getValue(Post.class);
                        String lat = post.getLat();
                        String longi = post.getLongi();
                        LocationTracker currentLocation = new LocationTracker(ViewPostActivity.this);
                        //Toast.makeText(ViewPostActivity.this, "current location: " + currentLocation, Toast.LENGTH_SHORT).show();
                        try {
                            if (currentLocation.canGetLocation()) {
                                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)",
                                        currentLocation.getLatitude(), currentLocation.getLongitude(), "Source"
                                        , Double.parseDouble(lat), Double.parseDouble(longi), "Destination");
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);
                            } else {
                                currentLocation.showSettingsAlert();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ViewPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ViewPostActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        //new code
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }

    //check needed permisi=sion allowed or not
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for this service. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            break;
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ViewPostActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }//oncreate
}//class