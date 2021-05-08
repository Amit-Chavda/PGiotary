package com.example.dhruv.pg_accomodation.actvities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.dhruv.pg_accomodation.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.helper_classes.BottomSheetDialog;
import com.example.dhruv.pg_accomodation.helper_classes.LocationTracker;
import com.example.dhruv.pg_accomodation.models.Post;
import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.models.UserModel;
import com.example.dhruv.pg_accomodation.models.UserListModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ViewPostActivity2 extends AppCompatActivity {

    private ImageButton viewInMapBtn;
    private CircleImageView userProfileImageView;
    private MaterialTextView usernameTextView;
    private ShapeableImageView postImageView;
    private MaterialTextView postStatusTextView;
    private MaterialTextView postTypeTextView;
    private MaterialTextView postAddressTextView;
    private MaterialTextView postDescriptionTextView;
    private MaterialTextView postFacilityTextView;
    private Post post;
    private String postId;
    private String ownerId;


    //new code
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private TextView likesTextView;
    private final static int ALL_PERMISSIONS_RESULT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post2);

        //initalization
        viewInMapBtn = findViewById(R.id.view_in_map_btn);
        userProfileImageView = findViewById(R.id.user_profile_imageView);
        postDescriptionTextView = findViewById(R.id.post_description_textView);
        usernameTextView = findViewById(R.id.username_textView);
        postImageView = findViewById(R.id.post_image_imageView);
        postStatusTextView = findViewById(R.id.post_status_textView);
        postTypeTextView = findViewById(R.id.post_type_textView);
        postAddressTextView = findViewById(R.id.post_address_textView);
        postFacilityTextView = findViewById(R.id.post_facility1_textView);
        likesTextView = findViewById(R.id.likes_textview);


        //hide action bar
        this.getSupportActionBar().hide();

        //get oist id and owner id from parent activity
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        ownerId = intent.getStringExtra("ownerId");


        //set up post info
        initializePostContent(postId, ownerId);
        //set like btn status
        getLikeBtnStatus(postId, FirebaseAuth.getInstance().getUid());

        //view address in map
        viewInMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAddressInMap(postId);
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

    }//oncreate

    private void loadAddressInMap(String postId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post = snapshot.getValue(Post.class);
                String lat = post.getLat();
                String longi = post.getLongi();
                LocationTracker currentLocation = new LocationTracker(ViewPostActivity2.this);
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
                    Toast.makeText(ViewPostActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewPostActivity2.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializePostContent(String postId, String ownerId) {

        //set profile pic and username
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(ownerId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    usernameTextView.setText("@" + user.getUsername().toLowerCase());
                    Glide.with(ViewPostActivity2.this).load(user.getProfileImage()).into(userProfileImageView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewPostActivity2.this, error.getMessage() + "", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }


        //load data from database
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    post = snapshot.getValue(Post.class);
                    if (post != null) {
                        postTypeTextView.setText(post.getPostType());
                        postStatusTextView.setText(post.getPostStatus());
                        postAddressTextView.setText(post.getPostAddress());
                        postDescriptionTextView.setText(post.getPostDescription());
                        Glide.with(ViewPostActivity2.this).load(post.getPostImage()).into(postImageView);
                        postFacilityTextView.setText(post.getFacility());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewPostActivity2.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }
    }


    public void getLikeBtnStatus(final String pid, final String uid) {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int likeCount = (int) snapshot.child(pid).getChildrenCount();
                likesTextView.setText(likeCount + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewPostActivity2.this, error.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
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
        new AlertDialog.Builder(ViewPostActivity2.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}