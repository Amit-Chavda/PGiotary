package com.example.dhruv.pg_accomodation.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.actvities.ViewPostActivity;
import com.example.dhruv.pg_accomodation.models.Post;
import com.example.dhruv.pg_accomodation.models.UserModel;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter2 extends RecyclerView.Adapter<PostAdapter2.ViewHolder2> {

    private Context context;
    private ArrayList<Post> posts;
    private boolean isLiked;

    public PostAdapter2(Context context,ArrayList<Post> posts){
        this.context=context;
        this.posts=posts;
    }
    @NonNull
    @Override
    public ViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        ViewHolder2 viewHolder = new ViewHolder2(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder2 holder, int position) {
        preparePostItem(posts.get(position),holder);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void preparePostItem(final Post model, final ViewHolder2 holder) {
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
                            if (isLiked) {
                                if (snapshot.child(postId).hasChild(currentUserId)) {
                                    likeRef.child(postId).child(currentUserId).removeValue();
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


     class ViewHolder2 extends RecyclerView.ViewHolder {

        private final ImageView postImageView;
        private final CircleImageView profileImageView;
        private final MaterialTextView usernameTextView;
         private final MaterialTextView postTypeTextView;
         private final MaterialTextView postStatustextView;
         private final MaterialTextView postCityTextView;

        private final ImageView likeBtn;
        private final TextView likesTextView;

        public ViewHolder2(@NotNull View itemView) {
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
                        likeBtn.setColorFilter(Color.argb(255, 255, 0, 0));
                    } else {
                        int likeCount = (int) snapshot.child(pid).getChildrenCount();
                        likesTextView.setText(likeCount + " likes");
                        likeBtn.setImageResource(R.drawable.ic_baseline_unlike_empty_heart);
                        likeBtn.setColorFilter(Color.argb(255, 0, 0, 0));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, error.getMessage()+"", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}//post adapter class
