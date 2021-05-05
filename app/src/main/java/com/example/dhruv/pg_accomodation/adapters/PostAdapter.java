package com.example.dhruv.pg_accomodation.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.actvities.ViewPostActivity;
import com.example.dhruv.pg_accomodation.models.Post;
import com.example.dhruv.pg_accomodation.models.UserModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;


public class PostAdapter extends FirebaseRecyclerAdapter<Post, PostAdapter.ViewHolder> {
    public Context context;
    private boolean isLiked;
    private int postCount;

    public PostAdapter(@NonNull FirebaseRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        postCount=0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Post post) {
                preparePostItem(post, holder);
    }



    private void preparePostItem(final Post model, final ViewHolder holder) {
        if (model != null) {
            //set username
            try {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(model.getPostOwner());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //set user details
                        UserModel user = snapshot.getValue(UserModel.class);
                        if (user != null) {
                            holder.usernameTextView.setText("@" + user.getUsername().toLowerCase());
                            Glide.with(context.getApplicationContext()).load(user.getProfileImage()).into(holder.profileImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }

            //set post details
            holder.postTypeTextView.setText(model.getPostType());
            holder.postStatustextView.setText(model.getPostStatus());
            holder.postCityTextView.setText(" in " + model.getPostCity());

            //get btn status
            final Post post = model;
            //Toast.makeText(context, position+"", Toast.LENGTH_SHORT).show();
            final String postId = post.getPostId();
            final String currentUserId = FirebaseAuth.getInstance().getUid();


            holder.getLikeBtnStatus(postId, currentUserId);
            //post img
            Glide.with(holder.postImageView.getContext()).load(post.getPostImage()).placeholder(R.drawable.ic_launcher_background).dontAnimate().into(holder.postImageView);


            holder.postImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewPostActivity.class);
                    intent.putExtra("postId", post.getPostId() + "");
                    intent.putExtra("ownerId", post.getPostOwner() + "");
                    context.startActivity(intent);
                }
            });

            holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isLiked = true;
                    final DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("likes");
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (isLiked == true) {
                                if (snapshot.child(postId).hasChild(currentUserId)) {
                                    likeRef.child(postId).removeValue();
                                    isLiked = false;
                                } else {
                                    likeRef.child(postId).child(currentUserId).setValue(true);
                                    isLiked = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }

    }

    private int getPostCount() {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Posts");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setLocalVar((int) snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage()+"", Toast.LENGTH_SHORT).show();
            }
        });
        return postCount;
    }

    private void setLocalVar(int var){
         postCount=var;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView postImageView;
        private CircleImageView profileImageView;
        private MaterialTextView usernameTextView, postTypeTextView, postStatustextView, postCityTextView;

        private ImageView likeBtn;
        private TextView likesTextView;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.user_profile_imageView);
            postImageView = itemView.findViewById(R.id.post_image_imageView_postItem);
            usernameTextView = itemView.findViewById(R.id.username_textView);
            postStatustextView = itemView.findViewById(R.id.post_status_textView);
            postTypeTextView = itemView.findViewById(R.id.post_type_textView);
            postCityTextView = itemView.findViewById(R.id.city_textview_postitem);
            likeBtn = itemView.findViewById(R.id.like_btn_postitem);
            likesTextView = itemView.findViewById(R.id.likes_textview);
        }

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
    }
}//post adapter class
