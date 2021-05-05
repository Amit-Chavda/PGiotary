package com.example.dhruv.pg_accomodation.actvities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class ViewPostActivity extends AppCompatActivity {

    private ImageButton viewInMapBtn;
    private MaterialButton contect;
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
    private String currentFirebaseUser;
    private String chatid;
    private String postid;
    private String publisherid;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    //new code
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private ImageView likeBtn;
    private TextView likesTextView;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private boolean isLiked;

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
        likeBtn = findViewById(R.id.like_btn_postitem);
        likesTextView = findViewById(R.id.likes_textview);
        contect = findViewById(R.id.btn_cotact);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();


//remove action bar
        ((AppCompatActivity) this).getSupportActionBar().hide();


        Intent intent = getIntent();
        final String postid = intent.getStringExtra("postId");
        this.postid = postid;
        final String ownerId = intent.getStringExtra("ownerId");
        publisherid = ownerId;

        //set profile pic and username
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(ownerId);
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


        contect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getChatId();
                    Intent intent = new Intent(ViewPostActivity.this, ChatActivity.class);
                    intent.putExtra("firstuser", currentFirebaseUser);
                    intent.putExtra("seconduser", publisherid);
                    intent.putExtra("chatid", chatid + "");

                    Toast.makeText(ViewPostActivity.this, "chatid: " + chatid, Toast.LENGTH_SHORT).show();

                    if (chatid.equals(null)) {
                        Toast.makeText(ViewPostActivity.this, "try again", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Toast.makeText(ViewPostActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }//onclick
        });



        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLiked = true;
                final DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("likes");
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (isLiked == true) {
                            if (snapshot.child(postid).hasChild(ownerId)) {
                                likeRef.child(postid).removeValue();
                                isLiked = false;
                            } else {
                                likeRef.child(postid).child(ownerId).setValue(true);
                                isLiked = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ViewPostActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }//oncreate

    public void getLikeBtnStatus(final String pid, final String uid) {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(pid).hasChild(uid)) {
                    int likeCount = (int) snapshot.child(pid).getChildrenCount();
                    likesTextView.setText(likeCount + " likes");
                    likeBtn.setImageResource(R.drawable.ic_baseline_like_filled_heart);
                } else {
                    int likeCount = (int) snapshot.child(pid).getChildrenCount();
                    likesTextView.setText(likeCount + " likes");
                    likeBtn.setImageResource(R.drawable.ic_baseline_unlike_empty_heart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getChatId() {
        try {

            databaseReference = firebaseDatabase.getReference().child("user_chatswith").child(currentFirebaseUser).child("chatwith").child(publisherid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        UserListModel user = snapshot.getValue(UserListModel.class);
                        setChatId(user.getChatid());
                    } else {
                        DatabaseReference user_chatswithReference = firebaseDatabase.getReference().child("user_chatswith").child(currentFirebaseUser).child("chatwith").child(publisherid);
                        DatabaseReference user_chatswithReference1 = firebaseDatabase.getReference().child("user_chatswith");

                        chatid = user_chatswithReference.push().getKey();
                        UserListModel users1 = new UserListModel();
                        users1.setChatid(chatid);
                        users1.setUserid(publisherid);
                        user_chatswithReference1.child(currentFirebaseUser).child("chatwith").child(publisherid).setValue(users1);

                        UserListModel users2 = new UserListModel();
                        users2.setChatid(chatid);
                        users2.setUserid(currentFirebaseUser);
                        user_chatswithReference1.child(publisherid).child("chatwith").child(currentFirebaseUser).setValue(users2);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewPostActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }//getchatid

    private void setChatId(String s) {
        chatid=s;
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

    }


}//class